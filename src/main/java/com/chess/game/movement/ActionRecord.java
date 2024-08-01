package com.chess.game.movement;

import com.chess.game.ChessBoard;
import com.chess.game.LogRecord;
import com.chess.game.Vector2D;
import com.chess.game.piece.ChessPiece;

public class ActionRecord implements LogRecord {

    private final Action action;
    private final ChessPiece moved;
    private final ChessPiece captured;
    private final boolean isFirstMove;

    public ActionRecord(Vector2D start, Vector2D end, ChessPiece moved) {
        this(new Action(moved.getColour(), start, end), moved);
    }

    public ActionRecord(Action action, ChessPiece moved) {
        this(action, moved, null);
    }

    public ActionRecord(Action action, ChessPiece moved, ChessPiece captured) {
        this.action = action;
        this.moved = moved;
        this.captured = captured;
        // For this to work this ActionRecord must be created after verifying but before executing the Action
        this.isFirstMove = !this.moved.hasMoved();
    }

    public ActionRecord(ChessBoard board, String log) {
        String[] split = log.split("-"); // When a log entry has a '-' it was not a capture
        if (split.length <= 1) {
            split = log.split("x"); // Assumes the piece is not using an x in its code, which shouldn't happen
        }

        Vector2D start = new Vector2D(split[0]);
        Vector2D end = new Vector2D(split[1]);
        // This only works by recreating the log by simulating the game forward. This fails recreating in reverse.
        this.moved = board.getPiece(start);
        this.captured = board.getPiece(end);
        this.action = new Action(moved.getColour(), start, end);
        this.isFirstMove = !this.moved.hasMoved();
    }

    @Override
    public Vector2D getStart() {
        return this.action.getStart();
    }

    @Override
    public Vector2D getEnd() {
        return this.action.getEnd();
    }

    @Override
    public ChessPiece getMovingPiece() {
        return this.moved;
    }

    @Override
    public ChessPiece getCapturedPiece() {
        return this.captured;
    }

    @Override
    public boolean isFirstMove() {
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
