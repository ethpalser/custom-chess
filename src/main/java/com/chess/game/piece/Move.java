package com.chess.game.piece;

import com.chess.game.Vector2D;
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
        return Optional.of(this.followUpMove);
    }

}
