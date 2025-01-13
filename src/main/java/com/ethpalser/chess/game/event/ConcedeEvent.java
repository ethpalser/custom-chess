package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Colour;

public class ConcedeEvent implements GameEvent {

    private final Colour player;

    public ConcedeEvent(Colour player) {
        if (player == null) {
            throw new IllegalArgumentException();
        }
        this.player = player;
    }

    public Colour player() {
        return this.player;
    }

    @Override
    public String choice() {
        return this.player.toString();
    }

    @Override
    public EventType type() {
        return EventType.CONCEDE;
    }

    @Override
    public void execute(GameContext context) {

    }

    @Override
    public void unExecute(GameContext context) {

    }
}
