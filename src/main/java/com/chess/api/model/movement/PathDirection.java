package com.chess.api.model.movement;

import com.chess.api.model.Coordinate;

public enum PathDirection {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
    CUSTOM;

    public static PathDirection findDirection(Coordinate start, Coordinate end) {
        int diffX = Math.abs(end.getX() - start.getY());
        int diffY = Math.abs(end.getY() - start.getY());

        if (diffX == 0) {
            return VERTICAL;
        } else if (diffY == 0) {
            return HORIZONTAL;
        } else if (diffX == diffY) {
            return DIAGONAL;
        } else {
            return CUSTOM;
        }
    }
}
