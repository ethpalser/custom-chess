package com.ethpalser.chess.game;

public interface Game {

    GameStatus updateGame(Action action);

    GameStatus getStatus();

    GameStatus undoUpdate(int changesToUndo, boolean saveForRedo);

    default GameStatus undoUpdate() {
        return this.undoUpdate(1, true);
    }

    GameStatus redoUpdate(int changesToRedo);

    default GameStatus redoUpdate() {
        return this.redoUpdate(1);
    }

    Iterable<Action> potentialUpdates();

    int evaluateState();

}
