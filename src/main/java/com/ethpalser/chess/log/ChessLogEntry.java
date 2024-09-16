package com.ethpalser.chess.log;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.reference.AbsoluteReference;
import com.ethpalser.chess.view.ActionView;

public class ChessLogEntry implements LogEntry<Point, Piece> {

    private final Point start;
    private final Point end;
    private final Piece moved;
    private final Piece captured;
    private final boolean isFirstMove;
    private final LogEntry<Point, Piece> followUp;
    private Piece promoted;

    public ChessLogEntry(Point start, Point end, Piece moved) {
        this(start, end, moved, null);
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = this.moved != null && !this.moved.getHasMoved();
        this.followUp = null;
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured, LogEntry<Point, Piece> followUpMove) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = !this.moved.getHasMoved();
        this.followUp = followUpMove;
    }

    public ChessLogEntry(Plane<Piece> board, String log) {
        String[] split = log.split("-"); // When a log entry has a '-' it was not a capture
        if (split.length <= 1) {
            split = log.split("X"); // Assumes the piece is not using an X in its code, which shouldn't happen
        }

        this.start = new Point(split[0]);
        this.end = new Point(split[1]);
        // todo: determine how to assign pieces when the board does not match, or remove storing moved and captured
        // this.moved = board.get(this.start);
        this.moved = null;
        // this.captured = board.get(this.end);
        this.captured = null;
        // this.isFirstMove = !this.moved.getHasMoved(); // todo: determine whether a piece is first moved or not in log
        this.isFirstMove = false;
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
    public Piece getPromotion() {
        return this.promoted;
    }

    @Override
    public void setPromotion(Piece promoted) {
        this.promoted = promoted;
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
        String followUpString;
        // Determining the kind of followup
        if (this.followUp != null && this.followUp.getStartObject() != null) {
            if ("R".equals(this.followUp.getStartObject().getCode())) {
                // Not queen side rook
                if (this.followUp.getStart().getY() != 0) {
                    return "O-O";
                } else {
                    return "O-O-O";
                }
            } else if ("P".equals(this.followUp.getStartObject().getCode())) {
                followUpString = " e.p.";
            } else {
                followUpString = " c.m.?"; // todo: setup a way to identify what a followup move does
            }
        } else {
            followUpString = "";
        }


        sb.append(this.moved.getCode()).append(this.start);
        if (this.captured != null) {
            sb.append("X").append(this.captured.getCode());
        } else {
            sb.append(" ");
        }
        sb.append(this.end).append(followUpString);
        return sb.toString();
    }

    @Override
    public ActionView toView() {
        return new ActionView(new AbsoluteReference<>(this.start), new AbsoluteReference<>(this.end));
    }
}
