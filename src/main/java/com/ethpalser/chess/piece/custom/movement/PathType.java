package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.board.Point;

public enum PathType {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
    CUSTOM;

    public static PathType findType(Point start, Point end) {
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
