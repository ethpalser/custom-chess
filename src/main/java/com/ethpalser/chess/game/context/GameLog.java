package com.ethpalser.chess.game.context;

import java.util.Iterator;
import java.util.LinkedList;

public class GameLog<E> implements Iterable<E> {

    private final LinkedList<E> log; // Stack of added items
    private final LinkedList<E> history; // Stack of removed items

    public GameLog() {
        this.log = new LinkedList<>();
        this.history = new LinkedList<>();
    }

    public GameLog(GameLog<E> original) {
        this.log = new LinkedList<>();
        this.log.addAll(original.log);
        this.history = new LinkedList<>();
        this.history.addAll(original.history);
    }

    public void push(E item) {
        // This item is a historical record being reapplied
        if (!history.isEmpty() && history.getLast().equals(item)) {
            // Only remove what is reapplied
            this.history.removeLast();
        } else {
            // Otherwise, the history must be cleared to disallow redo with a different sequence of items
            this.history.clear();
        }
        this.log.add(item);
    }

    public E peek() {
        if (this.log.isEmpty()) {
            return null;
        }
        return this.log.getLast();
    }

    public E pop() {
        if (this.log.isEmpty()) {
            return null;
        }
        this.history.add(this.log.getLast());
        return this.log.removeLast();
    }

    /**
     * A game log's undone items are those that were previously applied, but are currently not applied. This state
     * is a result of a log removing a record and not updating its state away from its once-added items.
     *
     * @return The most recent item removed. Otherwise, null, as it is at the most recent update.
     */
    public E peekUndone() {
        if (this.history.isEmpty()) {
            return null;
        }
        return this.history.getLast();
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
