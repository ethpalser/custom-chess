package com.ethpalser.chess.game;

import com.ethpalser.chess.piece.Colour;

public enum GameStatus {

    PENDING,
    ONGOING,
    WHITE_IN_CHECK,
    BLACK_IN_CHECK,
    WHITE_WIN,
    BLACK_WIN,
    STALEMATE;

    public static boolean isCompletedGameStatus(GameStatus status) {
        return switch (status) {
            case WHITE_WIN, BLACK_WIN, STALEMATE -> true;
            default -> false;
        };
    }

    public static GameStatus colourWinStatus(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return WHITE_WIN;
        } else {
            return BLACK_WIN;
        }
    }

    public static GameStatus colourInCheckStatus(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return WHITE_IN_CHECK;
        } else {
            return BLACK_IN_CHECK;
        }
    }

}
