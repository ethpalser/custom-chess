package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Vector2D;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.MoveRecord;

public interface LogRecord extends MoveRecord {

    Vector2D getStart();

    Vector2D getEnd();

    ChessPiece getMovingPiece();

    ChessPiece getCapturedPiece();

    boolean isFirstMove();

}
