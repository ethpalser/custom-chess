package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Move;
import com.ethpalser.chess.piece.standard.Bishop;
import com.ethpalser.chess.piece.standard.King;
import com.ethpalser.chess.piece.standard.Knight;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.piece.standard.Queen;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.Map;

public class StandardBoard implements ChessBoard {

    private final Plane<ChessPiece> piecesOnBoard;

    public StandardBoard() {
        Plane<ChessPiece> map = new Plane<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(this.length() - 2));
        map.putAll(this.generatePiecesInRank(this.length() - 1));
        this.piecesOnBoard = map;
    }

    @Override
    public Plane<ChessPiece> getPieces() {
        return this.piecesOnBoard;
    }

    @Override
    public ChessPiece getPiece(Point point) {
        return this.piecesOnBoard.get(point);
    }

    @Override
    public void addPiece(Point point, ChessPiece piece) {
        this.piecesOnBoard.put(point, piece);
    }

    @Override
    public void movePiece(Point start, Point end) {
        ChessPiece piece = this.piecesOnBoard.get(start);
        this.piecesOnBoard.remove(start);
        this.piecesOnBoard.put(end, piece);
        piece.move(end);

        Move move = piece.getMoves(this, null).getMove(end);
        move.getFollowUpMove().ifPresent(m -> {
            ChessPiece followUp = m.getMovingPiece();
            this.piecesOnBoard.remove(m.getStart());
            this.piecesOnBoard.put(m.getEnd(), followUp);
        });
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return x > this.piecesOnBoard.getMaxX() || this.piecesOnBoard.getMinX() > x
                || y > this.piecesOnBoard.getMaxY() || this.piecesOnBoard.getMinY() > y;
    }

    // PRIVATE METHODS

    private int length() {
        return this.piecesOnBoard.getMaxY() - this.piecesOnBoard.getMinY();
    }

    private Map<Point, ChessPiece> generatePiecesInRank(int rank) {
        Map<Point, ChessPiece> map = new HashMap<>();
        Colour colour = rank < (this.length() - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        if (rank == 0 || rank == this.length() - 1) {
            for (int file = 0; file < 8; file++) {
                Point point = new Point(file, rank);
                ChessPiece piece = switch (file) {
                    case 0, 7 -> new Rook(colour, point);
                    case 1, 6 -> new Knight(colour, point);
                    case 2, 5 -> new Bishop(colour, point);
                    case 3 -> new Queen(colour, point);
                    case 4 -> new King(colour, point);
                    default -> null;
                };
                map.put(point, piece);
            }
        } else if (rank == 1 || rank == this.length() - 2) {
            for (int file = 0; file < 8; file++) {
                Point point = new Point(file, rank);
                map.put(point, new Pawn(colour, point));
            }
        }
        return map;
    }

}
