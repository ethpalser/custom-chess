package com.ethpalser.chess.space;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Path implements Iterable<Point> {

    private final LinkedHashSet<Point> linkedHashSet;

    public Path(Point end) {
        this.linkedHashSet = new LinkedHashSet<>();
        this.linkedHashSet.add(end);
    }

    public Path(List<Point> points) {
        this.linkedHashSet = points.stream().collect(Collector.of(
                (Supplier<LinkedHashSet<Point>>) LinkedHashSet::new,
                HashSet::add,
                (map, map2) -> {
                    map.addAll(map2);
                    return map;
                }
        ));
    }

    public Path(LinkedHashSet<Point> set) {
        this.linkedHashSet = set;
    }

    /**
     * Creates a Path moving in a linear direction (vertical, horizontal or diagonal) from start to end
     * {@link Point}.
     *
     * @param start {@link Point} representing the first vector of the path
     * @param end   {@link Point} representing the last vector of the path
     */
    public Path(Point start, Point end) {
        LinkedHashSet<Point> set;
        switch (PathType.fromPoints(start, end)) {
            case POINT, CUSTOM -> {
                set = new LinkedHashSet<>();
                if (start != null)
                    set.add(start);
                if (end != null)
                    set.add(end);
            }
            case VERTICAL, HORIZONTAL, DIAGONAL -> {
                int x = start.getX();
                int y = start.getY();
                int diffX = end.getX() - x;
                int diffY = end.getY() - y;
                int dirX = diffX == 0 ? 0 : diffX / Math.abs(diffX); // 0 for Vertical
                int dirY = diffY == 0 ? 0 : diffY / Math.abs(diffY); // 0 for Horizontal

                set = new LinkedHashSet<>();
                // Build the path along the line until an edge is exceeded
                while (x != end.getX() + dirX && y != end.getY() + dirY) {
                    Point point = new Point(x, y);
                    set.add(point);
                    x = x + dirX;
                    y = y + dirY;
                }
            }
            default -> set = new LinkedHashSet<>();
        }
        this.linkedHashSet = set;
    }

    public Set<Point> toSet() {
        return this.linkedHashSet;
    }

    @Override
    public Iterator<Point> iterator() {
        return this.linkedHashSet.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!this.getClass().equals(obj.getClass()))
            return false;
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int prime = 63;
        int result = 1;
        for (Point vector : this) {
            result = result * prime + vector.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Point> iterator = this.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private enum PathType {
        EMPTY,
        POINT,
        VERTICAL,
        HORIZONTAL,
        DIAGONAL,
        CUSTOM;

        public static Path.PathType fromPoints(Point start, Point end) {
            if (start == null && end == null) {
                return EMPTY;
            }
            if (start == null || end == null) {
                return POINT;
            }

            int diffX = Math.abs(end.getX() - start.getX());
            int diffY = Math.abs(end.getY() - start.getY());
            if (diffX == 0 && diffY == 0) {
                return POINT;
            } else if (diffX == 0) {
                return VERTICAL;
            } else if (diffY == 0) {
                return HORIZONTAL;
            } else if (diffX == diffY) {
                return DIAGONAL;
            } else {
                return CUSTOM;
            }
        }
    }

}
