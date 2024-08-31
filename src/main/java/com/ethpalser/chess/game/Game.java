package com.ethpalser.chess.game;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;

public interface Game {

    Log<Point, Piece> getLog();

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

}
