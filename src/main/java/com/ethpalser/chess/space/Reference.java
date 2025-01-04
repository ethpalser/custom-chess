package com.ethpalser.chess.space;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.view.ReferenceView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Reference {

    private final Direction direction;
    private final Location location;
    private final Coordinate[] fixedCoordinates;

    /**
     * Self Reference. When you retrieve coordinates for this reference you are expected to provide the coordinate
     * you want, such as a Piece's own location.
     */
    public Reference() {
        this(Direction.AT, Location.POINT, (Coordinate[]) null);
    }

    public Reference(Direction direction, Location location) {
        this(direction, location, (Coordinate[]) null);
    }

    public Reference(Direction direction, Location location, Coordinate fixedCoordinate) {
        this(direction, location, new Coordinate[]{fixedCoordinate});
    }

    public Reference(Direction direction, Location location, Coordinate[] fixedCoordinates) {
        if (direction == null || location == null) {
            throw new IllegalArgumentException("Either direction or location are null");
        }
        this.direction = direction;
        this.location = location;
        this.fixedCoordinates = fixedCoordinates;
    }

    public Reference(Direction direction, Location location, Path path) {
        if (direction == null || location == null) {
            throw new IllegalArgumentException("Either direction or location are null");
        }
        this.direction = direction;
        this.location = location;
        if (path == null) {
            this.fixedCoordinates = null;
        } else {
            Coordinate[] arr = new Coordinate[path.length()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = path.getPoint(i);
            }
            this.fixedCoordinates = arr;
        }
    }

    public List<Coordinate> coordinates(GameContext.Record context, Coordinate relativeCoordinate) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        if (this.fixedCoordinates == null && relativeCoordinate == null) {
            throw new IllegalArgumentException("null relative coordinate and reference does not use fixed coordinates");
        }

        int[] directionVector = this.direction.vector();
        List<Coordinate> coordinates;
        switch (this.location) {
            case LAST_MOVED -> coordinates = List.of(context.getLog().peek().getEnd());
            case POINT -> {
                // Only one coordinate is expected in this case, and all others are ignored
                if (this.fixedCoordinates != null && this.fixedCoordinates.length > 0) {
                    coordinates = List.of(this.fixedCoordinates[0]);
                } else if (relativeCoordinate != null) {
                    // Todo: apply direction vector to all coordinates
                    coordinates = List.of(relativeCoordinate);
                } else {
                    coordinates = List.of();
                }
            }
            case PATH -> {
                if (this.fixedCoordinates != null) {
                    coordinates = List.of(this.fixedCoordinates);
                    coordinates.forEach(c -> c.translate(1, directionVector));
                } else {
                    if (Direction.AT.equals(this.direction)) {
                        coordinates = List.of(relativeCoordinate);
                    } else {
                        // Draw a path to the edge of the board
                        List<Coordinate> temp = new ArrayList<>();
                        Board<Coordinate> board = context.getBoard();
                        // Shift the coordinate first, as the relative coordinate is not intended to be included
                        Coordinate c = relativeCoordinate.translate(1, directionVector);
                        while (!board.rejects(c)) {
                            temp.add(c);
                            c.translate(1, directionVector);
                        }
                        coordinates = temp;
                    }
                }
            }
            default -> coordinates = List.of();
        }
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reference reference = (Reference) o;
        return direction == reference.direction && location == reference.location && Arrays.equals(fixedCoordinates,
                reference.fixedCoordinates);
    }

    @Override
    public String toString() {
        return "Reference{" +
                "direction=" + direction +
                ", location=" + location +
                ", fixedCoordinates=" + Arrays.toString(fixedCoordinates) +
                '}';
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(direction, location);
        result = 31 * result + Arrays.hashCode(fixedCoordinates);
        return result;
    }

    public ReferenceView toView() {
        return new ReferenceView(this.location, this.fixedCoordinates != null ? (Point) this.fixedCoordinates[0] :
                null, this.direction);
    }
}
