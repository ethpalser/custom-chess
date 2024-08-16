package com.ethpalser.chess.log;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;

public class ChessLogEntry implements LogEntry<Point, Piece> {

    private final Point start;
    private final Point end;
    private final Piece moved;
    private final Piece captured;
    private final boolean isFirstMove;
    private final LogEntry<Point, Piece> followUp;

    public ChessLogEntry(Point start, Point end, Piece moved) {
        this(start, end, moved, null);
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = this.moved != null && !this.moved.hasMoved();
        this.followUp = null;
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured, LogEntry<Point, Piece> followUpMove) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = !this.moved.hasMoved();
        this.followUp = followUpMove;
    }

    public ChessLogEntry(Board board, String log) {
        String[] split = log.split("-"); // When a log entry has a '-' it was not a capture
        if (split.length <= 1) {
            split = log.split("x"); // Assumes the piece is not using an x in its code, which shouldn't happen
        }

        this.start = new Point(split[0]);
        this.end = new Point(split[1]);
        // This only works by recreating the log by simulating the game forward. This fails recreating in reverse.
        this.moved = board.getPiece(start);
        this.captured = board.getPiece(end);
        this.isFirstMove = !this.moved.hasMoved();
        this.followUp = null; // Todo: check for specific log strings involving enPassant and castle
    }

    @Override
    public Point getStart() {
        return this.start;
    }

    @Override
    public Point getEnd() {
        return this.end;
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

    public LogEntry<Point, Piece> getSubLogEntry() {
        return this.followUp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.moved.getCode()).append(this.start);
        if (this.captured != null) {
            sb.append("x");
        } else {
            sb.append("-");
        }
        sb.append(this.end);
        return sb.toString();
    }
}
