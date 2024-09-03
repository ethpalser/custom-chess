package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.game.view.ReferenceView;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathReference<T extends Positional> implements Reference<T> {

    private final Location location;
    private final Point start;
    private final Point end;

    public PathReference(Location location) {
        this(location, null);
    }

    public PathReference(Location location, Point point) {
        this(location, point, point);
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
        switch (this.location) {
            case POINT -> {
                T ref = plane.get(this.start);
                if (ref != null) {
                    return List.of(ref);
                }
            }
            case PATH -> {
                return new Path(this.start, this.end).toSet().stream()
                        .map(plane::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            default -> {
                System.err.println("Unsupported Location{" + location + "} for path reference");
                return List.of();
            }
        }
        return List.of();
    }

    @Override
    public ReferenceView toView() {
        return new ReferenceView(Location.PATH, this.start, this.end.getX() - this.start.getX(),
                this.end.getY() - this.start.getY());
    }
}
