package com.chess.game;

import com.chess.game.reference.Direction;

public class Vector2D implements Comparable<Vector2D> {

    private final int x;
    private final int y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(char x, char y) {
        this(x - 'a', y - '1');
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Checks if the given x and y coordinates are within the bounds of the board. This is fixed at a min of 0 and
     * max of 7 for both x and y coordinates.
     *
     * @param x int
     * @param y int
     * @return true if 0 <= x <= 7 and 0 <= y <= 7, otherwise false
     */
    public static boolean isValid(int x, int y) {
        return 0 <= x && x <= 7 && 0 <= y && y <= 7;
    }

    /**
     * Checks if the current vector is within the bounds of the board. This is fixed at a min of 0 and max of 7
     * for both x and y coordinates.
     *
     * @return true if 0 <= x <= 7 and 0 <= y <= 7, otherwise false
     */
    public boolean isValid() {
        return Vector2D.isValid(this.x, this.y);
    }

    /**
     * Creates a new Vector from the current Vector shifted one space in the given direction.
     *
     * @param colour    {@link Colour} of the piece which the player is facing.
     * @param direction {@link Direction} relative to the piece. Left is always White's left side.
     * @return {@link Vector2D}
     */
    public Vector2D shift(Colour colour, Direction direction) {
        if (colour == null || direction == null) {
            throw new NullPointerException();
        }
        // The direction the piece will shift towards. Black's directions are the opposite of White's
        int dir = Colour.WHITE.equals(colour) ? 1 : -1;
        return switch (direction) {
            case AT -> this;
            case FRONT -> new Vector2D(this.x, this.y + dir);
            case BACK -> new Vector2D(this.x, this.y - dir);
            case RIGHT -> new Vector2D(this.x + dir, this.y);
            case LEFT -> new Vector2D(this.x - dir, this.y);
        };
    }

    @Override
    public int compareTo(Vector2D o) {
        if (o == null) {
            return -1;
        }
        // The hashCodes are unique for every x,y combination
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        // Using a prime number to ensure uniqueness, and will enforce that the board cannot be greater than 16x16
        return this.y * 17 + this.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        Vector2D vector = (Vector2D) o;
        // When x and y are equal, their hashCodes are equal
        return this.hashCode() == vector.hashCode();
    }

    @Override
    public String toString() {
        char xChar = (char) ('a' + this.x);
        return "" + xChar + (this.y + 1);
    }
}
