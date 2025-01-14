package com.ethpalser.chess.game.state;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.event.GameEvent;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-21",
        lastModified = "2025-01-13"
)
public interface GameState {

    GameState update(GameEvent event);

    Iterable<GameEvent> updates();

}
