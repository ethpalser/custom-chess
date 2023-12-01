package com.chess.api.model.movement;

import com.chess.api.model.Vector2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Getter;

public class Path implements Iterable<Vector2D> {

    @Getter
    private final LinkedHashMap<Integer, Vector2D> map;

    public Path() {
        this.map = new LinkedHashMap<>();
    }

    public Path(List<Vector2D> vectors) {
        LinkedHashMap<Integer, Vector2D> linkedHashMap = new LinkedHashMap<>();
        if (vectors != null) {
            for (Vector2D vector : vectors) {
                if (vector != null) {
                    linkedHashMap.put(vector.hashCode(), vector);
                }
            }
        }
        this.map = linkedHashMap;
    }

    public Path(Vector2D end) {
        LinkedHashMap<Integer, Vector2D> linkedHashMap = new LinkedHashMap<>();
        if (end != null) {
            linkedHashMap.put(end.hashCode(), end);
        }
        this.map = linkedHashMap;
    }

    public Path(Vector2D start, Vector2D end) {
        LinkedHashMap<Integer, Vector2D> linkedHashMap = new LinkedHashMap<>();
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
                int diff = end.getY() - start.getY();
                int dir = diff / Math.abs(diff);

                while (y != end.getY() + dir) {
                    Vector2D vector = Vector2D.at(x, y);
                    linkedHashMap.put(vector.hashCode(), vector);
                    y = y + dir;
                }
            }
            case HORIZONTAL -> {
                int diff = end.getX() - start.getX();
                int dir = diff / Math.abs(diff);

                while (x != end.getX() + dir) {
                    Vector2D vector = Vector2D.at(x, y);
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
                    Vector2D vector = Vector2D.at(x, y);
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

    public int size() {
        if (!this.map.containsValue(null)) {
            return this.map.size();
        }
        // Ignore all incorrectly added null values
        int size = 0;
        for (Vector2D vector : this) {
            if (vector != null) {
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
        for (Vector2D vector : this) {
            if (vector != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Vector2D> iterator() {
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
        for (Vector2D vector : this) {
            result = result * prime + vector.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Vector2D> iterator = this.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
