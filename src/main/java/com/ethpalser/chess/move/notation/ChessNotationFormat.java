package com.ethpalser.chess.move.notation;

public interface ChessNotationFormat {

    String format(ChessRecord chessRecord);

    /**
     * Formats the chess record with an alias provided to replace a portion of its string. This format should know
     * how to handle the alias, or use everything provided by the record.
     *
     * @param chessRecord Record of everything involved in a change of state for a chess game
     * @param alias String representing a simplified version of a state change.
     * @return String using the alias
     */
    String format(ChessRecord chessRecord, ChessNotationAlias alias);

    ChessRecord parse(String chessNotation);

}
