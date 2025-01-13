package com.ethpalser.chess.game.state;

import com.ethpalser.chess.exception.UnsupportedEventException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.event.GameEvent;
import java.util.List;

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
