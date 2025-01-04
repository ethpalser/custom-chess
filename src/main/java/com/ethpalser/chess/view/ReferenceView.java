package com.ethpalser.chess.view;

import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Location;
import java.util.Objects;

public class ReferenceView {

    private final Location location;
    private final Point point;
    private final Direction direction;

    public ReferenceView(Location location, Point point, Direction direction) {
        this.location = Objects.requireNonNullElse(location, Location.POINT);
        this.point = point;
        this.direction = direction;
    }

    public Location getLocation() {
        return location;
    }

    public Point getPoint() {
        return point;
    }

    public Direction getDirection() {return direction;}
}
