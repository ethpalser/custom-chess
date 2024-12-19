package com.ethpalser.chess.game.event;

import com.ethpalser.chess.space.Coordinate;

public record PromoteEvent(Coordinate source, String code) implements GameEvent {
    @Override
    public EventType type() {
        return EventType.PROMOTE;
    }
}
