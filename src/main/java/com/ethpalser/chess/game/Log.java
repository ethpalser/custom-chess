package com.ethpalser.chess.game;

import java.util.Collection;

public interface Log extends Collection<LogEntry> {

    void push(LogEntry item);

    LogEntry peek();

    LogEntry pop();

    LogEntry undo();

    LogEntry redo();

}
