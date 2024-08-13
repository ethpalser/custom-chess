package com.ethpalser.chess.space;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Path implements Iterable<Point> {

    private final LinkedHashMap<Integer, Point> linkedHashMap;

    public Path(Point end) {
        this.linkedHashMap = new LinkedHashMap<>();
        this.linkedHashMap.put(end.hashCode(), end);
    }

    public Path(List<Point> points) {
        this.linkedHashMap = points.stream().collect(Collector.of(
                (Supplier<LinkedHashMap<Integer, Point>>) LinkedHashMap::new,
                (map, point) -> map.put(point.hashCode(), point),
                (map, map2) -> {
                    map.putAll(map2);
                    return map;
                }
        ));
    }

    /**
     * Creates a Path moving in a linear direction (vertical, horizontal or diagonal) from start to end
     * {@link Point}.
     *
     * @param start {@link Point} representing the first vector of the path
     * @param end   {@link Point} representing the last vector of the path
     */
    public Path(Point start, Point end) {
        LinkedHashMap<Integer, Point> map;
        switch (PathType.fromPoints(start, end)) {
            case POINT, CUSTOM -> {
                map = new LinkedHashMap<>();
                if (start != null)
                    map.put(start.hashCode(), start);
                if (end != null)
                    map.put(end.hashCode(), end);
            }
            case VERTICAL, HORIZONTAL, DIAGONAL -> {
                int x = start.getX();
                int y = start.getY();
                int diffX = end.getX() - x;
                int diffY = end.getY() - y;
                int dirX = diffX / Math.abs(diffX); // 0 for Vertical
                int dirY = diffY / Math.abs(diffY); // 0 for Horizontal

                map = new LinkedHashMap<>();
                // Build the path along the line until an edge is exceeded
                while (x != end.getX() + dirX && y != end.getY() + dirY) {
                    Point vector = new Point(x, y);
                    map.put(vector.hashCode(), vector);
                    x = x + dirX;
                    y = y + dirY;
                }
            }
            default -> map = new LinkedHashMap<>();
        }
        this.linkedHashMap = map;
    }

    @Override
    public Iterator<Point> iterator() {
        return this.linkedHashMap.values().iterator();
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
        Iterator<Point> iterator = this.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
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
