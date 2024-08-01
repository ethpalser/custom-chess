package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.Point;

public interface MoveRecord {

    ChessPiece getMovingPiece();

    Point getStart();

    Point getEnd();

}
