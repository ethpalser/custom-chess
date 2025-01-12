package com.ethpalser.chess.game;

import com.ethpalser.chess.space.Space;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MockGame implements Game {

    private final MockNode root;
    private int turn;
    private MockNode current;
    private final Deque<MockNode> logStack;
    private final Deque<MockNode> undoStack;

    public MockGame(MockNode root) {
        this.root = root;
        this.turn = 1;
        this.current = root;
        this.logStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
    }

    @Override
    public GameStatus updateGame(Action action) {
        for (MockNode node : current.getNext()) {
            int val = action.getEnd().getValue(Space.AXIS.X);
            if (node.getValue() == val) {
                this.logStack.push(this.current);
                this.undoStack.clear();
                this.current = node;
                return GameStatus.ONGOING;
            }
        }
        this.turn++;
        return GameStatus.ONGOING;
    }

    @Override
    public GameStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTurn() {
        return this.turn;
    }

    @Override
    public GameStatus undo() {
        if (this.logStack.isEmpty()) {
            return GameStatus.ONGOING;
        }
        MockNode prev = this.logStack.pop();
        if (prev != null) {
            this.undoStack.push(this.current);
            this.current = prev;
            this.turn--;
        } else {
            this.current = this.root;
            this.turn = 1;
        }
        return GameStatus.ONGOING;
    }

    @Override
    public GameStatus redo() {
        if (this.undoStack.isEmpty()) {
            return GameStatus.ONGOING;
        }
        MockNode next = this.undoStack.pop();
        if (next != null) {
            this.logStack.push(next);
            this.current = next;
            this.turn++;
        } else {
            this.current = this.root;
            this.turn = 1;
        }
        return GameStatus.ONGOING;
    }

    @Override
    public Iterable<Action> potentialUpdates() {
        if (this.current == null) {
            return List.of();
        }
        return this.current.getChildren();
    }

    @Override
    public int evaluateState() {
        return this.current.getValue();
    }

    @Override
    public GameSaveData createSaveData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GameInfo info() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GameContext context() {
        throw new UnsupportedOperationException();
    }
}
