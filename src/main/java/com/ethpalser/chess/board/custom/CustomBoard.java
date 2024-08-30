package com.ethpalser.chess.board.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomBoard implements Board {

    private final Plane<Piece> pieces;

    public CustomBoard(Log<Point, Piece> log) {
        Plane<Piece> plane = new Plane<>();
        int length = plane.length();
        plane.putAll(this.generatePiecesInRank(plane, length, 0, log));
        plane.putAll(this.generatePiecesInRank(plane, length, 1, log));
        plane.putAll(this.generatePiecesInRank(plane, length, plane.length() - 2, log));
        plane.putAll(this.generatePiecesInRank(plane, length, plane.length() - 1, log));
        this.pieces = plane;
    }

    public CustomBoard(Log<Point, Piece> log, List<String> pieces) {
        Plane<Piece> plane = new Plane<>();
        CustomPieceFactory pf = new CustomPieceFactory(plane, log);
        for (String s : pieces) {
            CustomPiece customPiece = pf.build(s);
            plane.put(customPiece.getPoint(), customPiece);
        }
        this.pieces = plane;
    }

    @Override
    public Plane<Piece> getPieces() {
        return pieces;
    }

    @Override
    public Piece getPiece(Point vector) {
        if (vector == null) {
            return null;
        }
        return pieces.get(vector);
    }

    @Override
    public void addPiece(Point point, Piece piece) {
        if (point == null) {
            throw new NullPointerException();
        }
        if (piece == null) {
            this.pieces.remove(point);
        } else {
            // Removes the piece from its original location
            this.pieces.remove(piece.getPoint());
            // Replaces the piece at the new point
            this.pieces.put(point, piece);
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
        CustomPiece piece = (CustomPiece) this.getPiece(start);
        if (piece == null) {
            throw new IllegalActionException("piece cannot move as it does not exist at " + start);
        }
        MoveSet moveset = piece.getMoves(this.getPieces(), log, threatMap);
        Movement move = moveset.getMove(end);
        if (move == null) {
            throw new IllegalActionException("piece (" + piece.getCode() + ") cannot move to " + end);
        }

        Piece captured = this.getPiece(end);
        this.pieces.remove(start);
        this.pieces.put(end, piece);
        piece.move(end);

        move.getFollowUpMove().ifPresent(m -> {
            Piece followUp = m.getStartObject();
            this.pieces.remove(m.getStart());
            this.pieces.put(m.getEnd(), followUp);
            this.pieces.remove(null); // If the piece is meant to be removed it was put here
        });
        return new ChessLogEntry(start, end, piece, captured, move.getFollowUpMove().orElse(null));
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.pieces.isInBounds(x, y);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.pieces.length() - 1; y >= 0; y--) {
            for (int x = 0; x <= this.pieces.width() - 1; x++) {
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

    private Map<Point, CustomPiece> generatePiecesInRank(Plane<Piece> plane, int length, int y, Log<Point, Piece> log) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = y < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = new CustomPieceFactory(plane, log);
        if (y == 0 || y == length - 1) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = switch (x) {
                    case 0, 7 -> customPieceFactory.build(PieceType.ROOK, colour, vector, false);
                    case 1, 6 -> customPieceFactory.build(PieceType.KNIGHT, colour, vector, false);
                    case 2, 5 -> customPieceFactory.build(PieceType.BISHOP, colour, vector, false);
                    case 3 -> customPieceFactory.build(PieceType.QUEEN, colour, vector, false);
                    case 4 -> customPieceFactory.build(PieceType.KING, colour, vector, false);
                    default -> null;
                };
                map.put(vector, customPiece);
            }
        } else if (y == 1 || y == length - 2) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = customPieceFactory.build(PieceType.PAWN, colour, vector, false);
                map.put(vector, customPiece);
            }
        }
        return map;
    }
}
