package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-30",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public class GameEventProxy implements GameEvent {

    private final Colour player;
    private final ChessNotation eventNotation;

    private GameEvent event;
    private final GameEvent next; // A notation can be an aggregate of many events, so there may be another

    public GameEventProxy(Colour player, ChessNotation chessNotation) {
        if (player == null || chessNotation == null) {
            throw new IllegalArgumentException("notation cannot be null for proxy");
        }
        this.player = player;
        this.eventNotation = chessNotation;
        this.event = null;
        this.next = null;
    }

    private GameEventProxy(Colour player, GameEvent event, GameEvent next) {
        if (player == null || event == null) {
            throw new IllegalArgumentException("event cannot be null for proxy");
        }
        this.player = player;
        this.eventNotation = null; // Not needed
        this.event = event;
        this.next = next;
    }

    @Override
    public Colour player() {
        return this.player;
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
        if (this.eventNotation == null) {
            throw new IllegalStateException("GameEventProxy in illegal state with both a null event and notation");
        }
        // Notation is only null when this class is constructed with a null event, which throws an exception
        ChessRecord chessRecord = this.eventNotation.toRecord();
        if (chessRecord.source() == null) {
            throw new IllegalStateException("Failed to determine initial piece coordinate from chess notation.");
        }

        GameEvent promoteEvent;
        // Promotions are always last to execute, first to un-execute for all Chess Notations
        if (chessRecord.promoteCode() != null) {
            promoteEvent = new GameEventProxy(this.player, new PromoteEvent(this.player, chessRecord.source(),
                    chessRecord.promoteCode()), null);
        } else {
            promoteEvent = null;
        }

        // Movements are always first to execute, last to un-execute
        if (chessRecord.target() != null) {
            // This event is a primitive LinkedList of GameEventProxy, and uses the inner event for execute/un-execute
            return new GameEventProxy(this.player, new MoveEvent(this.player, chessRecord.source(),
                    chessRecord.target()), promoteEvent);
        } else {
            throw new IllegalStateException("Failed to create an event from chess notation.");
        }
    }
}
