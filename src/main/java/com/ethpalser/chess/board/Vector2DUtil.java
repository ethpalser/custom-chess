package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Colour;
import java.util.HashSet;
import java.util.Set;

public class Vector2DUtil {

    public static Vector2D generateValidPointOrNull(ChessBoard board, Vector2D start, Colour colour,
            int xOffset, int yOffset) {
        Vector2D point = new Vector2D(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or (exists and matching colour)
        if (!board.isInBounds(point.getX(), point.getY()) || (board.getPiece(point) != null
                && board.getPiece(point).getColour() == colour)) {
            return null;
        }
        return point;
    }

    public static Vector2D generateCapturePointOrNull(ChessBoard board, Vector2D start, Colour colour,
            int xOffset, int yOffset) {
        Vector2D point = new Vector2D(start.getX() + xOffset, start.getY() + yOffset);
        // not in bounds or empty or matching colour
        if (!board.isInBounds(point.getX(), point.getY()) || board.getPiece(point) == null
                || board.getPiece(point).getColour() == colour) {
            return null;
        }
        // in bounds and opposite colour (i.e. can capture)
        return point;
    }

    public static Set<Vector2D> generateHorizontalMoves(ChessBoard board, Vector2D start, Colour colour,
            boolean right) {
        Set<Vector2D> set = new HashSet<>();
        int x = right ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY())) {
            Vector2D pos = new Vector2D(start.getX() + x, start.getY());
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

    public static Set<Vector2D> generateVerticalMoves(ChessBoard board, Vector2D start, Colour colour,
            boolean up) {
        Set<Vector2D> set = new HashSet<>();
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX(), start.getY() + y)) {
            Vector2D pos = new Vector2D(start.getX(), start.getY() + y);
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

    public static Set<Vector2D> generateDiagonalMoves(ChessBoard board, Vector2D start, Colour colour,
            boolean right, boolean up) {
        Set<Vector2D> set = new HashSet<>();
        int x = right ? 1 : -1;
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY() + y)) {
            Vector2D pos = new Vector2D(start.getX() + x, start.getY() + y);
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
