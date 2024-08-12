package com.ethpalser.chess.game;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.MoveRecord;

public interface LogRecord extends MoveRecord {

    Point getStart();

    Point getEnd();

    Piece getMovingPiece();

    Piece getCapturedPiece();

    boolean isFirstMove();

}
