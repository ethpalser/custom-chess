package com.ethpalser.chess.game.event;

import com.ethpalser.chess.piece.Colour;

public record ConcedeEvent(Colour player) implements GameEvent {
    @Override
    public EventType type() {
        return EventType.CONCEDE;
    }
}
