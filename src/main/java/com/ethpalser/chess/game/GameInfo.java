package com.ethpalser.chess.game;

import com.ethpalser.chess.game.context.GameContext;

public record GameInfo(
        int turn,
        int score,
        GameStatus status,
        GameContext context
) {
}
