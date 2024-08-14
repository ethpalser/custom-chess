package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.stream.Collectors;

public class PathReference<T> implements Reference<T> {

    private final Location location;
    private final Point start;
    private final Point end;

    public PathReference(Location location) {
        this(location, null);
    }

    public PathReference(Location location, Point point) {
        this(location, point, null);
    }

    public PathReference(Location location, Point start, Point end) {
        this.location = location;
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
        return switch (this.location) {
            case START, VECTOR -> List.of(plane.get(this.start));
            case DESTINATION -> List.of(plane.get(this.end));
            case PATH_TO_DESTINATION, PATH_TO_VECTOR -> new Path(this.start, this.end).toSet().stream().map(plane::get)
                    .collect(Collectors.toList());
            default -> List.of();
        };
    }
}
