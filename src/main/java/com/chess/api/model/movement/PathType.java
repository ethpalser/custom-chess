package com.chess.api.model.movement;

import com.chess.api.model.Coordinate;

public enum PathType {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
    CUSTOM;

    public static PathType findType(Coordinate start, Coordinate end) {
        int diffX = Math.abs(end.getX() - start.getX());
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
