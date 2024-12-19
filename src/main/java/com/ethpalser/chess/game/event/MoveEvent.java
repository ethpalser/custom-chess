package com.ethpalser.chess.game.event;

import com.ethpalser.chess.space.Coordinate;

public record MoveEvent(Coordinate source, Coordinate target) implements GameEvent {
    @Override
    public EventType type() {
        return EventType.MOVE;
    }
}
