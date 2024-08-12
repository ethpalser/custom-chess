package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.game.ChessLog;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(ChessBoard board);

    default MoveSet getMoves(ChessBoard board, ChessLog log) {
        // log ignored
        return this.getMoves(board);
    }

    default MoveSet getMoves(ChessBoard board, ChessLog log, ThreatMap threats) {
        // threats ignored, and likely log as well
        return this.getMoves(board, log);
    }

    Point getPoint();

    default boolean canMove(ChessBoard board, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    default boolean canMove(ChessBoard board, ChessLog log, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    default boolean canMove(ChessBoard board, ChessLog log, ThreatMap threats, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log, threats).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    void move(Point destination);

    boolean hasMoved();
}
