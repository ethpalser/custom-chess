package com.ethpalser.chess.move.notation;

public enum ChessNotationAlias {

    QUEEN_SIDE_CASTLE("O-O-O"),
    KING_SIDE_CASTLE("O-O"),
    NOT_AN_ALIAS("");

    private final String alias;

    ChessNotationAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return this.alias;
    }

    public static ChessNotationAlias fromString(String str) {
        for (ChessNotationAlias alias : ChessNotationAlias.values()) {
            if (alias.toString().equals(str)) {
                return alias;
            }
        }
        return NOT_AN_ALIAS;
    }
}
