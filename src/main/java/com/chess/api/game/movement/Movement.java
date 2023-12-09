package com.chess.api.game.movement;

import com.chess.api.game.Board;
import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;
import com.chess.api.game.condition.Conditional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public Movement() {
        this.originalPath = new Path();
        this.type = MovementType.ADVANCE;
        this.mirrorXAxis = false;
        this.mirrorYAxis = false;
        this.specificQuadrant = false;
        this.conditions = null;
        this.extraAction = null;
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
        this.originalPath = path;
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.specificQuadrant = specificQuadrant;
        this.conditions = conditions;
        this.extraAction = extraAction;
    }

    /**
     * Determines the direction that the end vector is relative to the start and builds a path in that direction
     * based off of this movement's path blueprint.
     *
     * @param colour Colour of the Piece, which determines which direction is forward
     * @param start Location of the piece
     * @param end Location the piece is requested to move to
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
    public Map<Integer, Vector2D> getCoordinates(@NonNull Colour colour, @NonNull Vector2D offset) {
        boolean isWhite = Colour.WHITE.equals(colour);
        boolean isUp = isWhite && !mirrorXAxis || !isWhite && mirrorXAxis;
        boolean isRight = !mirrorYAxis;
        boolean hasUp = isWhite || mirrorXAxis;
        boolean hasDown = !isWhite || mirrorXAxis;

        Map<Integer, Vector2D> map = new HashMap<>();
        for (Vector2D vector : this.getOriginalPath()) {
            int baseX = vector.getX() + offset.getX();
            int baseY = vector.getY() + offset.getY();
            int mirrorX = offset.getX() - vector.getX();
            int mirrorY = offset.getY() - vector.getY();

            Vector2D upRight = Vector2D.isValid(baseX, baseY) ? new Vector2D(baseX, baseY) : null;
            Vector2D upLeft = Vector2D.isValid(mirrorX, baseY) ? new Vector2D(mirrorX, baseY) : null;
            Vector2D downRight = Vector2D.isValid(baseX, mirrorY) ? new Vector2D(baseX, mirrorY) : null;
            Vector2D downLeft = Vector2D.isValid(mirrorX, mirrorY) ? new Vector2D(mirrorX, mirrorY) : null;

            if (this.specificQuadrant) {
                if (isUp && isRight && upRight != null) {
                    map.put(upRight.hashCode(), upRight);
                } else if (isUp && !isRight && upLeft != null) {
                    map.put(upLeft.hashCode(), upLeft);
                } else if (!isUp && isRight && downRight != null) {
                    map.put(downRight.hashCode(), downRight);
                } else if (!isUp && !isRight && downLeft != null) {
                    map.put(downLeft.hashCode(), downLeft);
                }
            } else {
                if (hasUp) {
                    if (upRight != null)
                        map.put(upRight.hashCode(), upRight);
                    if (this.mirrorYAxis && upLeft != null)
                        map.put(upLeft.hashCode(), upLeft);
                }
                if (hasDown) {
                    if (downRight != null)
                        map.put(downRight.hashCode(), downRight);
                    if (this.mirrorYAxis && downLeft != null)
                        map.put(downLeft.hashCode(), downLeft);
                }
            }
        }
        return map;
    }

    /**
     * Verifies that all {@link Conditional} defined in this Movement are meeting their criteria.
     *
     * @param board {@link Board} for the Condition to verify with
     * @param action {@link Action} representing the movement attempted for a Piece at a location to a destination
     * @return true if all Condition pass, otherwise false
     */
    public boolean passesConditions(@NonNull Board board, @NonNull Action action) {
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
        Map<Integer, Vector2D> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[8][8];
        for (Vector2D c : coordinates.values()) {
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
