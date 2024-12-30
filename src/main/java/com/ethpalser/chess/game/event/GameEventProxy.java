package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;

public class GameEventProxy implements GameEvent {

    private final ChessNotation eventNotation;

    private GameEvent event;

    public GameEventProxy(ChessNotation chessNotation) {
        if (chessNotation == null) {
            throw new IllegalArgumentException();
        }
        this.eventNotation = chessNotation;
    }

    @Override
    public String choice() {
        if (this.event == null) {
            this.event = this.createFromProxy();
        }
        return this.event.choice();
    }

    @Override
    public EventType type() {
        if (this.event == null) {
            this.event = this.createFromProxy();
        }
        return this.event.type();
    }

    @Override
    public void execute(GameContext context) {
        if (this.event == null) {
            this.event = this.createFromProxy();
        }
        this.event.execute(context);
    }

    @Override
    public void unExecute(GameContext context) {
        if (this.event == null) {
            this.event = this.createFromProxy();
        }
        this.event.unExecute(context);
    }

    private GameEvent createFromProxy() {
        // Re-creating this event is not allowed, as only the first is necessary
        if (this.event != null) {
            return this.event;
        }
        ChessRecord chessRecord = this.eventNotation.toRecord();
        if (chessRecord.source() == null) {
            throw new IllegalStateException("Failed to determine initial piece coordinate from chess notation.");
        }

        if (chessRecord.promoteCode() != null) {
            return new PromoteEvent(chessRecord.source(), chessRecord.promoteCode());
        } else if (chessRecord.target() != null) {
            return new MoveEvent(chessRecord.source(), chessRecord.target());
        } else {
            throw new IllegalStateException("Failed to create an event from chess notation.");
        }
    }
}
