package com.ethpalser.chess.piece;

import com.ethpalser.chess.space.Point;

public interface MoveRecord {

    Piece getMovingPiece();

    Point getStart();

    Point getEnd();

}
