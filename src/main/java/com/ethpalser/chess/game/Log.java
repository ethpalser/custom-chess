package com.ethpalser.chess.game;

import java.util.Collection;

public interface Log extends Collection<LogRecord> {

    void push(LogRecord item);

    LogRecord peek();

    LogRecord pop();

    LogRecord undo();

    LogRecord redo();

}
