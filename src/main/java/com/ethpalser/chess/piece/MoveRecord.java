package com.ethpalser.chess.piece;

import com.ethpalser.chess.space.Point;

public interface MoveRecord {

    ChessPiece getMovingPiece();

    Point getStart();

    Point getEnd();

}
