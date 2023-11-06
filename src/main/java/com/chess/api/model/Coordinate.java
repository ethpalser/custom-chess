package com.chess.api.model;

import java.util.Locale;
import lombok.Getter;

@Getter
public class Coordinate {

    private final int posX;
    private final int posY;

    public Coordinate(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            throw new IndexOutOfBoundsException();
        }

        posX = x;
        posY = y;
    }

    public static Coordinate parseString(String str) {
        if (str.length() != 2) {
            throw new IllegalArgumentException();
        }
        char[] chars = str.toLowerCase(Locale.ROOT).toCharArray();
        int x = chars[0] - 'a';
        int y = chars[1] - '1';
        return new Coordinate(x, y);
    }

}
