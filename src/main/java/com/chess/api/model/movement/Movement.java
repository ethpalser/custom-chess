package com.chess.api.model.movement;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.condition.Condition;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Movement {

    private final Path originalPath;
    private final MovementType type;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean specificQuadrant;
    private final List<Condition> conditions;
    private final ExtraMovement extraMovement;

    public Movement() {
        this.originalPath = new Path();
        this.type = MovementType.ADVANCE;
        this.mirrorXAxis = false;
        this.mirrorYAxis = false;
        this.specificQuadrant = false;
        this.conditions = null;
        this.extraMovement = null;
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis) {
        this(path, type, mirrorXAxis, mirrorYAxis, false, List.of(), null);
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, boolean specificQuadrant, List<Condition> conditions) {
        this(path, type, mirrorXAxis, mirrorYAxis, specificQuadrant, conditions, null);
    }

    public Movement(Path path, MovementType type, boolean mirrorXAxis, boolean mirrorYAxis, boolean specificQuadrant, List<Condition> conditions, ExtraMovement extraMovement) {
        this.originalPath = path;
        this.type = type;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.specificQuadrant = specificQuadrant;
        this.conditions = conditions;
        this.extraMovement = extraMovement;
    }

    public Path getPath(@NonNull Colour colour, @NonNull Coordinate start, @NonNull Coordinate end) {
        // Determine direction
        int diffX = end.getX() - start.getX();
        int diffY = end.getY() - start.getY();
        boolean negX = diffX != 0 && diffX / Math.abs(diffX) == -1;
        boolean negY = diffY != 0 && diffY / Math.abs(diffY) == -1;
        boolean isBlack = Colour.BLACK.equals(colour);

        // Invalid direction cases
        if ((!negX && isBlack && !this.mirrorXAxis)
                || (negX && !isBlack && !this.mirrorXAxis)
                || (negY && !this.mirrorYAxis)) {
            return new Path();
        }

        List<Coordinate> coordinates = new LinkedList<>();
        for (Coordinate coordinate : this.getOriginalPath()) {
            int nextX = !negX ? coordinate.getX() + start.getX() : start.getX() - coordinate.getX();
            int nextY = !negY ? coordinate.getY() + start.getY() : start.getY() - coordinate.getY();
            if (!Coordinate.isValid(nextX, nextY)) {
                break;
            }
            coordinates.add(Coordinate.at(nextX, nextY));
        }
        return new Path(coordinates);
    }

    public Map<Integer, Coordinate> getCoordinates(@NonNull Colour colour, @NonNull Coordinate offset) {
        boolean isWhite = Colour.WHITE.equals(colour);
        boolean isUp = isWhite && !mirrorXAxis || !isWhite && mirrorXAxis;
        boolean isRight = !mirrorYAxis;
        boolean hasUp = isWhite || mirrorXAxis;
        boolean hasDown = !isWhite || mirrorXAxis;

        Map<Integer, Coordinate> map = new HashMap<>();
        for (Coordinate coordinate : this.getOriginalPath()) {
            int baseX = coordinate.getX() + offset.getX();
            int baseY = coordinate.getY() + offset.getY();
            int mirrorX = offset.getX() - coordinate.getX();
            int mirrorY = offset.getY() - coordinate.getY();

            Coordinate upRight = Coordinate.isValid(baseX, baseY) ? Coordinate.at(baseX, baseY) : null;
            Coordinate upLeft = Coordinate.isValid(mirrorX, baseY) ? Coordinate.at(mirrorX, baseY) : null;
            Coordinate downRight = Coordinate.isValid(baseX, mirrorY) ? Coordinate.at(baseX, mirrorY) : null;
            Coordinate downLeft = Coordinate.isValid(mirrorX, mirrorY) ? Coordinate.at(mirrorX, mirrorY) : null;

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

    public boolean isValidCoordinate(@NonNull Colour colour, @NonNull Coordinate source,
            @NonNull Coordinate destination) {
        Map<Integer, Coordinate> coordinates = this.getCoordinates(colour, source);
        return coordinates.get(destination.hashCode()) != null;
    }

    public boolean[][] drawCoordinates(@NonNull Colour colour) {
        return this.drawCoordinates(colour, new Coordinate(0, 0));
    }

    public boolean[][] drawCoordinates(@NonNull Colour colour, @NonNull Coordinate offset) {
        Map<Integer, Coordinate> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : coordinates.values()) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    @Override
    public String toString() {
        return this.toString(Colour.WHITE, Coordinate.origin());
    }

    public String toString(@NonNull Colour colour, @NonNull Coordinate offset) {
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
