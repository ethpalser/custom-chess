package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.piece.custom.condition.Conditional;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Movement {

    private final Path pathBase;
    private final MovementType moveType;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean isSpecificQuadrant;
    private final boolean isMove;
    private final boolean isAttack;
    private final List<Conditional> conditions;
    private final ExtraAction extraAction;

    public boolean isMove() {
        return this.isMove;
    }

    public boolean isAttack() {
        return this.isAttack;
    }

    public static class Builder {
        // required
        private final Path path;
        private final MovementType moveType;
        // optional
        private boolean mirrorXAxis = true;
        private boolean mirrorYAxis = true;
        private boolean isSpecificQuadrant = false;
        private boolean isAttack = true;
        private boolean isMove = true;
        private List<Conditional> conditions = List.of();
        private ExtraAction extraAction = null;

        public Builder(Path path, MovementType moveType) {
            this.path = path;
            this.moveType = moveType;
        }

        public Builder isMirrorXAxis(boolean bool) {
            this.mirrorXAxis = bool;
            return this;
        }

        public Builder isMirrorYAxis(boolean bool) {
            this.mirrorYAxis = bool;
            return this;
        }

        public Builder isSpecificQuadrant(boolean bool) {
            this.isSpecificQuadrant = bool;
            return this;
        }

        public Builder isAttack(boolean bool) {
            this.isAttack = bool;
            return this;
        }

        public Builder isMove(boolean bool) {
            this.isMove = bool;
            return this;
        }

        public Builder conditions(List<Conditional> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder extraAction(ExtraAction extraAction) {
            this.extraAction = extraAction;
            return this;
        }

        public Movement build() {
            return new Movement(this);
        }
    }

    public Movement(Builder builder) {
        this.pathBase = builder.path;
        this.moveType = builder.moveType;
        this.mirrorXAxis = builder.mirrorXAxis;
        this.mirrorYAxis = builder.mirrorYAxis;
        this.isSpecificQuadrant = builder.isSpecificQuadrant;
        this.isMove = builder.isMove;
        this.isAttack = builder.isAttack;
        this.conditions = builder.conditions;
        this.extraAction = builder.extraAction;
    }

    public Movement(Path path, MovementType moveType, boolean mirrorXAxis, boolean mirrorYAxis) {
        this.pathBase = path;
        this.moveType = moveType;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.isSpecificQuadrant = false;
        this.isMove = true;
        this.isAttack = true;
        this.conditions = List.of();
        this.extraAction = null;
    }

    public Path getBasePath() {
        return this.pathBase;
    }

    public MovementType getMoveType() {
        return this.moveType;
    }

    public ExtraAction getExtraAction() {
        return this.extraAction;
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
    public Path getPath(Colour colour, Point start, Point end, CustomBoard board) {
        if (colour == null || start == null || end == null) {
            throw new NullPointerException();
        }

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

        List<Point> vectors = new LinkedList<>();
        for (Point vector : this.pathBase) {
            int nextX = !negX ? vector.getX() + start.getX() : start.getX() - vector.getX();
            int nextY = !negY ? vector.getY() + start.getY() : start.getY() - vector.getY();
            if (!board.isValidLocation(nextX, nextY)) {
                break;
            }
            vectors.add(new Point(nextX, nextY));
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
     * @param offset {@link Point} representing the position of the piece
     * @return Map of {@link Point}
     */
    public Set<Point> getCoordinates(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        return this.getCoordinates(colour, offset, null, false, false);
    }

    public Set<Point> getCoordinates(Colour colour, Point offset, CustomBoard board,
            boolean withDefend, boolean ignoreKing) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        if (this.isSpecificQuadrant) {
            return getVectorsInSpecificQuadrant(offset, colour, board, withDefend, ignoreKing);
        } else {
            return getVectorsInAllQuadrants(offset, colour, board, withDefend, ignoreKing);
        }
    }

    private Set<Point> getVectorsInSpecificQuadrant(Point offset, Colour colour, CustomBoard board,
            boolean withDefend, boolean ignoreKing) {
        if (offset == null || colour == null) {
            throw new NullPointerException();
        }
        boolean isRight = !mirrorYAxis;
        boolean isUp = Colour.WHITE.equals(colour) && !mirrorXAxis || !Colour.WHITE.equals(colour) && mirrorXAxis;

        Set<Point> set = new HashSet<>();
        for (Point vector : this.pathBase) {
            Point v = getVectorInQuadrant(vector, offset, isRight, isUp);
            if (canMoveInQuadrant(v, colour, board, withDefend, ignoreKing))
                set.add(v);
            if (isBlockedInQuadrant(v, board, ignoreKing))
                break;
        }
        return set;
    }

    private Set<Point> getVectorsInAllQuadrants(Point offset, Colour colour, CustomBoard board,
            boolean withDefend, boolean ignoreKing) {
        if (offset == null || colour == null) {
            throw new NullPointerException();
        }
        boolean blockTopRight = false;
        boolean blockTopLeft = false;
        boolean blockBotRight = false;
        boolean blockBotLeft = false;

        Set<Point> set = new HashSet<>();
        for (Point vector : this.pathBase) {
            if (mirrorXAxis || Colour.WHITE.equals(colour)) {
                if (!blockTopRight) {
                    Point topRight = getVectorInQuadrant(vector, offset, true, true);
                    if (canMoveInQuadrant(topRight, colour, board, withDefend, ignoreKing))
                        set.add(topRight);
                    blockTopRight = isBlockedInQuadrant(topRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockTopLeft) {
                    Point topLeft = getVectorInQuadrant(vector, offset, false, true);
                    if (canMoveInQuadrant(topLeft, colour, board, withDefend, ignoreKing))
                        set.add(topLeft);
                    blockTopLeft = isBlockedInQuadrant(topLeft, board, ignoreKing);
                }
            }
            if (mirrorXAxis || !Colour.WHITE.equals(colour)) {
                if (!blockBotRight) {
                    Point bottomRight = getVectorInQuadrant(vector, offset, true, false);
                    if (canMoveInQuadrant(bottomRight, colour, board, withDefend, ignoreKing))
                        set.add(bottomRight);
                    blockBotRight = isBlockedInQuadrant(bottomRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockBotLeft) {
                    Point bottomLeft = getVectorInQuadrant(vector, offset, false, false);
                    if (canMoveInQuadrant(bottomLeft, colour, board, withDefend, ignoreKing))
                        set.add(bottomLeft);
                    blockBotLeft = isBlockedInQuadrant(bottomLeft, board, ignoreKing);
                }
            }
        }
        return set;
    }

    private Point getVectorInQuadrant(Point vector, Point offset, boolean isRight,
            boolean isUp) {
        if (vector == null || offset == null) {
            throw new NullPointerException();
        }
        int x = isRight ? offset.getX() + vector.getX() : offset.getX() - vector.getX();
        int y = isUp ? offset.getY() + vector.getY() : offset.getY() - vector.getY();
        return new Point(x, y);
    }

    private boolean canMoveInQuadrant(Point vector, Colour colour, CustomBoard board,
            boolean withDefend, boolean ignoreKing) {
        if (vector == null || colour == null) {
            throw new NullPointerException();
        }
        if (!vector.isValidLocation(vector)) {
            return false;
        }
        if (board != null) {
            CustomPiece p = board.getPiece(vector);
            if (p != null) {
                return withDefend || !colour.equals(p.getColour()) || PieceType.KING.equals(p.getType()) && ignoreKing;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean isBlockedInQuadrant(Point vector, CustomBoard board, boolean ignoreKing) {
        if (vector == null) {
            throw new NullPointerException();
        }
        if (!vector.isValidLocation(vector) || board == null) {
            return false;
        }
        CustomPiece customPiece = board.getPiece(vector);
        return customPiece != null && !(PieceType.KING.equals(customPiece.getType()) && ignoreKing);
    }

    /**
     * Verifies that all {@link Conditional} defined in this Movement are meeting their criteria.
     *
     * @param board  {@link CustomBoard} for the Condition to verify with
     * @param action {@link Action} representing the movement attempted for a Piece at a location to a destination
     * @return true if all Condition pass, otherwise false
     */
    public boolean passesConditions(CustomBoard board, Action action) {
        if (board == null || action == null) {
            throw new NullPointerException();
        }
        CustomPiece pStart = board.getPiece(action.getStart());
        CustomPiece pEnd = board.getPiece(action.getEnd());
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
    public boolean[][] drawCoordinates(Colour colour) {
        if (colour == null) {
            throw new NullPointerException();
        }
        return this.drawCoordinates(colour, new Point(0, 0));
    }

    /**
     * Marks all locations valid for this piece to move to, before referencing the board, from origin. This matches
     * the original path of this movement for White pieces.
     *
     * @param colour Colour of the piece, to determine which direction is forward.
     * @return 2D boolean array, true are valid locations
     */
    public boolean[][] drawCoordinates(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        Set<Point> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[8][8];
        for (Point c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    @Override
    public String toString() {
        return this.toString(Colour.WHITE, new Point());
    }

    public String toString(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
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
