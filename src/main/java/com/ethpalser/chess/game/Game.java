package com.ethpalser.chess.game;

public interface Game {

    GameInfo info();

    GameStatus getStatus();

    int getTurn();

    GameStatus updateGame(Action action);

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

    String toJson();
}
