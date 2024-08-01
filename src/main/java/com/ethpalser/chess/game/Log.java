package com.ethpalser.chess.game;

import com.ethpalser.chess.board.ChessBoard;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class Log implements ChessLog {

    private final Deque<LogRecord> logStack;
    private final Deque<LogRecord> undoStack;

    public Log() {
        this.logStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
    }

    public Log(ChessBoard board, List<String> logStrings) {
        this();
        for (String s : logStrings) {
            ActionRecord rec = new ActionRecord(board, s);
            this.logStack.push(rec);
            // Manually move the piece on the board, ignoring all checks
            board.movePiece(rec.getStart(), rec.getEnd());
        }
    }

    @Override
    public boolean addAll(Collection<? extends LogRecord> c) {
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
        return this.logStack.equals(o);
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
        return this.logStack.contains(c);
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
    public Iterator<LogRecord> iterator() {
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
    public boolean add(LogRecord actionRecord) {
        return this.logStack.add(actionRecord);
    }

    @Override
    public LogRecord peek() {
        return this.logStack.peek();
    }

    @Override
    public void push(LogRecord actionRecord) {
        this.logStack.push(actionRecord);
    }

    @Override
    public LogRecord pop() {
        return this.logStack.pop();
    }

    @Override
    public void undo() {
        if (!this.logStack.isEmpty()) {
            this.undoStack.push(this.logStack.pop());
        }
    }

    @Override
    public void redo() {
        if (!this.undoStack.isEmpty()) {
            this.logStack.push(this.undoStack.pop());
        }
    }
}
