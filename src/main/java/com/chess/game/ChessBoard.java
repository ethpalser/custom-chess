package com.chess.game;

import com.chess.game.piece.ChessPiece;
import java.util.Set;

public interface ChessBoard {

    Space2D<ChessPiece> getPieces();

    ChessPiece getPiece(Vector2D point);

    void movePiece(Vector2D start, Vector2D end);

    Set<ChessPiece> getThreats(Vector2D point, Colour colour);

    default boolean hasThreats(Vector2D point, Colour colour) {
        if (point == null) {
            return false;
        }
        return !getThreats(point, colour).isEmpty();
    }

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Vector2D point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
