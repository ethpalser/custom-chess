package com.ethpalser.chess.game.event;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.space.Coordinate;

public class PromoteEvent implements GameEvent {

    private final Coordinate source;
    private final String code;

    public PromoteEvent(Coordinate source, String pieceCode) {
        if (source == null || pieceCode == null) {
            throw new IllegalArgumentException();
        }
        this.source = source;
        this.code = pieceCode;
    }

    @Override
    public EventType type() {
        return EventType.PROMOTE;
    }

    @Override
    public void execute(GameContext context) {

    }

    @Override
    public void unExecute(GameContext context) {

    }
}
