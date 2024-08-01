package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.Vector2D;

public interface MoveRecord {

    ChessPiece getMovingPiece();

    Vector2D getStart();

    Vector2D getEnd();

}
