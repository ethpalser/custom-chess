package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeReference<T> implements Reference<T> {

    private final Location location;
    private final Direction direction;
    private final Colour colour;
    private final Point start;
    private final Point end;

    public RelativeReference(Location location) {
        this(location, Direction.AT, null);
    }

    public RelativeReference(Location location, Point point) {
        this(location, Direction.AT, point);
    }

    public RelativeReference(Location location, Direction direction, Point point) {
        this(location, Colour.WHITE, direction, point, null);
    }

    public RelativeReference(Location location, Colour colour, Direction direction, Point point) {
        this(location, colour, direction, point, null);
    }

    public RelativeReference(Location location, Colour colour, Direction direction, Point start, Point end) {
        this.location = location;
        this.direction = direction;
        this.colour = colour;
        this.start = start;
        this.end = end;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public List<T> getReferences(Plane<T> plane) {
        if (plane == null) {
            return List.of();
        }
        Point shiftedStart = this.start.shift(this.colour, this.direction);
        Point shiftedEnd = this.end.shift(this.colour, this.direction);

        return switch (this.location) {
            case START, VECTOR -> List.of(plane.get(shiftedStart));
            case DESTINATION -> List.of(plane.get(shiftedEnd));
            case PATH_TO_DESTINATION, PATH_TO_VECTOR -> new Path(shiftedStart, shiftedEnd).toSet().stream().map(plane::get)
                    .collect(Collectors.toList());
            default -> List.of();
        };
    }
}
