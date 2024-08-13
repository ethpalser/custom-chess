package com.ethpalser.chess.space;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.reference.Direction;
import java.util.HashSet;
import java.util.Set;

public class Point implements Comparable<Point> {

    private final int x;
    private final int y;

    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public Point() {
        this.x = 0;
        this.y = 0;
        // Default bounds
        this.minX = 0;
        this.minY = 0;
        this.maxX = 31;
        this.maxY = 31;
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:31] and y [0:31].
     *
     * @param x An integer between 0 and 31 along the x-axis.
     * @param y An integer between 0 and 31 along the y-axis.
     */
    public Point(int x, int y) {
        this(x, y, 0, 0, 31, 31);
    }

    /**
     * Instantiate a Vector2D within the bounds of x [0:10] and y [0:10], but using characters in Chess' notation
     * where x begins at 'a' and y begins at '1'. Bounds are limited to 10 as there are only ten numerical characters.
     *
     * @param x An integer between 0 and 10 along the x-axis.
     * @param y An integer between 0 and 10 along the y-axis.
     */
    public Point(char x, char y) {
        this(x - 'a', y - '1', 0, 0, 10, 10);
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

    /**
     * Instantiate a Vector2D with specified bounds. All Vector2D must have bounds for hashing. It is
     * recommended to use a Factory method of a class with these bounds to ensure consistency and to abstract the
     * min and max. Vector2Ds are most compatible with those with the same min and max, and to safely compare these
     * vectors it is recommended to convert them into the same size.
     *
     * @param xVal     Integer co-ordinate on x-axis
     * @param yVal     Integer co-ordinate on y-axis
     * @param xBounds1 Represents one bound of the x-axis (ex. min. x value)
     * @param yBounds1 Represents one bound of the y-axis (ex. min. y value)
     * @param xBounds2 Represents one bound of the x-axis
     * @param yBounds2 Represents one bound of the y-axis
     */
    public Point(int xVal, int yVal, int xBounds1, int yBounds1, int xBounds2, int yBounds2) {
        this.x = xVal;
        this.y = yVal;
        // width * height >= Integer.MAX_VALUE, using division to avoid overflow
        if (Integer.MAX_VALUE / Math.abs(xBounds1 + xBounds2 + 1) <= Math.abs(yBounds1 + yBounds2 + 1)) {
            throw new ArithmeticException("Bounds exceed allowed size of " + Integer.MAX_VALUE);
        }
        // Default bounds
        this.minX = Math.min(xBounds1, xBounds2);
        this.minY = Math.min(yBounds1, yBounds2);
        this.maxX = Math.max(xBounds1, xBounds2);
        this.maxY = Math.max(yBounds1, yBounds2);
    }

    public Point(Point copy) {
        this(copy.x, copy.y, copy.minX, copy.minY, copy.maxX, copy.maxY);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public int compareTo(Point o) {
        if (o == null) {
            return -1;
        }
        // The hashCodes are unique for every x,y combination
        return this.hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        int quad = 0;
        if (x < 0)
            quad += 1;
        if (y < 0)
            quad += 2;
        // Each x, y value maps to a distinct positive integer in a bounded space
        return boundsWidth() * boundsHeight() * quad + Math.abs(this.y) * boundsWidth() + Math.abs(this.x);
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

    private int boundsWidth() {
        // This can only be 1 or greater
        return this.maxX - this.minX + 1;
    }

    private int boundsHeight() {
        // This can only be 1 or greater
        return this.maxY - this.minY + 1;
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
            case FRONT -> new Point(this.x, this.y + dir, this.minX, this.minY, this.maxX, this.maxY);
            case BACK -> new Point(this.x, this.y - dir, this.minX, this.minY, this.maxX, this.maxY);
            case RIGHT -> new Point(this.x + dir, this.y, this.minX, this.minY, this.maxX, this.maxY);
            case LEFT -> new Point(this.x - dir, this.y, this.minX, this.minY, this.maxX, this.maxY);
        };
    }

    // Temporary
    public boolean isValidLocation(Point point) {
        return this.minX <= point.getX() && point.getX() <= this.maxX
                && this.minY <= point.getY() && point.getY() <= this.minY;
    }

    // STATIC METHODS

    public static Point generateValidPointOrNull(Board board, Point start, Colour colour,
            int xOffset, int yOffset) {
        Point point = new Point(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or (exists and matching colour)
        if (!board.isInBounds(point.getX(), point.getY()) || (board.getPiece(point) != null
                && board.getPiece(point).getColour() == colour)) {
            return null;
        }
        return point;
    }

    public static Point generateCapturePointOrNull(Board board, Point start, Colour colour,
            int xOffset, int yOffset) {
        Point point = new Point(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or empty or matching colour
        if (!board.isInBounds(point.getX(), point.getY()) || board.getPiece(point) == null
                || board.getPiece(point).getColour() == colour) {
            return null;
        }
        // in bounds and opposite colour (i.e. can capture)
        return point;
    }

    public static Set<Point> generateHorizontalMoves(Board board, Point start, Colour colour) {
        Set<Point> set = new HashSet<>();
        set.addAll(generateHorizontalMoves(board, start, colour, false));
        set.addAll(generateHorizontalMoves(board, start, colour, true));
        return set;
    }

    private static Set<Point> generateHorizontalMoves(Board board, Point start, Colour colour,
            boolean right) {
        Set<Point> set = new HashSet<>();
        int x = right ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY())) {
            Point pos = new Point(start.getX() + x, start.getY());
            if (board.getPiece(pos) != null) {
                if (board.getPiece(pos).getColour() != colour) {
                    set.add(pos);
                }
                break;
            }
            set.add(pos);
            x = right ? x + 1 : x - 1;
        }
        return set;
    }

    public static Set<Point> generateVerticalMoves(Board board, Point start, Colour colour) {
        Set<Point> set = new HashSet<>();
        set.addAll(generateVerticalMoves(board, start, colour, false));
        set.addAll(generateVerticalMoves(board, start, colour, true));
        return set;
    }

    private static Set<Point> generateVerticalMoves(Board board, Point start, Colour colour,
            boolean up) {
        Set<Point> set = new HashSet<>();
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX(), start.getY() + y)) {
            Point pos = new Point(start.getX(), start.getY() + y);
            if (board.getPiece(pos) != null) {
                if (board.getPiece(pos).getColour() != colour) {
                    set.add(pos);
                }
                break;
            }
            set.add(pos);
            y = up ? y + 1 : y - 1;
        }
        return set;
    }

    public static Set<Point> generateDiagonalMoves(Board board, Point start, Colour colour) {
        Set<Point> set = new HashSet<>();
        set.addAll(generateDiagonalMoves(board, start, colour, false, false));
        set.addAll(generateDiagonalMoves(board, start, colour, false, true));
        set.addAll(generateDiagonalMoves(board, start, colour, true, false));
        set.addAll(generateDiagonalMoves(board, start, colour, true, true));
        return set;
    }

    private static Set<Point> generateDiagonalMoves(Board board, Point start, Colour colour,
            boolean right, boolean up) {
        Set<Point> set = new HashSet<>();
        int x = right ? 1 : -1;
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY() + y)) {
            Point pos = new Point(start.getX() + x, start.getY() + y);
            if (board.getPiece(pos) != null) {
                if (board.getPiece(pos).getColour() != colour) {
                    set.add(pos);
                }
                break;
            }
            set.add(pos);
            x = right ? x + 1 : x - 1;
            y = up ? y + 1 : y - 1;
        }
        return set;
    }

}
