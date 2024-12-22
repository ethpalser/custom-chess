package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;

public interface Game {

    Board<Coordinate> getBoard();

    Log<Coordinate, Piece> getLog();

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

    // #### NEW METHODS ####

    GameInfo info();
}
