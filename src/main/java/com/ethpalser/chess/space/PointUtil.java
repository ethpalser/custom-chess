package com.ethpalser.chess.space;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;
import java.util.HashSet;
import java.util.Set;

public class PointUtil {

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

    public static Set<Point> generateHorizontalMoves(Board board, Point start, Colour colour,
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

    public static Set<Point> generateVerticalMoves(Board board, Point start, Colour colour,
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

    public static Set<Point> generateDiagonalMoves(Board board, Point start, Colour colour,
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
