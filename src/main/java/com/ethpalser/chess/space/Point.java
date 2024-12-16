package com.ethpalser.chess.space;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;

public class Point implements Coordinate, Comparable<Point> {

    private final int x;
    private final int y;

    public static final Point ORIGIN = new Point();

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:31] and y [0:31].
     *
     * @param x An integer between 0 and 31 along the x-axis.
     * @param y An integer between 0 and 31 along the y-axis.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:10] and y [0:10], but using characters in Chess' notation
     * where x begins at 'a' and y begins at '1'. Bounds are limited to 10 as there are only ten numerical characters.
     *
     * @param x An integer between 0 and 10 along the x-axis.
     * @param y An integer between 0 and 10 along the y-axis.
     */
    public Point(char x, char y) {
        this(x - 'a', y - '1');
    }

    /**
     * Instantiate a Vector2D using a String, parsing it for characters to utilize instantiation with char values.
     * This throws a NullPointerException if the string is not at least two characters long.
     *
     * @param s String of a move in chess notation (ex. e6)
     */
    public Point(String s) throws NullPointerException, IndexOutOfBoundsException {
        this(s.charAt(s.length() - 2), s.charAt(s.length() - 1));
    }

    public Point(Point copy) {
        this(copy.x, copy.y);
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public int getValue(int dimension) {
        return switch (dimension) {
            case 1 -> this.x;
            case 2 -> this.y;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public int[] getValues() {
        return new int[]{this.x, this.y};
    }

    @Override
    public Coordinate translate(int magnitude, int... values) {
        int[] newValues = new int[this.getDimension()];
        for (int i = 0; i < newValues.length; i++) {
            newValues[i] += this.getValue(i + 1);
            if (i < values.length) {
                newValues[i] += magnitude * values[i];
            }
        }
        return new Point(newValues[0], newValues[1]);
    }

    @Override
    public int compareTo(Point o) {
        if (o == null)
            return -1;
        int dimDiff = o.getDimension() - this.getDimension();
        if (dimDiff != 0)
            return dimDiff;

        for (int d = 1; d <= o.getDimension(); d++) {
            int valDiff = o.getValue(d) - this.getValue(d);
            if (valDiff != 0)
                return valDiff;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return this.getY() * 31 + this.getX();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        Point vector = (Point) o;
        // Compares only x and y values, ignoring bounds
        // These are not equal when inserting into hashmap
        return this.x == vector.x && this.y == vector.y;
    }

    @Override
    public String toString() {
        char xChar = (char) ('a' + this.x);
        return "" + xChar + (this.y + 1);
    }

    /**
     * Creates a new Vector from the current Vector shifted one space in the given direction.
     *
     * @param colour    {@link Colour} of the piece which the player is facing.
     * @param direction {@link Direction} relative to the piece. Left is always White's left side.
     * @return {@link Point}
     */
    public Point shift(Colour colour, Direction direction) {
        if (colour == null || direction == null) {
            throw new NullPointerException();
        }
        // The direction the piece will shift towards. Black's directions are the opposite of White's
        int dir = Colour.WHITE.equals(colour) ? 1 : -1;
        return switch (direction) {
            case AT -> this;
            case FRONT -> new Point(this.x, this.y + dir);
            case BACK -> new Point(this.x, this.y - dir);
            case RIGHT -> new Point(this.x + dir, this.y);
            case LEFT -> new Point(this.x - dir, this.y);
        };
    }

    // STATIC METHODS

    public static Point validOrNull(Board<Coordinate> board, Point start, Colour colour,
            int xOffset, int yOffset, boolean includeDefends) {
        Point point = (Point) start.translate(1, xOffset, yOffset);
        // In general, valid spaces are empty or an opponent's piece
        boolean isEmpty = board.get(point) == null;
        boolean isOpponent = !isEmpty && !board.get(point).getColour().equals(colour);
        // IncludeDefends is a special valid case only needed for algorithms considering opponent actions
        if (!board.rejects(point) && (includeDefends || isEmpty || isOpponent)) {
            return point;
        }
        return null;
    }

    public static Point notCaptureOrNull(Board<Coordinate> board, Point start, int xOffset, int yOffset) {
        Point point = (Point) start.translate(1, xOffset, yOffset);
        // Non-capture points are always empty
        if (!board.rejects(point) && board.get(point) == null) {
            return point;
        }
        return null;
    }

    public static Point captureOrNull(Board<Coordinate> board, Point start, Colour colour,
            int xOffset, int yOffset, boolean includeDefends) {
        Point point = (Point) start.translate(1, xOffset, yOffset);
        // Capture points are all valid spaces that are not empty
        boolean isEmpty = board.get(point) == null;
        boolean isOpponent = !isEmpty && !board.get(point).getColour().equals(colour);
        if (!board.rejects(point) && (includeDefends || isOpponent)) {
            return point;
        }
        return null;
    }

}
