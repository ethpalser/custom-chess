package com.ethpalser.chess.piece.custom;

public enum PieceType {
    PAWN("P"),
    ROOK("R"),
    KNIGHT("N"),
    BISHOP("B"),
    QUEEN("Q"),
    KING("K"),
    CUSTOM("~custom~");

    private final String code;

    PieceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PieceType fromCode(String code) {
        return switch (code) {
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
