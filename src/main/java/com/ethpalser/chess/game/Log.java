package com.ethpalser.chess.game;

import java.util.Collection;

public interface Log extends Collection<MoveRecord> {

    void push(MoveRecord item);

    MoveRecord peek();

    MoveRecord pop();

    MoveRecord undo();

    MoveRecord redo();

}
