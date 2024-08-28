package com.ethpalser.chess.board;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public interface Board {

    Plane<Piece> getPieces();

    Piece getPiece(Point point);

    default Piece getPiece(int x, int y) {
        return getPiece(new Point(x, y));
    }

    void addPiece(Point point, Piece piece);

    LogEntry<Point, Piece> movePiece(Point start, Point end,
            Log<Point, Piece> log, ThreatMap threatMap);

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Point point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
