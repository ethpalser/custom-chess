package com.chess.game;

import com.chess.game.piece.ChessPiece;

public interface LogRecord {

    Vector2D getStart();

    Vector2D getEnd();

    ChessPiece getMoved();

    ChessPiece getCaptured();

    boolean isFirstMove();

}
