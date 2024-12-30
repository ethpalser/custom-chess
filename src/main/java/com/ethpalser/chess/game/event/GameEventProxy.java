package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;

public class GameEventProxy implements GameEvent {

    private final ChessNotation eventNotation;

    private GameEvent event;
    private GameEvent next; // A notation can be an aggregate of many events, so there may be another

    public GameEventProxy(ChessNotation chessNotation) {
        if (chessNotation == null) {
            throw new IllegalArgumentException();
        }
        this.eventNotation = chessNotation;
        this.event = null;
        this.next = null;
    }

    private GameEventProxy(GameEvent event, GameEvent next) {
        this.eventNotation = null; // Not needed
        this.event = event;
        this.next = next;
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
        if (this.next != null) {
            this.next.execute(context); // continue executing down the linked-list
        }
    }

    @Override
    public void unExecute(GameContext context) {
        if (this.event == null) {
            this.event = this.createFromProxy();
        }
        if (this.next != null) {
            this.next.unExecute(context); // un-execute from the end of the linked-list first, then each on the way back
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

        GameEvent gameEvent = null;
        // Movements are always first to execute, last to un-execute
        if (chessRecord.target() != null) {
            gameEvent = new GameEventProxy(new MoveEvent(chessRecord.source(), chessRecord.target()), gameEvent);
        } else {
            throw new IllegalStateException("Failed to create an event from chess notation.");
        }
        // Promotions are always last to execute, first to un-execute for all Chess Notations
        if (chessRecord.promoteCode() != null) {
            gameEvent = new GameEventProxy(new PromoteEvent(chessRecord.source(), chessRecord.promoteCode()), null);
        }
        // This GameEvent is a primitive LinkedList of GameEventProxy, and uses the inner event for execute/un-execute
        return gameEvent;
    }
}
