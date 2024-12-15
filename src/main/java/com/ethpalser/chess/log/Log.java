package com.ethpalser.chess.log;

import com.ethpalser.chess.space.Coordinate;
import java.util.Collection;

public interface Log<T extends Coordinate, U> extends Collection<LogEntry<T, U>> {

    void push(LogEntry<T, U> item);

    LogEntry<T, U> peek();

    LogEntry<T, U> pop();

    LogEntry<T, U> undo();

    LogEntry<T, U> redo();

}
