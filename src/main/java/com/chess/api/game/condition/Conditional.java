package com.chess.api.game.condition;

import com.chess.api.game.Board;
import com.chess.api.game.movement.Action;

public interface Conditional {

    boolean isExpected(Board board, Action action);

}
