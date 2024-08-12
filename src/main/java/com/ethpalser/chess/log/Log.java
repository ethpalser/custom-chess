package com.ethpalser.chess.log;

import java.util.Collection;

public interface Log<T extends Comparable<T>, U> extends Collection<LogEntry<T, U>> {

    void push(LogEntry<T, U> item);

    LogEntry<T, U> peek();

    LogEntry<T, U> pop();

    LogEntry<T, U> undo();

    LogEntry<T, U> redo();

}
