package com.ethpalser.chess.game;

public interface Game {

    GameInfo info();

    GameContext context();

    GameStatus getStatus();

    int getTurn();

    GameStatus updateGame(Action action);

    GameStatus undo();

    GameStatus redo();

    Iterable<Action> potentialUpdates();

    int evaluateState();

    GameSaveData createSaveData();

}
