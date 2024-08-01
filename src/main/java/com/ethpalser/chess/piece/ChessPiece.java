package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.game.ChessLog;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(ChessBoard board, ChessLog log);

    boolean canMove(ChessBoard board, ChessLog log, Point destination);

    void move(Point destination);

    boolean hasMoved();
}
