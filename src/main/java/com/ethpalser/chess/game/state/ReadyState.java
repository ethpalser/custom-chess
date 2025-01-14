package com.ethpalser.chess.game.state;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.exception.UnsupportedEventException;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.game.event.EventType;
import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.MoveFollowUpEvent;
import com.ethpalser.chess.game.event.PromoteEvent;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-21",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public class ReadyState implements GameState {

    private final GameContext context;

    public ReadyState(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException("One or more arguments is null");
        }
        this.context = context;
    }

    @Override
    public GameState update(GameEvent event) {
        if (EventType.MOVE.equals(event.type())) {
            event.execute(this.context);
            GamePrompt prompt = this.context.getPrompt();
            if (prompt != null) {
                return this.handlePrompt(event.player(), prompt);
            } else {
                return new ReadyState(this.context);
            }
        } else {
            throw new UnsupportedEventException();
        }
    }

    private GameState handlePrompt(Colour player, GamePrompt prompt) {
        Piece source = context.getBoard().get(prompt.source());
        boolean isPawn = PieceType.PAWN.toCode().equals(source.getCode());

        List<String> choices;
        if (EventType.PROMOTE.equals(prompt.eventType()) && isPawn) {
            choices = List.of("Q"); // Temporary. This is to automatically promote it to a queen
        } else {
            choices = prompt.choices();
        }

        if (choices.size() <= 1) {
            this.context.clearPrompt();
            String choice = prompt.choices().isEmpty() ? null : prompt.choices().get(0);
            // Create the event to automatically perform
            if (EventType.MOVE_FOLLOW_UP.equals(prompt.eventType())) {
                Coordinate target;
                if (choice == null) {
                    target = null; // This should remove the piece
                } else {
                    target = new Point(choice);
                }
                new MoveFollowUpEvent(player, prompt.source(), target).execute(this.context);
            } else if (EventType.PROMOTE.equals(prompt.eventType())) {
                if (choice == null) {
                    throw new IllegalArgumentException("Cannot promote with a null choice");
                }
                new PromoteEvent(player, prompt.source(), prompt.choices().get(0)).execute(this.context);
            }
            // Another prompt may have been raised
            GamePrompt another = this.context.getPrompt();
            if (another != null) {
                return new AwaitState(this.context, another.eventType(), another.choices());
            }
        } else {
            return new AwaitState(this.context, prompt.eventType(), prompt.choices());
        }
        return new ReadyState(this.context);
    }

    @Override
    public Iterable<GameEvent> updates() {
        return null;
    }
}
