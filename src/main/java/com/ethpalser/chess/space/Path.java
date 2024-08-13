package com.ethpalser.chess.space;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class Path implements Iterable<Point> {

    private final LinkedHashMap<Integer, Point> map;

    public LinkedHashMap<Integer, Point> getMap() {
        return this.map;
    }

    public Path() {
        this.map = new LinkedHashMap<>();
    }

    public Path(Point end) {
        this(List.of(end));
    }

    public Path(List<Point> vectors) {
        LinkedHashMap<Integer, Point> linkedHashMap = new LinkedHashMap<>();
        if (vectors != null) {
            for (Point vector : vectors) {
                if (vector != null) {
                    linkedHashMap.put(vector.hashCode(), vector);
                }
            }
        }
        this.map = linkedHashMap;
    }

    /**
     * Creates a Path moving in a linear direction (vertical, horizontal or diagonal) from start to end
     * {@link Point}.
     *
     * @param start {@link Point} representing the first vector of the path
     * @param end   {@link Point} representing the last vector of the path
     */
    public Path(Point start, Point end) {
        LinkedHashMap<Integer, Point> linkedHashMap = new LinkedHashMap<>();
        if (start == null || end == null) {
            this.map = linkedHashMap;
            return;
        }

        PathType pathType;
        if (start.equals(end)) {
            pathType = PathType.CUSTOM;
        } else {
            pathType = PathType.fromPoints(start, end);
        }

        int x = start.getX();
        int y = start.getY();
        switch (pathType) {
            case VERTICAL -> {
                int diff = end.getY() - start.getY();
                int dir = diff / Math.abs(diff);

                while (y != end.getY() + dir) {
                    Point vector = new Point(x, y);
                    linkedHashMap.put(vector.hashCode(), vector);
                    y = y + dir;
                }
            }
            case HORIZONTAL -> {
                int diff = end.getX() - start.getX();
                int dir = diff / Math.abs(diff);

                while (x != end.getX() + dir) {
                    Point vector = new Point(x, y);
                    linkedHashMap.put(vector.hashCode(), vector);
                    x = x + dir;
                }
            }
            case DIAGONAL -> {
                int diffX = end.getX() - start.getX();
                int diffY = end.getY() - start.getY();
                int dirX = diffX / Math.abs(diffX);
                int dirY = diffY / Math.abs(diffY);

                while (x != end.getX() + dirX && y != end.getY() + dirY) {
                    Point vector = new Point(x, y);
                    linkedHashMap.put(vector.hashCode(), vector);
                    x = x + dirX;
                    y = y + dirY;
                }
            }
            case CUSTOM -> {
                linkedHashMap.put(start.hashCode(), start);
                linkedHashMap.put(end.hashCode(), end);
            }
        }
        this.map = linkedHashMap;
    }

    /**
     * Iterates through all vectors of this path to count all non-null elements.
     *
     * @return int of non-null elements in path
     */
    public int size() {
        if (!this.map.containsValue(null)) {
            return this.map.size();
        }
        // Ignore all incorrectly added null values
        int size = 0;
        for (Point vector : this) {
            if (vector != null) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Iterator<Point> iterator() {
        return this.map.values().iterator();
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
        int prime = 31;
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
        VERTICAL,
        HORIZONTAL,
        DIAGONAL,
        CUSTOM;

        public static Path.PathType fromPoints(Point start, Point end) {
            int diffX = Math.abs(end.getX() - start.getX());
            int diffY = Math.abs(end.getY() - start.getY());

            if (diffX == 0) {
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
