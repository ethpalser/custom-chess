package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.board.Vector2D;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(ChessBoard board, ChessLog log);

    boolean canMove(ChessBoard board, ChessLog log, Vector2D destination);

    void move(Vector2D destination);

    boolean hasMoved();
}
