package com.ethpalser.chess.game;

import java.util.Collection;

public interface ChessLog extends Collection<LogRecord> {

    void push(LogRecord item);

    LogRecord peek();

    LogRecord pop();

    LogRecord undo();

    LogRecord redo();

}
