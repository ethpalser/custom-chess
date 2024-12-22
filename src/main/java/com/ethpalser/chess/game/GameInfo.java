package com.ethpalser.chess.game;

public record GameInfo(
        int turn,
        int score,
        GameStatus status,
        GameContext context
) {
}
