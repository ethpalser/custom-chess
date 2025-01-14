package com.ethpalser.chess.move;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.space.Coordinate;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-02",
        majorVersion = 1,
        minorVersion = 4,
        lastModified = "2025-01-13"
)
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
        BLOCKED_BY_THREAT;
    }
}
