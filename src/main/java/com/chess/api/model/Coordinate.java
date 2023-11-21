package com.chess.api.model;

import java.util.Locale;
import lombok.Getter;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;

@Getter
public class Coordinate implements Comparable<Coordinate> {

    public static final int MAX_X = 7;
    public static final int MAX_Y = 7;

    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        if (x > MAX_X || x < 0 || y > MAX_Y || y < 0) {
            throw new IndexOutOfBoundsException();
        }

        this.x = x;
        this.y = y;
    }

    public Coordinate(char x, char y) {
        this(x - 'a', y - '1');
    }

    public static Coordinate parseString(String str) {
        if (str.length() != 2) {
            throw new IllegalArgumentException();
        }
        char[] chars = str.toLowerCase(Locale.ROOT).toCharArray();
        return new Coordinate(chars[0], chars[1]);
    }

    @Override
    public int compareTo(Coordinate o) {
        if (o == null) {
            return -1;
        }
        return (this.y * MAX_X + this.x) - (o.getY() * MAX_X + o.getX());
    }

    @Override
    public int hashCode() {
        return this.y * MAX_X + this.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        Coordinate coordinate = (Coordinate) o;
        if (coordinate.getX() != this.x)
            return false;
        if (coordinate.getY() != this.y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        char xChar = (char) ('a' + this.x);
        return "" + xChar + this.y;
    }
}
