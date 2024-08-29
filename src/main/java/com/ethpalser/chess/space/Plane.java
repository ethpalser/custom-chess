package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Plane<T extends Positional> implements Map<Point, T>, Iterable<T> {

    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    private final Map<Point, T> space;

    public Plane() {
        space = new HashMap<>();
        this.minX = 0;
        this.minY = 0;
        this.maxX = 7;
        this.maxY = 7;
    }

    public Plane(int maxX, int maxY) {
        space = new HashMap<>();
        this.minX = 0;
        this.minY = 0;
        this.maxX = maxX;
        this.maxY = maxY;
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
    public T put(Point key, T value) {
        if (value == null) {
            throw new IllegalArgumentException("value added to plane cannot be null; occurred at point " + key);
        }
        return space.put(key, value);
    }

    @Override
    public T remove(Object key) {
        return space.remove(key);
    }

    @Override
    public void putAll(Map<? extends Point, ? extends T> m) {
        space.putAll(m);
    }

    @Override
    public void clear() {
        space.clear();
    }

    @Override
    public Set<Point> keySet() {
        return space.keySet();
    }

    @Override
    public Collection<T> values() {
        return space.values();
    }

    @Override
    public Set<Entry<Point, T>> entrySet() {
        return space.entrySet();
    }

    public Iterator<T> iterator() {
        return space.values().iterator();
    }

    public Point at(int x, int y) throws IndexOutOfBoundsException {
        if (!this.isInBounds(x, y)) {
            String errMsg = "Invalid x (" + x + ") or y (" + y + ") coordinates for this space. " + this.printBounds();
            throw new IndexOutOfBoundsException(errMsg);
        }
        return new Point(x, y);
    }

    public Point at(char x, char y) throws IndexOutOfBoundsException {
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

    public boolean isInBounds(Point point) {
        return point != null && this.isInBounds(point.getX(), point.getY());
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

    public int length() {
        return this.maxY - this.minY + 1;
    }

    public int width() {
        return this.maxX - this.minX + 1;
    }

    // Temporary
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length() - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width() - 1; x++) {
                Piece piece = (Piece) get(this.at(x, y));
                if (piece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");

                    String code = piece.getCode();
                    if ("".equals(code)) {
                        code = "P"; // In some cases that pawn's code is an empty string
                    }
                    if (Colour.WHITE.equals(piece.getColour())) {
                        code = code.toLowerCase(Locale.ROOT);
                    }
                    sb.append(code).append(" ");
                }
            }
            sb.append("| ").append(1 + y).append("\n");
        }
        for (int x = 0; x < this.width(); x++) {
            sb.append("  ").append((char) ('a' + x)).append(" ");
        }
        return sb.toString();
    }
}
