package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Piece;

public class ActionRecord implements LogEntry<Point, Piece> {

    private final Action action;
    private final Piece moved;
    private final Piece captured;
    private final boolean isFirstMove;

    public ActionRecord(Point start, Point end, Piece moved) {
        this(new Action(moved.getColour(), start, end), moved);
    }

    public ActionRecord(Action action, Piece moved) {
        this(action, moved, null);
    }

    public ActionRecord(Action action, Piece moved, Piece captured) {
        this.action = action;
        this.moved = moved;
        this.captured = captured;
        // For this to work this ActionRecord must be created after verifying but before executing the Action
        this.isFirstMove = !this.moved.hasMoved();
    }

    public ActionRecord(Board board, String log) {
        String[] split = log.split("-"); // When a log entry has a '-' it was not a capture
        if (split.length <= 1) {
            split = log.split("x"); // Assumes the piece is not using an x in its code, which shouldn't happen
        }

        Point start = new Point(split[0]);
        Point end = new Point(split[1]);
        // This only works by recreating the log by simulating the game forward. This fails recreating in reverse.
        this.moved = board.getPiece(start);
        this.captured = board.getPiece(end);
        this.action = new Action(moved.getColour(), start, end);
        this.isFirstMove = !this.moved.hasMoved();
    }

    @Override
    public Point getStart() {
        return this.action.getStart();
    }

    @Override
    public Point getEnd() {
        return this.action.getEnd();
    }

    @Override
    public Piece getStartObject() {
        return this.moved;
    }

    @Override
    public Piece getEndObject() {
        return this.captured;
    }

    @Override
    public boolean isFirstOccurrence() {
        return this.isFirstMove;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.moved.getCode()).append(this.action.getStart());
        if (this.captured != null) {
            sb.append("x");
        } else {
            sb.append("-");
        }
        sb.append(this.action.getEnd());
        return sb.toString();
    }
}
