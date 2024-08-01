package com.chess.game;

import com.chess.game.piece.ChessPiece;

public interface ChessBoard {

    ChessPiece getPiece(Vector2D point);

    void movePiece(Vector2D start, Vector2D end);

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Vector2D point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
