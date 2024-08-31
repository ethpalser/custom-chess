package com.ethpalser.chess.game;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MockGame implements Game {

    private final MockNode root;
    private MockNode current;
    private final Deque<MockNode> logStack;
    private final Deque<MockNode> undoStack;

    public MockGame(MockNode root) {
        this.root = root;
        this.current = root;
        this.logStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
    }

    @Override
    public GameStatus updateGame(Action action) {
        for (MockNode node : current.getNext()) {
            int val = action.getEnd().getX();
            if (node.getValue() == val) {
                this.logStack.push(this.current);
                this.current = node;
                return GameStatus.ONGOING;
            }
        }
        return GameStatus.ONGOING;
    }

    @Override
    public Log<Point, Piece> getLog() {
        return null;
    }

    @Override
    public GameStatus getStatus() {
        return GameStatus.ONGOING;
    }

    @Override
    public int getTurn() {
        return 0;
    }

    @Override
    public GameStatus undoUpdate(int changesToUndo, boolean saveForRedo) {
        if (this.logStack.isEmpty()) {
            return GameStatus.ONGOING;
        }
        MockNode prev = this.logStack.pop();
        if (prev != null) {
            if (saveForRedo) {
                this.undoStack.push(this.current);
            }
            this.current = prev;
        } else {
            this.current = this.root;
        }
        return GameStatus.ONGOING;
    }

    @Override
    public GameStatus redoUpdate(int changesToRedo) {
        if (this.undoStack.isEmpty()) {
            return GameStatus.ONGOING;
        }
        MockNode next = this.undoStack.pop();
        if (next != null) {
            this.logStack.push(next);
            this.current = next;
        } else {
            this.current = this.root;
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
}
