package com.ethpalser.chess.space;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Path implements Iterable<Point> {

    private final List<Point> pointList;

    public Path(Point end) {
        this.pointList = new LinkedList<>();
        if (end != null) {
            this.pointList.add(end);
        }
    }

    public Path(List<Point> points) {
        this.pointList = points;
    }

    /**
     * Creates a Path moving in a linear direction (vertical, horizontal or diagonal) from start to end
     * {@link Point}.
     *
     * @param start {@link Point} representing the first vector of the path
     * @param end   {@link Point} representing the last vector of the path
     */
    public Path(Point start, Point end) {
        List<Point> list;
        switch (PathType.fromPoints(start, end)) {
            case POINT, CUSTOM -> {
                list = new LinkedList<>();
                if (start != null)
                    list.add(start);
                if (end != null)
                    list.add(end);
            }
            case VERTICAL, HORIZONTAL, DIAGONAL -> {
                int x = start.getX();
                int y = start.getY();
                int diffX = end.getX() - x;
                int diffY = end.getY() - y;
                int dirX = diffX == 0 ? 0 : diffX / Math.abs(diffX); // 0 for Vertical
                int dirY = diffY == 0 ? 0 : diffY / Math.abs(diffY); // 0 for Horizontal

                list = new LinkedList<>();
                // Build the path along the line until an edge is exceeded
                do {
                    list.add(new Point(x, y));
                    x = x + dirX;
                    y = y + dirY;
                } while ((x != end.getX() || y != end.getY()));
                // Loop only continues until the end point is reached, so this is added after
                list.add(end);
            }
            default -> list = new LinkedList<>();
        }
        list.remove(null);
        this.pointList = list;
    }

    public boolean hasPoint(Point point) {
        return this.pointList.contains(point);
    }

    public Set<Point> toSet() {
        return new LinkedHashSet<>(this.pointList);
    }

    @Override
    public Iterator<Point> iterator() {
        return this.pointList.iterator();
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
        // This hash has little value. It is possible for path hashes to overlap
        // ex. Path A: [(1, 0), (4, 0)] and Path B: [(2, 0), (3, 0)] are equal
        int result = 0;
        for (Point vector : this) {
            if (vector != null) {
                result += vector.hashCode();
            }
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
