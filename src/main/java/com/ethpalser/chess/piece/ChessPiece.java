package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.game.ChessLog;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(ChessBoard board, ChessLog log);

    default boolean canMove(ChessBoard board, ChessLog log, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    void move(Point destination);

    boolean hasMoved();
}
