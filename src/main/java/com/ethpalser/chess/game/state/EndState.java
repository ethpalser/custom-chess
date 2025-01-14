package com.ethpalser.chess.game.state;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.exception.UnsupportedEventException;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.game.event.GameEvent;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-21",
        majorVersion = 1,
        minorVersion = 3,
        lastModified = "2025-01-13"
)
public class EndState implements GameState {

    private final GameContext context;

    public EndState(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException("One or more arguments is null");
        }
        this.context = context;
    }

    @Override
    public GameState update(GameEvent event) {
        throw new UnsupportedEventException();
    }

    @Override
    public Iterable<GameEvent> updates() {
        return List.of();
    }
}
