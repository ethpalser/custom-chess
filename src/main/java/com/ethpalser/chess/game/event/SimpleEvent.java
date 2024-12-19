package com.ethpalser.chess.game.event;

public record SimpleEvent(EventType type) implements GameEvent {

    public SimpleEvent() {
        this(EventType.NO_EVENT);
    }

}
