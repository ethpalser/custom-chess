package com.ethpalser.chess.game;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.piece.Colour;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-08-10",
        majorVersion = 1,
        minorVersion = 2,
        lastModified = "2025-01-13"
)
public enum GameStatus {

    PENDING,
    ONGOING,
    WHITE_IN_CHECK,
    BLACK_IN_CHECK,
    WHITE_WIN,
    BLACK_WIN,
    STALEMATE,
    NO_CHANGE;

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
