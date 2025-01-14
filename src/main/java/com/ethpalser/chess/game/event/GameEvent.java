package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.piece.Colour;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-19",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public interface GameEvent {

    Colour player();

    String choice();

    EventType type();

    void execute(GameContext context);

    void unExecute(GameContext context);

}
