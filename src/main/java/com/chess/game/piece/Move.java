package com.chess.game.piece;

import com.chess.game.Vector2D;
import java.util.Objects;
import java.util.Optional;

public class Move {

    private final Vector2D point;
    private final MoveRecord followUpMove;

    public Move(Vector2D point) {
        this.point = point;
        this.followUpMove = null;
    }

    public Move(Vector2D point, MoveRecord followUpMove) {
        this.point = point;
        this.followUpMove = followUpMove;
    }

    public Vector2D getPoint() {
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
        return Objects.equals(point, move.point) && Objects.equals(followUpMove, move.followUpMove);
    }

    @Override
    public int hashCode() {
        // FollowUp is irrelevant when used in a HashSet, as there cannot be overlap with points
        return Objects.hash(point);
    }
}
