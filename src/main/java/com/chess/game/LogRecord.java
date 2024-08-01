package com.chess.game;

import com.chess.game.piece.MoveRecord;
import com.chess.game.piece.ChessPiece;

public interface LogRecord extends MoveRecord {

    Vector2D getStart();

    Vector2D getEnd();

    ChessPiece getMovingPiece();

    ChessPiece getCapturedPiece();

    boolean isFirstMove();

}
