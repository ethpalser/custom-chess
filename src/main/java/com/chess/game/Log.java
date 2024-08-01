package com.chess.game;

import com.chess.game.movement.ActionRecord;
import com.chess.game.piece.Piece;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class Log implements Collection<ActionRecord> {

    private final Deque<ActionRecord> logStack;
    private final Deque<ActionRecord> undoStack;

    public Log() {
        this.logStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
    }

    public Log(Space2D<Piece> board, List<String> logStrings) {
        this();
        for (String s : logStrings) {
            ActionRecord rec = new ActionRecord(board, s);
            this.logStack.push(rec);
            // Manually move the piece on the board, ignoring all checks
            board.put(rec.getAction().getEnd(), board.remove(rec.getAction().getStart()));
        }
    }

    @Override
    public boolean addAll(Collection<? extends ActionRecord> c) {
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
    public Iterator<ActionRecord> iterator() {
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
    public boolean add(ActionRecord actionRecord) {
        return this.logStack.add(actionRecord);
    }

    public ActionRecord peek() {
        return this.logStack.peek();
    }

    public void push(ActionRecord actionRecord) {
        this.logStack.push(actionRecord);
    }

    public ActionRecord pop() {
        return this.logStack.pop();
    }

    public void undo() {
        if (!this.logStack.isEmpty()) {
            this.undoStack.push(this.logStack.pop());
        }
    }

    public void redo() {
        if (!this.undoStack.isEmpty()) {
            this.logStack.push(this.undoStack.pop());
        }
    }

    public void restore() {
        while (!this.undoStack.isEmpty()) {
            this.redo();
        }
    }

}
