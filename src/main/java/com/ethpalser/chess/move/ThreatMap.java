package com.ethpalser.chess.move;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThreatMap implements MoveMap {

    private final Colour colour;
    private final Map<Point, Set<ChessPiece>> map;

    public ThreatMap(Colour colour) {
        this.colour = colour;
        this.map = new HashMap<>();
    }

    public ThreatMap(Colour colour, ChessBoard board) {
        this.colour = colour;
        this.map = this.setup(colour, board, null);
    }

    public ThreatMap(Colour colour, ChessBoard board, ChessLog log) {
        this.colour = colour;
        this.map = this.setup(colour, board, log);
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Set<ChessPiece> getPieces(Point point) {
        if (point == null) {
            return null;
        }
        return this.map.get(point);
    }

    @Override
    public void clearMoves(ChessPiece piece) {
        for (Point p : this.map.keySet()) {
            this.clearMoves(piece, p);
        }
    }

    @Override
    public void clearMoves(ChessPiece piece, Point point) {
        this.map.get(point).remove(piece);
    }

    @Override
    public void updateMoves(ChessBoard board, ChessLog log, Point point) {
        ChessPiece piece = board.getPiece(point);
        if (piece != null) {
            Set<ChessPiece> threateningPieces = this.getPieces(point);

            // Expensive operation. This can be improved by knowing the paths to replace.
            this.map.get(point).clear();
            for (ChessPiece c : threateningPieces) {
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

    private Map<Point, Set<ChessPiece>> setup(Colour colour, ChessBoard board, ChessLog log) {
        Map<Point, Set<ChessPiece>> piecesThreateningPoint = new HashMap<>();
        for (ChessPiece piece : board.getPieces()) {
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
