package com.ethpalser.chess.piece;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public enum PieceType {
    PAWN("P"),
    ROOK("R"),
    KNIGHT("N"),
    BISHOP("B"),
    QUEEN("Q"),
    KING("K"),
    CUSTOM("(custom)"),
    INVALID("_");

    private final String code;

    PieceType(String code) {
        this.code = code;
    }

    public String toCode() {
        return code;
    }

    public static PieceType fromCode(String code) {
        return switch (code) {
            case "_" -> INVALID;
            case "P", "" -> PAWN;
            case "R" -> ROOK;
            case "N" -> KNIGHT;
            case "B" -> BISHOP;
            case "Q" -> QUEEN;
            case "K" -> KING;
            default -> CUSTOM;
        };
    }
}
