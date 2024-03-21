package main.java.com.chess.game.condition;

import main.java.com.chess.game.Board;
import main.java.com.chess.game.movement.Action;

/**
 * Conditional describes an object that has conditions, rule or criteria, that it contains. The criteria will take
 * the current state of the board and the action being attempted to determine if the expected criteria is met.
 */
public interface Conditional {

    boolean isExpected(Board board, Action action);

}
