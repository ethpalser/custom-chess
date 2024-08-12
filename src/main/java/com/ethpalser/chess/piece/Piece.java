package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.space.Point;

public interface Piece {

    String getCode();

    Colour getColour();

    MoveSet getMoves(Board board);

    default MoveSet getMoves(Board board, Log<Point, Piece> log) {
        // log ignored
        return this.getMoves(board);
    }

    default MoveSet getMoves(Board board, Log<Point, Piece> log, ThreatMap threats) {
        // threats ignored, and likely log as well
        return this.getMoves(board, log);
    }

    Point getPoint();

    default boolean canMove(Board board, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    default boolean canMove(Board board, Log<Point, Piece> log, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    default boolean canMove(Board board, Log<Point, Piece> log, ThreatMap threats, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log, threats).toSet().stream().anyMatch(m -> destination.equals(m.getPoint()));
    }

    void move(Point destination);

    boolean hasMoved();
}
