package com.chess.api.model.movement;

import com.chess.api.model.Coordinate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Getter;

public class Path implements Iterable<Coordinate> {

    @Getter
    private final LinkedHashMap<Integer, Coordinate> map;

    public Path() {
        this.map = new LinkedHashMap<>();
    }

    public Path(List<Coordinate> coordinates) {
        LinkedHashMap<Integer, Coordinate> linkedHashMap = new LinkedHashMap<>();
        if (coordinates != null) {
            for (Coordinate coordinate : coordinates) {
                if (coordinate != null) {
                    linkedHashMap.put(coordinate.hashCode(), coordinate);
                }
            }
        }
        this.map = linkedHashMap;
    }

    public Path(Coordinate end) {
        LinkedHashMap<Integer, Coordinate> linkedHashMap = new LinkedHashMap<>();
        if (end != null) {
            linkedHashMap.put(end.hashCode(), end);
        }
        this.map = linkedHashMap;
    }

    public Path(Coordinate start, Coordinate end) {
        LinkedHashMap<Integer, Coordinate> linkedHashMap = new LinkedHashMap<>();
        if (start == null || end == null) {
            this.map = linkedHashMap;
            return;
        }

        PathType pathType;
        if (start.equals(end)) {
            pathType = PathType.CUSTOM;
        } else {
            pathType = PathType.findType(start, end);
        }

        int x = start.getX();
        int y = start.getY();
        switch (pathType) {
            case VERTICAL -> {
                while (y != end.getY()) {
                    int diff = end.getY() - start.getY();
                    int dir = diff / Math.abs(diff);
                    Coordinate coordinate = Coordinate.at(x, y);
                    linkedHashMap.put(coordinate.hashCode(), coordinate);
                    y = y + dir;
                }
            }
            case HORIZONTAL -> {
                while (x != end.getX()) {
                    int diff = end.getX() - start.getX();
                    int dir = diff / Math.abs(diff);
                    Coordinate coordinate = Coordinate.at(x, y);
                    linkedHashMap.put(coordinate.hashCode(), coordinate);
                    x = x + dir;
                }
            }
            case DIAGONAL -> {
                while (x != end.getX() && y != end.getY()) {
                    int diffX = end.getX() - start.getX();
                    int diffY = end.getY() - start.getY();
                    int dirX = diffX / Math.abs(diffX);
                    int dirY = diffY / Math.abs(diffY);

                    Coordinate coordinate = Coordinate.at(x, y);
                    linkedHashMap.put(coordinate.hashCode(), coordinate);
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

    public int size() {
        if (!this.map.containsValue(null)) {
            return this.map.size();
        }
        // Ignore all incorrectly added null values
        int size = 0;
        for (Coordinate coordinate : this) {
            if (coordinate != null) {
                size++;
            }
        }
        return size;
    }

    public boolean isEmpty() {
        if (!this.map.containsValue(null)) {
            return this.map.isEmpty();
        }
        // Ignore all incorrectly added null values
        for (Coordinate coordinate : this) {
            if (coordinate != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Coordinate> iterator() {
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
        for (Coordinate coordinate : this) {
            result = result * prime + coordinate.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Coordinate> iterator = this.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
