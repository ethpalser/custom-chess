package com.chess.api.model;

import java.util.Locale;
import lombok.Getter;

@Getter
public class Coordinate implements Comparable<Coordinate> {

    private static final int MAX_X = 7;
    private static final int MAX_Y = 7;
    private static final int MIN_X = 0;
    private static final int MIN_Y = 0;

    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        if (x > MAX_X || x < MIN_X || y > MAX_Y || y < MIN_Y) {
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
        return (this.getY() * MAX_X + this.getX()) - (o.getY() * MAX_X + o.getX());
    }

    public boolean equals(Coordinate coordinate) {
        return this.compareTo(coordinate) == 0;
    }
}
