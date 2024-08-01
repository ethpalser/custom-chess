package com.ethpalser.chess.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Space2D<T> implements Map<Vector2D, T>, Iterable<T> {

    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    private final Map<Vector2D, T> space;

    public Space2D() {
        space = new HashMap<>();
        this.minX = 0;
        this.minY = 0;
        this.maxX = 31;
        this.maxY = 31;
    }

    public Space2D(int x1, int y1, int x2, int y2) {
        space = new HashMap<>();

        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
    }

    @Override
    public int size() {
        return space.size();
    }

    @Override
    public boolean isEmpty() {
        return space.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return space.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return space.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return space.get(key);
    }

    @Override
    public T put(Vector2D key, T value) {
        return space.put(key, value);
    }

    @Override
    public T remove(Object key) {
        return space.remove(key);
    }

    @Override
    public void putAll(Map<? extends Vector2D, ? extends T> m) {
        space.putAll(m);
    }

    @Override
    public void clear() {
        space.clear();
    }

    @Override
    public Set<Vector2D> keySet() {
        return space.keySet();
    }

    @Override
    public Collection<T> values() {
        return space.values();
    }

    @Override
    public Set<Entry<Vector2D, T>> entrySet() {
        return space.entrySet();
    }

    public Iterator<T> iterator() {
        return space.values().iterator();
    }

    public Vector2D at(int x, int y) throws IndexOutOfBoundsException {
        if (x < minX || x > maxX || y < minY || y > maxY) {
            String errMsg = "Invalid x (" + x + ") or y (" + y + ") coordinates for this space. " + this.printBounds();
            throw new IndexOutOfBoundsException(errMsg);
        }
        return new Vector2D(x, y, this.minX, this.minY, this.maxX, this.maxY);
    }

    public Vector2D at(char x, char y) throws IndexOutOfBoundsException {
        // This has greater bounds than Vector2D's char constructor. Ensures consistency in hash value
        return this.at(x - 'a', y - '1');
    }

    public String printBounds() {
        String xBounds = "[" + minX + "," + maxX + "]";
        String yBounds = "[" + minY + "," + maxY + "]";
        return "x:" + xBounds + " y:" + yBounds;
    }

    public boolean isInBounds(int x, int y) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.maxY;
    }

}
