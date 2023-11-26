package com.chess.api.model.piece;

public enum PieceType {
    PAWN(""),
    ROOK("R"),
    KNIGHT("N"),
    BISHOP("B"),
    QUEEN("Q"),
    KING("K");

    private final String code;

    PieceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
