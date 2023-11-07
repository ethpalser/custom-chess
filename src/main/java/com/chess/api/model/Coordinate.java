package com.chess.api.model;

import java.util.Locale;
import lombok.Getter;

@Getter
public class Coordinate {

    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
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

}
