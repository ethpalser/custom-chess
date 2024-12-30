package com.ethpalser.chess.move.notation;

public class ChessNotation {
    // Store the expected notation that must be built
    private final ChessNotationFormat format;
    private final String string;

    /**
     * Create a ChessNotation string using an action's alias. This alias should be well known, or it will not
     * be understood by players. For example, the King's Castle actions have aliases O-O-O or O-O for queen-side
     * castle and king-side castle respectively.
     *
     * @param alias String of a specific, unique chess notation
     */
    public ChessNotation(ChessNotationFormat chessFormat, ChessRecord chessRecord, ChessNotationAlias alias) {
        this.format = chessFormat;
        this.string = chessFormat.format(chessRecord, alias);
    }

    public ChessNotation(ChessNotationFormat chessFormat, ChessRecord chessRecord) {
        if (chessFormat == null || chessRecord == null) {
            throw new IllegalArgumentException();
        }
        this.format = chessFormat;
        this.string = chessFormat.format(chessRecord);
    }

    public ChessRecord toRecord() {
        return this.format.parse(this.string);
    }

    @Override
    public String toString() {
        return this.string;
    }

}
