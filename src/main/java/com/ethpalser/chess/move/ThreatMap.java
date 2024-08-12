package com.ethpalser.chess.move;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThreatMap implements MoveMap {

    private final Colour colour;
    private final Map<Point, Set<Piece>> map;

    public ThreatMap(Colour colour, Board board, Log log) {
        this.colour = colour;
        this.map = this.setup(colour, board, log);
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Set<Piece> getPieces(Point point) {
        if (point == null) {
            return null;
        }
        return this.map.get(point);
    }

    @Override
    public void clearMoves(Piece piece) {
        for (Point p : this.map.keySet()) {
            this.clearMoves(piece, p);
        }
    }

    @Override
    public void clearMoves(Piece piece, Point point) {
        this.map.get(point).remove(piece);
    }

    @Override
    public void updateMoves(Board board, Log log, Point point) {
        Piece piece = board.getPiece(point);
        if (piece != null) {
            Set<Piece> threateningPieces = this.getPieces(point);

            // Expensive operation. This can be improved by knowing the paths to replace.
            this.map.get(point).clear();
            for (Piece c : threateningPieces) {
                this.clearMoves(c);
                for (Point p : piece.getMoves(board, log).getPoints()) {
                    this.map.computeIfAbsent(p, k -> new HashSet<>()).add(c);
                }
            }
        }
    }

    @Override
    public Integer getValue() {
        return null;
    }

    // PRIVATE METHODS

    private Map<Point, Set<Piece>> setup(Colour colour, Board board, Log log) {
        Map<Point, Set<Piece>> piecesThreateningPoint = new HashMap<>();
        for (Piece piece : board.getPieces()) {
            if (colour.equals(piece.getColour())) {
                MoveSet moveSet = piece.getMoves(board, log);
                for (Point point : moveSet.getPoints()) {
                    piecesThreateningPoint.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        return piecesThreateningPoint;
    }

}
