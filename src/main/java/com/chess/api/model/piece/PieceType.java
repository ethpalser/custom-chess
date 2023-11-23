package com.chess.api.model.piece;

public enum PieceType {
    PAWN("p"),
    ROOK("r"),
    KNIGHT("k"),
    BISHOP("b"),
    QUEEN("q"),
    KING("k"),
    CUSTOM("z");

    private final String code;

    PieceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
