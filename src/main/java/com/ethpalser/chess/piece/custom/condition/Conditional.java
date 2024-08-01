package com.ethpalser.chess.piece.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.Action;

/**
 * Conditional describes an object that has conditions, rule or criteria, that it contains. The criteria will take
 * the current state of the board and the action being attempted to determine if the expected criteria is met.
 */
public interface Conditional {

    boolean isExpected(Board board, Action action);

}
