package com.ethpalser.chess.space;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Path implements Iterable<Coordinate> {

    private final List<Coordinate> pointList;

    public Path(Coordinate end) {
        this.pointList = new LinkedList<>();
        if (end != null) {
            this.pointList.add(end);
        }
    }

    public Path(List<Coordinate> points) {
        this.pointList = points;
    }

    /**
     * Creates a Path moving in a linear direction (vertical, horizontal or diagonal) from start to end
     * {@link Point}.
     *
     * @param start {@link Point} representing the first vector of the path
     * @param end   {@link Point} representing the last vector of the path
     */
    public Path(Coordinate start, Coordinate end) {
        List<Coordinate> list;
        switch (PathType.fromPoints(start, end)) {
            case POINT, CUSTOM -> {
                list = new LinkedList<>();
                if (start != null)
                    list.add(start);
                if (end != null)
                    list.add(end);
            }
            case VERTICAL, HORIZONTAL, DIAGONAL -> {
                int x = start.getValue(Space.AXIS.X);
                int y = start.getValue(Space.AXIS.Y);
                int diffX = end.getValue(Space.AXIS.X) - x;
                int diffY = end.getValue(Space.AXIS.Y) - y;
                int dirX = diffX != 0 ? diffX / Math.abs(diffX) : 0;
                int dirY = diffY != 0 ? diffY / Math.abs(diffY) : 0;

                list = new LinkedList<>();
                Coordinate point = new Point(x, y);
                // Build the path along the line until an edge is exceeded
                do {
                    list.add(point);
                    point = point.translate(1, dirX, dirY);
                } while (point.getValue(Space.AXIS.X) != end.getValue(Space.AXIS.X)
                        || point.getValue(Space.AXIS.Y) != end.getValue(Space.AXIS.Y));
                // Loop only continues until the end point is reached, so this is added after
                list.add(end);
            }
            default -> list = new LinkedList<>();
        }
        list.remove(null);
        this.pointList = list;
    }

    public Path(Space space, Coordinate start, int[] shiftVector) {
        if (space == null || start == null || shiftVector == null) {
            throw new IllegalArgumentException("one ore more arguments are null");
        }
        boolean hasNonZero = false;
        for (int i = 0; i < shiftVector.length && !hasNonZero; i++) {
            if (shiftVector[i] != 0) {
                hasNonZero = true;
            }
        }
        if (!hasNonZero) {
            throw new IllegalArgumentException("shift vector cannot have all zeroes");
        }

        List<Coordinate> list = new LinkedList<>();
        Coordinate pos = start;
        while (!space.isOutOfBounds(pos)) {
            list.add(pos);
            pos = pos.translate(1, shiftVector);
        }
        this.pointList = list;
    }

    public int length() {
        return this.pointList.size();
    }

    public boolean isEmpty() {
        return this.pointList.isEmpty();
    }

    public Coordinate getPoint(int index) {
        return this.pointList.get(index);
    }

    public boolean hasPoint(Coordinate point) {
        return this.pointList.contains(point);
    }

    public List<Coordinate> toList() {
        return new ArrayList<>(this.pointList);
    }

    public Set<Coordinate> toSet() {
        return new LinkedHashSet<>(this.pointList);
    }

    @Override
    public Iterator<Coordinate> iterator() {
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
        for (Coordinate vector : this) {
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
        Iterator<Coordinate> iterator = this.iterator();
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

        public static Path.PathType fromPoints(Coordinate start, Coordinate end) {
            if (start == null && end == null) {
                return EMPTY;
            }
            if (start == null || end == null) {
                return POINT;
            }

            int diffX = Math.abs(end.getValue(Space.AXIS.X) - start.getValue(Space.AXIS.X));
            int diffY = Math.abs(end.getValue(Space.AXIS.Y) - start.getValue(Space.AXIS.Y));
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
