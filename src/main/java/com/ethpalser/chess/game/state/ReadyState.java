package com.ethpalser.chess.game.state;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.event.EventType;
import com.ethpalser.chess.game.event.GameEvent;

public class ReadyState implements GameState {

    private final GameContext context;

    public ReadyState(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException("One or more arguments is null");
        }
        this.context = context;
    }

    @Override
    public GameState update(GameEvent event) {
        if (EventType.MOVE.equals(event.type())) {
            event.execute(this.context);
            GamePrompt prompt = this.context.getPrompt();
            if (prompt != null) {
                return new AwaitState(this.context, prompt.eventType(), prompt.choices());
            }
        } else {
            throw new IllegalActionException("Event not supported by the current game state");
        }
        return new ReadyState(this.context);
    }

    @Override
    public Iterable<GameEvent> updates() {
        return null;
    }
}
