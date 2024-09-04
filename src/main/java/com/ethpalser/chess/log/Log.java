package com.ethpalser.chess.log;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import java.util.Collection;
import java.util.List;

public interface Log<T extends Comparable<T>, U> extends Collection<LogEntry<T, U>> {

    void addAll(Plane<Piece> board, List<String> entryStrings);

    void push(LogEntry<T, U> item);

    LogEntry<T, U> peek();

    LogEntry<T, U> pop();

    LogEntry<T, U> undo();

    LogEntry<T, U> redo();

}
