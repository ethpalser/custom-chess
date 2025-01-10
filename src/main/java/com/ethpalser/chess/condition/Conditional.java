package com.ethpalser.chess.condition;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.space.Coordinate;

/**
 * Conditional classes are functional objects that describe the rules that are expected to pass, using the GameContext
 * for information and the coordinate this condition is being applied to. Often, this coordinate this condition will
 * apply to is a piece's coordinate.
 */
public interface Conditional {

    boolean isExpected(GameContext.Record context, Coordinate appliedTo);

}
