package com.ethpalser.chess.game;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.MoveRecord;

public interface LogRecord extends MoveRecord {

    Point getStart();

    Point getEnd();

    ChessPiece getMovingPiece();

    ChessPiece getCapturedPiece();

    boolean isFirstMove();

}
