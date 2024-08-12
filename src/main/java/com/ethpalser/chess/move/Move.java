package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import java.util.Objects;
import java.util.Optional;

public class Move {

    private final Point point;
    private final LogEntry<Point, Piece> followUpMove;

    public Move(Point point) {
        this.point = point;
        this.followUpMove = null;
    }

    public Move(Point point, LogEntry<Point, Piece> followUpMove) {
        this.point = point;
        this.followUpMove = followUpMove;
    }

    public Point getPoint() {
        return this.point;
    }

    public Optional<LogEntry<Point, Piece>> getFollowUpMove() {
        return Optional.ofNullable(this.followUpMove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(point, move.point);
    }

    @Override
    public int hashCode() {
        // FollowUp is irrelevant when used in a HashSet, as there cannot be overlap with points
        return Objects.hash(point);
    }
}
