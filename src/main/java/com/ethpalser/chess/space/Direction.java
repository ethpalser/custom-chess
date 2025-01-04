package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Colour;

public enum Direction {
    AT,
    NORTH,
    SOUTH,
    EAST,
    WEST,
    FRONT,
    BACK,
    RIGHT,
    LEFT;

    public int[] vector() {
        return this.vector(Colour.WHITE);
    }

    public int[] vector(Colour colour) {
        final int dir = Colour.WHITE.equals(colour) ? 1 : -1;
        return switch (this) {
            case NORTH -> new int[]{0, 1};
            case SOUTH -> new int[]{0, -1};
            case EAST -> new int[]{1, 0};
            case WEST -> new int[]{-1, 0};
            case FRONT -> new int[]{0, dir};
            case BACK -> new int[]{0, dir * -1};
            case RIGHT -> new int[]{dir, 0};
            case LEFT -> new int[]{dir * -1, 0};
            default -> new int[]{0, 0};
        };
    }

}
