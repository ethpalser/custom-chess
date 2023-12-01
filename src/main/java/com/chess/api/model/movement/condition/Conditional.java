package com.chess.api.model.movement.condition;

import com.chess.api.model.Action;
import com.chess.api.model.Board;

public interface Conditional {

    boolean isExpected(Board board, Action action);

}
