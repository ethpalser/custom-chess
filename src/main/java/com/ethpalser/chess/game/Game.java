package com.ethpalser.chess.game;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.exception.IllegalResultException;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.game.event.Action;
import com.ethpalser.chess.game.event.GameEvent;


@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-12-01",
        majorVersion = 3,
        minorVersion = 3,
        lastModified = "2025-01-13"
)
public interface Game {

    GameInfo info();

    GameContext context();

    GameStatus status();

    int turn();

    int score();

    /**
     * Update the game by performing a movement according to the start and end coordinates provided by Action.
     *
     * @param action A record representing information for a movement, replaced by MoveEvent.
     * @return GameStatus enum representing the result of the game update.
     * @deprecated Since January 12, 2025
     */
    @Deprecated(since = "2025-01-12")
    GameStatus update(Action action);

    GameStatus update(GameEvent event);

    GameStatus undo() throws IllegalResultException;

    GameStatus redo() throws IllegalResultException;

    Iterable<Action> potentialUpdates();

    GameSaveData createSaveData();

}
