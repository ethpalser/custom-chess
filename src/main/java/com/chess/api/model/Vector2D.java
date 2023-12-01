package com.chess.api.model;

import java.util.Locale;
import lombok.Getter;

@Getter
public class Vector2D implements Comparable<Vector2D> {

    public static final int MAX_X = 7;
    public static final int MAX_Y = 7;

    private final int x;
    private final int y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(int x, int y) {
        if (x > MAX_X || x < 0 || y > MAX_Y || y < 0) {
            throw new IndexOutOfBoundsException();
        }

        this.x = x;
        this.y = y;
    }

    public Vector2D(char x, char y) {
        this(x - 'a', y - '1');
    }

    public static Vector2D parseString(String str) {
        if (str.length() != 2) {
            throw new IllegalArgumentException();
        }
        char[] chars = str.toLowerCase(Locale.ROOT).toCharArray();
        return new Vector2D(chars[0], chars[1]);
    }

    public static Vector2D at(int x, int y) {
        return new Vector2D(x, y);
    }

    public static Vector2D origin() {
        return new Vector2D(0, 0);
    }

    public static boolean isValid(int x, int y) {
        return 0 <= x && x <= MAX_X && 0 <= y && y <= MAX_Y;
    }

    @Override
    public int compareTo(Vector2D o) {
        if (o == null) {
            return -1;
        }
        // The hashCodes are unique for every x,y combination
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        return this.y * (MAX_X + 1) + this.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        Vector2D vector = (Vector2D) o;
        // When x and y are equal, their hashCodes are equal
        return this.hashCode() == vector.hashCode();
    }

    @Override
    public String toString() {
        char xChar = (char) ('a' + this.x);
        return "" + xChar + this.y;
    }
}
