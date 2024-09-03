package com.ethpalser.chess.piece;

public enum Colour {
    WHITE,
    BLACK;

    public String toCode() {
        if (WHITE.equals(this)) {
            return "w";
        } else {
            return "b";
        }
    }

    public static Colour opposite(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

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
