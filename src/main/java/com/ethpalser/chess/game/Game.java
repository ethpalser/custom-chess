package com.ethpalser.chess.game;

public interface Game {

    GameInfo info();

    GameContext context();

    GameStatus status();

    int turn();

    int score();

    GameStatus update(Action action);

    GameStatus undo();

    GameStatus redo();

    Iterable<Action> potentialUpdates();

    GameSaveData createSaveData();

}
