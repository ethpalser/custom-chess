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

    void addPiece(Point point, Piece piece);

    LogEntry<Point, Piece> movePiece(Point start, Point end,
            Log<Point, Piece> log, ThreatMap threatMap);

    boolean isInBounds(Point point);

}
