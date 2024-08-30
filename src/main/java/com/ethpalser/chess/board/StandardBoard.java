package com.ethpalser.chess.board;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
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
import java.util.Locale;
import java.util.Map;

public class StandardBoard implements Board {

    private final Plane<Piece> piecesOnBoard;

    public StandardBoard() {
        Plane<Piece> map = new Plane<>();
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
        if (point == null) {
            return;
        }
        if (piece == null) {
            this.piecesOnBoard.remove(point);
        } else {
            // Removes the piece from its original location
            this.piecesOnBoard.remove(piece.getPoint());
            // Replaces the piece at the new point
            this.piecesOnBoard.put(point, piece);
            // Updates the piece to be at its new location
            piece.move(point);
        }
    }

    @Override
    public LogEntry<Point, Piece> movePiece(Point start, Point end,
            Log<Point, Piece> log, ThreatMap threatMap) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        Piece piece = this.piecesOnBoard.get(start);
        if (piece == null) {
            throw new IllegalActionException("piece cannot move as it does not exist at " + start);
        }

        Movement move = piece.getMoves(this.getPieces(), log, threatMap).getMove(end);
        if (move == null) {
            throw new IllegalActionException("piece (" + piece.getCode() + ") cannot move to " + end);
        }

        Piece captured = this.getPiece(end);
        this.piecesOnBoard.remove(start);
        this.piecesOnBoard.put(end, piece);
        piece.move(end);

        LogEntry<Point, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getStartObject();
            this.piecesOnBoard.remove(followUp.getStart());
            this.piecesOnBoard.put(followUp.getEnd(), toForcePush);
            this.piecesOnBoard.remove(null); // If the piece is meant to be removed it was put here
        }
        return new ChessLogEntry(start, end, piece, captured, move.getFollowUpMove());
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.piecesOnBoard.getMinX() <= x && x <= this.piecesOnBoard.getMaxX()
                && this.piecesOnBoard.getMinY() <= y && y <= this.piecesOnBoard.getMaxY();
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

                    String code = customPiece.getCode();
                    if ("".equals(code)) {
                        code = "P"; // In some cases that pawn's code is an empty string
                    }
                    if (Colour.WHITE.equals(customPiece.getColour())) {
                        code = code.toLowerCase(Locale.ROOT);
                    }
                    sb.append(code).append(" ");
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
