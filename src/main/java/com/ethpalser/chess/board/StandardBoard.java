package com.ethpalser.chess.board;

import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
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

public class StandardBoard implements Board {

    private final Plane<Piece> piecesOnBoard;

    public StandardBoard() {
        Plane<Piece> map = new Plane<>(0, 0, 7, 7);
        int length = map.length();
        map.putAll(this.generatePiecesInRank(length, 0));
        map.putAll(this.generatePiecesInRank(length, 1));
        map.putAll(this.generatePiecesInRank(length, map.length() - 2));
        map.putAll(this.generatePiecesInRank(length, map.length() - 1));
        this.piecesOnBoard = map;
    }

    @Override
    public Plane<Piece> getPieces() {
        return this.piecesOnBoard;
    }

    @Override
    public Piece getPiece(Point point) {
        return this.piecesOnBoard.get(point);
    }

    @Override
    public void addPiece(Point point, Piece piece) {
        this.piecesOnBoard.put(point, piece);
    }

    @Override
    public void movePiece(Point start, Point end) {
        Piece piece = this.piecesOnBoard.get(start);
        this.piecesOnBoard.remove(start);
        this.piecesOnBoard.put(end, piece);
        piece.move(end);

        Move move = piece.getMoves(this, null).getMove(end);
        move.getFollowUpMove().ifPresent(m -> {
            Piece followUp = m.getStartObject();
            this.piecesOnBoard.remove(m.getStart());
            this.piecesOnBoard.put(m.getEnd(), followUp);
            this.piecesOnBoard.remove(null); // If the piece is meant to be removed it was put here
        });
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return x > this.piecesOnBoard.getMaxX() || this.piecesOnBoard.getMinX() > x
                || y > this.piecesOnBoard.getMaxY() || this.piecesOnBoard.getMinY() > y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.piecesOnBoard.length() - 1; y >= 0; y--) {
            for (int x = 0; x <= this.piecesOnBoard.width() - 1; x++) {
                Piece customPiece = getPiece(x, y);
                if (customPiece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");
                    if (PieceType.PAWN.getCode().equals(customPiece.getCode())) {
                        sb.append("P ");
                    } else {
                        sb.append(customPiece.getCode()).append(" ");
                    }
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    // PRIVATE METHODS

    private Map<Point, Piece> generatePiecesInRank(int length, int rank) {
        Map<Point, Piece> map = new HashMap<>();
        Colour colour = rank < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        if (rank == 0 || rank == length - 1) {
            for (int file = 0; file < length; file++) {
                Point point = new Point(file, rank);
                Piece piece = switch (file) {
                    case 0, 7 -> new Rook(colour, point);
                    case 1, 6 -> new Knight(colour, point);
                    case 2, 5 -> new Bishop(colour, point);
                    case 3 -> new Queen(colour, point);
                    case 4 -> new King(colour, point);
                    default -> null;
                };
                map.put(point, piece);
            }
        } else if (rank == 1 || rank == length - 2) {
            for (int file = 0; file < 8; file++) {
                Point point = new Point(file, rank);
                map.put(point, new Pawn(colour, point));
            }
        }
        return map;
    }

}
