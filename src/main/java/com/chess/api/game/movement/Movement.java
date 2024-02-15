package com.chess.api.game.movement;

import com.chess.api.game.Board;
import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;
import com.chess.api.game.condition.Conditional;
import com.chess.api.game.condition.PropertyCondition;
import com.chess.api.game.piece.Piece;
import com.chess.api.game.piece.PieceType;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Movement {

    private final Path originalPath;
    private final MovementType type;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean specificQuadrant;
    private final List<Conditional> conditions;
    private final ExtraAction extraAction;
    private final boolean isAttack;
    private final boolean isMove;

    public Movement() {
        this.originalPath = new Path();
        this.type = MovementType.ADVANCE;
        this.mirrorXAxis = false;
        this.mirrorYAxis = false;
        this.specificQuadrant = false;
        this.conditions = null;
        this.extraAction = null;
        this.isAttack = true;
        this.isMove = true;
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis) {
        this(path, type, mirrorXAxis, mirrorYAxis, false, List.of(), null);
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, boolean specificQuadrant,
            List<Conditional> conditions) {
        this(path, type, mirrorXAxis, mirrorYAxis, specificQuadrant, conditions, null);
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, boolean specificQuadrant,
            List<Conditional> conditions, ExtraAction extraAction) {
        this(path, type, true, true, mirrorXAxis, mirrorYAxis, specificQuadrant, conditions, extraAction);
    }

    public Movement(Path path, MovementType type, boolean isAttack, boolean isMove, boolean mirrorXAxis,
            boolean mirrorYAxis, boolean specificQuadrant, List<Conditional> conditions, ExtraAction extraAction) {
        this.originalPath = path;
        this.type = type;
        this.isAttack = isAttack;
        this.isMove = isMove;
        // Determines direction
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        // Determines relativity
        this.specificQuadrant = specificQuadrant;
        // For special actions
        this.conditions = conditions;
        this.extraAction = extraAction;

        if (isMove && !isAttack) {
            this.conditions.add(new PropertyCondition());
        }
    }

    /**
     * Determines the direction that the end vector is relative to the start and builds a path in that direction
     * based off of this movement's path blueprint.
     *
     * @param colour Colour of the Piece, which determines which direction is forward
     * @param start  Location of the piece
     * @param end    Location the piece is requested to move to
     * @return {@link Path}
     */
    public Path getPath(@NonNull Colour colour, @NonNull Vector2D start, @NonNull Vector2D end) {
        // Determine direction
        int diffX = end.getX() - start.getX();
        int diffY = end.getY() - start.getY();
        boolean negX = diffX != 0 && diffX / Math.abs(diffX) == -1;
        boolean negY = diffY != 0 && diffY / Math.abs(diffY) == -1;
        boolean isBlack = Colour.BLACK.equals(colour);

        // Invalid direction cases
        if ((!negY && isBlack && !this.mirrorXAxis)
                || (negY && !isBlack && !this.mirrorXAxis)
                || (negX && !this.mirrorYAxis)) {
            return null;
        }

        List<Vector2D> vectors = new LinkedList<>();
        for (Vector2D vector : this.getOriginalPath()) {
            int nextX = !negX ? vector.getX() + start.getX() : start.getX() - vector.getX();
            int nextY = !negY ? vector.getY() + start.getY() : start.getY() - vector.getY();
            if (!Vector2D.isValid(nextX, nextY)) {
                break;
            }
            vectors.add(new Vector2D(nextX, nextY));
            // Destination has been added, so there is no need to add more
            if (end.getX() == nextX && end.getY() == nextY) {
                break;
            }
        }
        if (vectors.isEmpty() || !end.equals(vectors.get(vectors.size() - 1))) {
            return null; // No path that reaches this end from this start
        }
        return new Path(vectors);
    }

    /**
     * Retrieves all possible vectors that the Piece with this colour at this location can move to.
     *
     * @param colour {@link Colour} representing the colour of the piece this movement is for
     * @param offset {@link Vector2D} representing the position of the piece
     * @return Map of {@link Vector2D}
     */
    public Set<Vector2D> getCoordinates(@NonNull Colour colour, @NonNull Vector2D offset) {
        return this.getCoordinates(colour, offset, null, false, false);
    }

    public Set<Vector2D> getCoordinates(@NonNull Colour colour, @NonNull Vector2D offset, Board board,
            boolean withDefend, boolean ignoreKing) {
        if (this.specificQuadrant) {
            return getVectorsInSpecificQuadrant(offset, colour, board, withDefend, ignoreKing);
        } else {
            return getVectorsInAllQuadrants(offset, colour, board, withDefend, ignoreKing);
        }
    }

    private Set<Vector2D> getVectorsInSpecificQuadrant(@NonNull Vector2D offset, @NonNull Colour colour, Board board,
            boolean withDefend, boolean ignoreKing) {
        boolean isRight = !mirrorYAxis;
        boolean isUp = Colour.WHITE.equals(colour) && !mirrorXAxis || !Colour.WHITE.equals(colour) && mirrorXAxis;

        Set<Vector2D> set = new HashSet<>();
        for (Vector2D vector : this.getOriginalPath()) {
            Vector2D v = getVectorInQuadrant(vector, offset, isRight, isUp);
            if (canMoveInQuadrant(v, colour, board, withDefend, ignoreKing))
                set.add(v);
            if (isBlockedInQuadrant(v, board, ignoreKing))
                break;
        }
        return set;
    }

    private Set<Vector2D> getVectorsInAllQuadrants(@NonNull Vector2D offset, @NonNull Colour colour, Board board,
            boolean withDefend, boolean ignoreKing) {
        boolean blockTopRight = false;
        boolean blockTopLeft = false;
        boolean blockBotRight = false;
        boolean blockBotLeft = false;

        Set<Vector2D> set = new HashSet<>();
        for (Vector2D vector : this.getOriginalPath()) {
            if (mirrorXAxis || Colour.WHITE.equals(colour)) {
                if (!blockTopRight) {
                    Vector2D topRight = getVectorInQuadrant(vector, offset, true, true);
                    if (canMoveInQuadrant(topRight, colour, board, withDefend, ignoreKing))
                        set.add(topRight);
                    blockTopRight = isBlockedInQuadrant(topRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockTopLeft) {
                    Vector2D topLeft = getVectorInQuadrant(vector, offset, false, true);
                    if (canMoveInQuadrant(topLeft, colour, board, withDefend, ignoreKing))
                        set.add(topLeft);
                    blockTopLeft = isBlockedInQuadrant(topLeft, board, ignoreKing);
                }
            }
            if (mirrorXAxis || !Colour.WHITE.equals(colour)) {
                if (!blockBotRight) {
                    Vector2D bottomRight = getVectorInQuadrant(vector, offset, true, false);
                    if (canMoveInQuadrant(bottomRight, colour, board, withDefend, ignoreKing))
                        set.add(bottomRight);
                    blockBotRight = isBlockedInQuadrant(bottomRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockBotLeft) {
                    Vector2D bottomLeft = getVectorInQuadrant(vector, offset, false, false);
                    if (canMoveInQuadrant(bottomLeft, colour, board, withDefend, ignoreKing))
                        set.add(bottomLeft);
                    blockBotLeft = isBlockedInQuadrant(bottomLeft, board, ignoreKing);
                }
            }
        }
        return set;
    }

    private Vector2D getVectorInQuadrant(@NonNull Vector2D vector, @NonNull Vector2D offset, boolean isRight,
            boolean isUp) {
        int x = isRight ? offset.getX() + vector.getX() : offset.getX() - vector.getX();
        int y = isUp ? offset.getY() + vector.getY() : offset.getY() - vector.getY();
        return new Vector2D(x, y);
    }

    private boolean canMoveInQuadrant(@NonNull Vector2D vector, @NonNull Colour colour, Board board,
            boolean withDefend, boolean ignoreKing) {
        if (!vector.isValid()) {
            return false;
        }
        if (board != null) {
            Piece p = board.getPiece(vector);
            if (p != null) {
                return withDefend || !colour.equals(p.getColour()) || PieceType.KING.equals(p.getType()) && ignoreKing;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean isBlockedInQuadrant(@NonNull Vector2D vector, Board board, boolean ignoreKing) {
        if (!vector.isValid() || board == null) {
            return false;
        }
        Piece piece = board.getPiece(vector);
        return piece != null && !(PieceType.KING.equals(piece.getType()) && ignoreKing);
    }

    /**
     * Verifies that all {@link Conditional} defined in this Movement are meeting their criteria.
     *
     * @param board  {@link Board} for the Condition to verify with
     * @param action {@link Action} representing the movement attempted for a Piece at a location to a destination
     * @return true if all Condition pass, otherwise false
     */
    public boolean passesConditions(@NonNull Board board, @NonNull Action action) {
        Piece pStart = board.getPiece(action.start());
        Piece pEnd = board.getPiece(action.end());
        if (!this.isAttack && this.isMove && pEnd != null) {
            return false;
        }
        if (this.isAttack && !this.isMove && (pEnd == null || pStart.getColour().equals(pEnd.getColour()))) {
            return false;
        }
        for (Conditional condition : this.conditions) {
            if (!condition.isExpected(board, action)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Marks all locations valid for this piece to move to, before referencing the board, from origin. This matches
     * the original path of this movement for White pieces.
     *
     * @param colour Colour of the piece, to determine which direction is forward.
     * @return 2D boolean array, true are valid locations
     */
    public boolean[][] drawCoordinates(@NonNull Colour colour) {
        return this.drawCoordinates(colour, new Vector2D(0, 0));
    }

    /**
     * Marks all locations valid for this piece to move to, before referencing the board, from origin. This matches
     * the original path of this movement for White pieces.
     *
     * @param colour Colour of the piece, to determine which direction is forward.
     * @return 2D boolean array, true are valid locations
     */
    public boolean[][] drawCoordinates(@NonNull Colour colour, @NonNull Vector2D offset) {
        Set<Vector2D> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[8][8];
        for (Vector2D c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    @Override
    public String toString() {
        return this.toString(Colour.WHITE, new Vector2D());
    }

    public String toString(@NonNull Colour colour, @NonNull Vector2D offset) {
        boolean[][] boardMove = this.drawCoordinates(colour, offset);
        StringBuilder sb = new StringBuilder();
        for (int y = boardMove[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < boardMove.length; x++) {
                if (x == offset.getX() && y == offset.getY()) {
                    sb.append("| P ");
                } else if (boardMove[x][y]) {
                    sb.append("| o ");
                } else {
                    sb.append("| x ");
                }
                if (x == boardMove.length - 1) {
                    sb.append("|");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
