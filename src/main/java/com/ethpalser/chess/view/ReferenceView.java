package com.ethpalser.chess.view;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.Location;
import java.util.Objects;

public class ReferenceView {

    private final Location location;
    private final Point point;
    private final int xOffset;
    private final int yOffset;

    public ReferenceView(Location location, Point point, int xOffset, int yOffset) {
        this.location = Objects.requireNonNullElse(location, Location.POINT);
        this.point = point;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Location getLocation() {
        return location;
    }

    public Point getPoint() {
        return point;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }
}
