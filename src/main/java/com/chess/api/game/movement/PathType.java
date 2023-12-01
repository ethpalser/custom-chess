package com.chess.api.game.movement;

import com.chess.api.game.Vector2D;

public enum PathType {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
    CUSTOM;

    public static PathType findType(Vector2D start, Vector2D end) {
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
