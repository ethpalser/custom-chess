package com.ethpalser.chess.view;

import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import java.util.Objects;

public class ReferenceView {

    private final Reference.Location location;
    private final Point point;
    private final Direction direction;

    public ReferenceView(Reference.Location location, Point point, Direction direction) {
        this.location = Objects.requireNonNullElse(location, Reference.Location.POINT);
        this.point = point;
        this.direction = direction;
    }

    public Reference.Location getLocation() {
        return location;
    }

    public Point getPoint() {
        return point;
    }

    public Direction getDirection() {
        return direction;
    }
}
