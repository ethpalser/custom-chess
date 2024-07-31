package com.chess.game.movement;

import com.chess.game.Space2D;
import com.chess.game.Vector2D;
import com.chess.game.piece.Piece;

public class ActionRecord {

    private final Action action;
    private final Piece moved;
    private final Piece captured;
    private final boolean isFirstMove;

    public ActionRecord(Action action, Piece moved) {
        this(action, moved, null);
    }

    public ActionRecord(Action action, Piece moved, Piece captured) {
        this.action = action;
        this.moved = moved;
        this.captured = captured;
        // For this to work this ActionRecord must be created after verifying but before executing the Action
        this.isFirstMove = !this.moved.getHasMoved();
    }

    public ActionRecord(Space2D<Piece> board, String log) {
        String[] split = log.split("-"); // When a log entry has a '-' it was not a capture
        if (split.length <= 1) {
            split = log.split("x"); // Assumes the piece is not using an x in its code, which shouldn't happen
        }

        Vector2D start = new Vector2D(split[0]);
        Vector2D end = new Vector2D(split[1]);
        // This only works by recreating the log by simulating the game forward. This fails recreating in reverse.
        this.moved = board.get(start);
        this.captured = board.get(end);
        this.action = new Action(moved.getColour(), start, end);
        this.isFirstMove = !this.moved.getHasMoved();
    }

    public Action getAction() {
        return this.action;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public int getDistanceMoved() {
        // Note: Moving diagonal is considered moving 2 for each 1 space.
        return Math.abs(action.getStart().getX() - action.getEnd().getX())
                + Math.abs(action.getStart().getY() - action.getEnd().getY());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.moved.getType().getCode()).append(this.action.getStart());
        if (this.captured != null) {
            sb.append("x");
        } else {
            sb.append("-");
        }
        sb.append(this.action.getEnd());
        return sb.toString();
    }
}
