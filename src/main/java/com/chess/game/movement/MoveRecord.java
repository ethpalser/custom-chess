package com.chess.game.movement;

import com.chess.game.Vector2D;
import com.chess.game.piece.ChessPiece;

public interface MoveRecord {

    ChessPiece getMovingPiece();

    Vector2D getStart();

    Vector2D getEnd();

}
