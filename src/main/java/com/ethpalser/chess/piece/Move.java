package com.ethpalser.chess.piece;

import com.ethpalser.chess.space.Point;
import java.util.Objects;
import java.util.Optional;

public class Move {

    private final Point point;
    private final MoveRecord followUpMove;

    public Move(Point point) {
        this.point = point;
        this.followUpMove = null;
    }

    public Move(Point point, MoveRecord followUpMove) {
        this.point = point;
        this.followUpMove = followUpMove;
    }

    public Point getPoint() {
        return this.point;
    }

    public Optional<MoveRecord> getFollowUpMove() {
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
