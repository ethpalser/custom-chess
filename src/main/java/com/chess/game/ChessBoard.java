package com.chess.game;

import com.chess.game.piece.ChessPiece;

public interface ChessBoard {

    ChessPiece get(Vector2D point);

    void move(Vector2D start, Vector2D end);

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Vector2D point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
