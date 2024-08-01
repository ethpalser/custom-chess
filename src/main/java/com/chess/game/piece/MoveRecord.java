package com.chess.game.piece;

import com.chess.game.Vector2D;

public interface MoveRecord {

    ChessPiece getMovingPiece();

    Vector2D getStart();

    Vector2D getEnd();

}
