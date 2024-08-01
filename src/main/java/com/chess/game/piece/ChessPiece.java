package com.chess.game.piece;

import com.chess.game.ChessBoard;
import com.chess.game.ChessLog;
import com.chess.game.Colour;
import com.chess.game.Vector2D;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(ChessBoard board, ChessLog log);

    boolean canMove(ChessBoard board, ChessLog log, Vector2D destination);

    void move(Vector2D destination);

    boolean hasMoved();
}
