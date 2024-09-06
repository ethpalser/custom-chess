package com.ethpalser.chess.move.map;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoveMap {

    private final Map<Point, Set<Piece>> map;
    private final int length;
    private final int width;

    public MoveMap(Colour colour, Plane<Piece> board, Log<Point, Piece> log, ThreatMap threatMap) {
        this.map = this.setup(colour, board, log, threatMap);
        this.length = board.length();
        this.width = board.width();
    }

    public Set<Point> getPoints() {
        return this.map.keySet();
    }

    public Set<Piece> getPieces(Point point) {
        if (point == null) {
            return Set.of();
        }
        Set<Piece> piecesThreateningPoint = this.map.get(point);
        if (piecesThreateningPoint == null) {
            return Set.of();
        }
        return piecesThreateningPoint;
    }

    public boolean hasNoMove(Point point) {
        return this.getPieces(point).isEmpty();
    }

    public boolean hasNoMove(Point point, boolean ignoreKing) {
        Set<Piece> set = this.getPieces(point);
        for (Piece p : set) {
            if (!ignoreKing || !Pieces.isKing(p)) {
                // Any piece (if the king is not ignored) or non-king piece can move to this point
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                boolean hasThreat = map.get(new Point(x, y)) != null && !map.get(new Point(x, y)).isEmpty();
                if (!hasThreat) {
                    sb.append("|   ");
                } else {
                    sb.append("| + ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    // PRIVATE METHODS

    private Map<Point, Set<Piece>> setup(Colour colour, Plane<Piece> board, Log<Point, Piece> log,
            ThreatMap threatMap) {
        Map<Point, Set<Piece>> moves = new HashMap<>();
        for (Piece piece : board) {
            if (piece != null && Pieces.isAllied(colour, piece)) {
                MoveSet moveSet = piece.getMoves(board, log, threatMap);
                for (Point point : moveSet.getPoints()) {
                    moves.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        return moves;
    }
}
