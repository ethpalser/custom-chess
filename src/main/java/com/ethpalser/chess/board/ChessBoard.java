package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import java.util.Set;

public interface ChessBoard {

    Plane<ChessPiece> getPieces();

    ChessPiece getPiece(Point point);

    void movePiece(Point start, Point end);

    Set<ChessPiece> getThreats(Point point, Colour colour);

    default boolean hasThreats(Point point, Colour colour) {
        if (point == null) {
            return false;
        }
        return !getThreats(point, colour).isEmpty();
    }

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Point point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
