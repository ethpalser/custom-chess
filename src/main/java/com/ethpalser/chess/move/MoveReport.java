package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Coordinate;
import java.util.List;

public record MoveReport(Move move, Status status, Coordinate lastChecked, boolean isAttack, List<Coordinate> threatsSinceKing) {

    public MoveReport {
        if (move == null || status == null) {
            throw new IllegalArgumentException("One or more constructor arguments are null");
        }
    }

    public enum Status {
        END_OF_PATH,
        OUT_OF_BOUNDS,
        BLOCKED_BY_ALLY,
        BLOCKED_BY_OPPONENT,
        BLOCKED_BY_OBSTACLE,
        FAILED_CONDITIONS;
    }
}
