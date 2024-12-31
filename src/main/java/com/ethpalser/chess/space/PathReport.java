package com.ethpalser.chess.space;

public record PathReport(Path path, Coordinate lastChecked, PathReport.Status status) {

    public PathReport {
        if (path == null || lastChecked == null || status == null) {
            throw new IllegalArgumentException("One or more constructor arguments are null");
        }
    }

    public enum Status {
        END_OF_PATH,
        END_OF_SPACE,
        OUT_OF_BOUNDS,
        BLOCKED_BY_ALLY,
        BLOCKED_BY_OPPONENT,
        BLOCKED_BY_OBSTACLE,
        FAILED_CONDITIONS;
    }
}
