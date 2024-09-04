package com.ethpalser.chess.log;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class ChessLog implements Log<Point, Piece> {

    private final Deque<LogEntry<Point, Piece>> logStack;
    private final Deque<LogEntry<Point, Piece>> undoStack;

    public ChessLog() {
        this.logStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
    }

    public void addAll(Plane<Piece> board, List<String> logStrings) {
        for (String s : logStrings) {
            ChessLogEntry rec = new ChessLogEntry(board, s);
            this.logStack.push(rec);
        }
    }

    @Override
    public boolean addAll(Collection<? extends LogEntry<Point, Piece>> c) {
        return this.logStack.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.logStack.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.logStack.retainAll(c);
    }

    @Override
    public void clear() {
        this.logStack.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ChessLog)) {
            return false;
        }
        return this.logStack.equals(((ChessLog) o).logStack) && this.undoStack.equals(((ChessLog) o).undoStack);
    }

    @Override
    public int hashCode() {
        return this.logStack.hashCode();
    }

    @Override
    public boolean remove(Object o) {
        return this.logStack.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!(o instanceof LogEntry)) {
                return false;
            }
            boolean exists = false;
            for (LogEntry<Point, Piece> entry : this.logStack) {
                if (entry.equals(o)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return this.logStack.contains(o);
    }

    @Override
    public int size() {
        return this.logStack.size();
    }

    @Override
    public boolean isEmpty() {
        return this.logStack.isEmpty();
    }

    @Override
    public Iterator<LogEntry<Point, Piece>> iterator() {
        return this.logStack.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.logStack.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.logStack.toArray(a);
    }

    @Override
    public boolean add(LogEntry<Point, Piece> actionRecord) {
        return this.logStack.add(actionRecord);
    }

    @Override
    public LogEntry<Point, Piece> peek() {
        return this.logStack.peek();
    }

    @Override
    public void push(LogEntry<Point, Piece> actionRecord) {
        if (actionRecord != null) {
            this.logStack.push(actionRecord);
        }
    }

    @Override
    public LogEntry<Point, Piece> pop() {
        if (!logStack.isEmpty()) {
            return this.logStack.pop();
        }
        return null;
    }

    @Override
    public LogEntry<Point, Piece> undo() {
        if (!this.logStack.isEmpty()) {
            LogEntry<Point, Piece> logEntry = this.logStack.pop();
            this.undoStack.push(logEntry);
            return logEntry;
        }
        return null;
    }

    @Override
    public LogEntry<Point, Piece> redo() {
        if (!this.undoStack.isEmpty()) {
            LogEntry<Point, Piece> logEntry = this.undoStack.pop();
            this.logStack.push(logEntry);
            return logEntry;
        }
        return null;
    }
}
