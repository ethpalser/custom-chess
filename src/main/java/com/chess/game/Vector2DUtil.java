package com.chess.game;

import com.chess.game.piece.Piece;
import java.util.HashSet;
import java.util.Set;

public class Vector2DUtil {

    public static Set<Vector2D> generateHorizontalMoves(Space2D<Piece> board, Vector2D start, Colour colour,
            boolean right) {
        Set<Vector2D> set = new HashSet<>();
        int x = right ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY())) {
            Vector2D pos = new Vector2D(start.getX() + x, start.getY());
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
                    set.add(pos);
                }
                break;
            }
            set.add(pos);
            x = right ? x + 1 : x - 1;
        }
        return set;
    }

    public static Set<Vector2D> generateVerticalMoves(Space2D<Piece> board, Vector2D start, Colour colour,
            boolean up) {
        Set<Vector2D> set = new HashSet<>();
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX(), start.getY() + y)) {
            Vector2D pos = new Vector2D(start.getX(), start.getY() + y);
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
                    set.add(pos);
                }
                break;
            }
            set.add(pos);
            y = up ? y + 1 : y - 1;
        }
        return set;
    }

    public static Set<Vector2D> generateDiagonalMoves(Space2D<Piece> board, Vector2D start, Colour colour,
            boolean right, boolean up) {
        Set<Vector2D> set = new HashSet<>();
        int x = right ? 1 : -1;
        int y = up ? 1 : -1;
        // while within the board's boundaries
        while (board.isInBounds(start.getX() + x, start.getY() + y)) {
            Vector2D pos = new Vector2D(start.getX() + x, start.getY() + y);
            if (board.get(pos) != null) {
                if (board.get(pos).getColour() != colour) {
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
