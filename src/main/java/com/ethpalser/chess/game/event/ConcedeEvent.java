package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.piece.Colour;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-19",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
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
        // todo: Update the game's state to the opponent's win, and the game is at an end state
    }

    @Override
    public void unExecute(GameContext context) {
        // todo: Not allowed
    }
}
