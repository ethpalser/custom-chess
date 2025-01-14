package com.ethpalser.chess.piece;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        minorVersion = 3,
        lastModified = "2025-01-13"
)
public enum Colour {
    WHITE,
    BLACK,
    NO_COLOUR;

    public String toCode() {
        if (WHITE.equals(this)) {
            return "w";
        } else if (BLACK.equals(this)) {
            return "b";
        } else {
            return "";
        }
    }

    public static Colour opposite(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    public static Colour fromCode(String string) throws IllegalArgumentException {
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
