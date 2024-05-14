package com.chess.game;

public enum Colour {
    WHITE,
    BLACK;

    public static Colour fromCode(String string) {
        if (string == null || string.length() != 1) {
            throw new IllegalArgumentException();
        }
        if (string.equalsIgnoreCase("w"))
            return WHITE;
        else if (string.equalsIgnoreCase("b"))
            return BLACK;
        else
            throw new IllegalArgumentException();
    }
}
