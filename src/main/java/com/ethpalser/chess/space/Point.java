package com.ethpalser.chess.space;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 3,
        minorVersion = 3,
        lastModified = "2025-01-13"
)
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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!this.getClass().isInstance(o)) {
            return false;
        }

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
}
