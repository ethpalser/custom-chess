package com.ethpalser.chess.space.custom;

public enum Location {
    LAST_MOVED,
    PATH,
    PIECE,
    POINT;

    public String toString() {
        return switch (this) {
            case LAST_MOVED -> "last moved";
            case PATH -> "path";
            case PIECE -> "piece";
            case POINT -> "point";
        };
    }

    public static Location fromString(String string) {
        if ("last moved".equals(string)) {
            return LAST_MOVED;
        } else if ("path".equals(string)) {
            return PATH;
        } else if ("piece".equals(string)) {
            return PIECE;
        } else if ("point".equals(string)) {
            return POINT;
        }
        return PATH;
    }
}
