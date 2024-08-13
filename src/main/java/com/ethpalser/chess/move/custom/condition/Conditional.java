package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;

/**
 * Conditional describes an object that has conditions, rule or criteria, that it contains. The criteria will take
 * the current state of the board and the action being attempted to determine if the expected criteria is met.
 */
public interface Conditional {

    boolean isExpected(CustomBoard board, Action action);

}
