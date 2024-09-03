package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.view.ConditionalView;

/**
 * Conditional describes an object that has conditions, rule or criteria, that it contains. The criteria will take
 * the current state of the board to determine if the expected criteria is met.
 */
public interface Conditional<T extends Positional> {

    boolean isExpected(Plane<T> plane);

    ConditionalView toView();

}
