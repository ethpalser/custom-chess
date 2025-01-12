package com.ethpalser.chess.move.config;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Reference {
    // Note: Edge locations are constrained to a 2D space
    public enum Location {POINT, PATH, LAST_MOVED, NORTH_EDGE, SOUTH_EDGE, EAST_EDGE, WEST_EDGE}

    private final Location location;
    private final Direction direction;
    private final int distance;
    private final Coordinate[] fixedCoordinates;

    /**
     * Self Reference. When you retrieve coordinates for this reference you are expected to provide the coordinate
     * you want, such as a Piece's own location.
     */
    public Reference() {
        this(Location.POINT, Direction.AT, 0, (Coordinate[]) null);
    }

    public Reference(Location location) {
        this(location, Direction.AT, 1, (Coordinate[]) null);
    }

    public Reference(Location location, Direction direction) {
        this(location, direction, 1, (Coordinate[]) null);
    }

    public Reference(Location location, Direction direction, int distance) {
        this(location, direction, distance, (Coordinate[]) null);
    }

    public Reference(Location location, Direction direction, Coordinate fixedCoordinate) {
        this(location, direction, 1, new Coordinate[]{fixedCoordinate});
    }

    public Reference(Location location, Direction direction, int distance, Coordinate fixedCoordinate) {
        this(location, direction, distance, new Coordinate[]{fixedCoordinate});
    }

    public Reference(Location location, Direction direction, int distance, Coordinate[] fixedCoordinates) {
        if (direction == null || location == null) {
            throw new IllegalArgumentException("Either direction or location are null");
        }
        this.location = location;
        this.direction = direction;
        this.distance = distance;
        this.fixedCoordinates = fixedCoordinates;
    }

    public Reference(Location location, Direction direction, Path path) {
        if (direction == null || location == null) {
            throw new IllegalArgumentException("Either direction or location are null");
        }
        this.location = location;
        this.direction = direction;
        this.distance = 1;
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

    public List<Coordinate> coordinates(GameContext.Record context, Coordinate providedCoordinates) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        if (this.fixedCoordinates == null && providedCoordinates == null) {
            throw new IllegalArgumentException("both fixed and provided coordinates are null, one should not be null");
        }
        List<Coordinate> coordinates = switch (this.location) {
            case LAST_MOVED -> this.lastMovedCoordinate(context.getLog());
            case POINT -> this.pointCoordinate(providedCoordinates);
            case PATH -> this.pathCoordinates(providedCoordinates, context.getBoard());
            default -> this.edgeCoordinate(providedCoordinates, context.getBoard().space());
        };
        return coordinates.stream().map(c -> c.translate(this.distance, this.direction)).toList();
    }

    private List<Coordinate> lastMovedCoordinate(ChessLog log) {
        if (log != null && log.peek() != null) {
            ChessRecord rec = log.peek().notation().toRecord();
            if (rec != null && !rec.isFollowUp()) {
                return List.of(rec.target());
            } else {
                // FollowUp moves will have there not be a last moved coordinate
                return List.of();
            }
        }
        return List.of();
    }

    private List<Coordinate> pointCoordinate(Coordinate provided) {
        // Only one coordinate is expected in this case, and all others are ignored
        if (this.fixedCoordinates != null && this.fixedCoordinates.length > 0) {
            return List.of(this.fixedCoordinates[0]);
        } else if (provided != null) {
            return List.of(provided);
        }
        return List.of();
    }

    private List<Coordinate> pathCoordinates(Coordinate provided, Board<Coordinate> board) {
        if (this.fixedCoordinates != null) {
            return Arrays.asList(this.fixedCoordinates);
        } else if (Direction.AT.equals(this.direction)) {
            return List.of(provided);
        }
        // Draw a path to the edge of the board
        List<Coordinate> coordinates = new ArrayList<>();
        int[] directionVector = this.direction.vector();
        Coordinate point = provided;
        while (!board.rejects(point)) {
            coordinates.add(point);
            point = point.translate(1, directionVector);
        }
        return coordinates;
    }

    private List<Coordinate> edgeCoordinate(Coordinate provided, Space space) {
        return switch (this.location) {
            case NORTH_EDGE -> List.of(new Point(provided.getValue(Space.AXIS.X), space.max(Space.AXIS.Y)));
            case SOUTH_EDGE -> List.of(new Point(provided.getValue(Space.AXIS.X), space.min(Space.AXIS.Y)));
            case EAST_EDGE -> List.of(new Point(space.max(Space.AXIS.X), provided.getValue(Space.AXIS.Y)));
            case WEST_EDGE -> List.of(new Point(space.min(Space.AXIS.X), provided.getValue(Space.AXIS.Y)));
            default -> List.of(); // Cannot determine an edge without an absolute direction
        };
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
}
