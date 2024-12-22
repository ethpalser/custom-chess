package com.ethpalser.chess.game.state;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.event.EventType;
import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.PromoteEvent;
import java.util.List;

public class AwaitState implements GameState {

    private final GameContext context;
    private final EventType eventType;
    private final List<String> options;

    public AwaitState(GameContext context, EventType eventType, List<String> options) {
        if (context == null || eventType == null || options == null || options.size() <= 1) {
            throw new IllegalArgumentException("One or more arguments are null, or there are less than two options");
        }
        this.context = context;
        this.eventType = eventType;
        this.options = options;
    }

    @Override
    public GameState update(GameEvent event) {
        if (event.type() != this.eventType) {
            throw new IllegalArgumentException("Event type does not match expected type");
        }
        if (!this.options.contains(event.choice())) {
            throw new IllegalArgumentException("Event choice is not one of expected choices");
        }
        switch (event.type()) {
            case MOVE, PROMOTE -> {
                // We no longer need the prompt use to start this wait state
                this.context.clearPrompt();
                event.execute(this.context);
                // Check if there is a new prompt to follow-up on, and then create another AwaitState if there is one
                GamePrompt newPrompt = this.context.getPrompt();
                if (newPrompt != null) {
                    return new AwaitState(this.context, newPrompt.eventType(), newPrompt.choices());
                }
            }
            default -> throw new IllegalActionException("Event not supported by the current game state");
        }
        return new ReadyState(this.context);
    }

    @Override
    public Iterable<GameEvent> updates() {
        return null;
    }

    @Override
    public GameState undo() {
        return null;
    }

    @Override
    public GameState redo() {
        return null;
    }
}
