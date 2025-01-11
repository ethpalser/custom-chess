package com.ethpalser.chess.game.log;

import java.util.Iterator;
import java.util.LinkedList;

public class GameLog<E> implements Iterable<E> {

    private final LinkedList<E> log; // Stack of added items
    private final LinkedList<E> history; // Stack of removed items

    public GameLog() {
        this.log = new LinkedList<>();
        this.history = new LinkedList<>();
    }

    public void push(E item) {
        this.history.clear();
        this.log.add(item);
    }

    public E peek() {
        if (this.log.isEmpty()) {
            return null;
        }
        return this.log.get(this.log.size() - 1);
    }

    public E pop() {
        if (this.log.isEmpty()) {
            throw new NullPointerException("Cannot remove item, as log is empty");
        }
        this.history.add(this.log.getLast());
        return this.log.removeLast();
    }

    public E redo() {
        if (this.history.isEmpty()) {
            throw new NullPointerException("Cannot redo item, as undo history is empty");
        }
        this.log.add(this.history.getLast());
        return this.history.removeLast();
    }

    public int size() {
        return this.log.size();
    }

    public boolean isEmpty() {
        return this.log.isEmpty();
    }

    public void clear() {
        this.log.clear();
        this.history.clear();
    }

    public Iterator<E> iterator() {
        return this.log.iterator();
    }

}
