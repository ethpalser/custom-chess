package com.ethpalser.chess.game;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.GameContext;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-22",
        majorVersion = 1,
        minorVersion = 2,
        lastModified = "2025-01-13"
)
public record GameInfo(
        int turn,
        int score,
        GameStatus status,
        GameContext context
) {
}
