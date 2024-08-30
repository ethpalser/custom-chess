package com.ethpalser.chess.board;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChessBoard implements Board {

    private final Plane<Piece> pieces;

    public ChessBoard() {
        this.pieces = this.standard();
    }

    public ChessBoard(BoardType type) {
        this(type, null);
    }

    public ChessBoard(BoardType type, Log<Point, Piece> log) {
        if (BoardType.STANDARD.equals(type)) {
            this.pieces = this.standard();
        } else {
            this.pieces = this.custom(log);
        }
    }

    public ChessBoard(BoardType type, Log<Point, Piece> log, List<String> pieces) {
        Plane<Piece> plane = new Plane<>();
        if (BoardType.STANDARD.equals(type)) {
            // todo Map a piece string to a piece
        } else {
            CustomPieceFactory pf = new CustomPieceFactory(plane, log);
            for (String s : pieces) {
                CustomPiece customPiece = pf.build(s);
                plane.put(customPiece.getPoint(), customPiece);
            }
        }
        this.pieces = plane;
    }

    @Override
    public Plane<Piece> getPieces() {
        return this.pieces;
    }

    @Override
    public Piece getPiece(Point point) {
        return this.pieces.get(point);
    }

    @Override
    public void addPiece(Point point, Piece piece) {
        if (point == null) {
            return;
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
        Piece piece = this.pieces.get(start);
        if (piece == null) {
            throw new IllegalActionException("piece cannot move as it does not exist at " + start);
        }

        Movement move = piece.getMoves(this.getPieces(), log, threatMap).getMove(end);
        if (move == null) {
            throw new IllegalActionException("piece (" + piece.getCode() + ") cannot move to " + end);
        }

        Piece captured = this.getPiece(end);
        this.pieces.remove(start);
        this.pieces.put(end, piece);
        piece.move(end);

        LogEntry<Point, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getStartObject();
            this.pieces.remove(followUp.getStart());
            if (followUp.getEnd() != null) {
                this.pieces.put(followUp.getEnd(), toForcePush);
            }
        }
        return new ChessLogEntry(start, end, piece, captured, move.getFollowUpMove());
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.pieces.getMinX() <= x && x <= this.pieces.getMaxX()
                && this.pieces.getMinY() <= y && y <= this.pieces.getMaxY();
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

    private Plane<Piece> standard() {
        Plane<Piece> plane = new Plane<>();
        int length = plane.length();
        plane.putAll(this.generateStandardPiecesInRank(length, 0));
        plane.putAll(this.generateStandardPiecesInRank(length, 1));
        plane.putAll(this.generateStandardPiecesInRank(length, plane.length() - 2));
        plane.putAll(this.generateStandardPiecesInRank(length, plane.length() - 1));
        return plane;
    }

    private Plane<Piece> custom(Log<Point, Piece> log) {
        Plane<Piece> plane = new Plane<>();
        int length = plane.length();
        plane.putAll(this.generateCustomPiecesInRank(length, 0, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, 1, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, plane.length() - 2, plane, log));
        plane.putAll(this.generateCustomPiecesInRank(length, plane.length() - 1, plane, log));
        return plane;
    }

    private Map<Point, Piece> generateStandardPiecesInRank(int length, int rank) {
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

    private Map<Point, CustomPiece> generateCustomPiecesInRank(int length, int rank, Plane<Piece> plane,
            Log<Point, Piece> log) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = rank < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = new CustomPieceFactory(plane, log);
        if (rank == 0 || rank == length - 1) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, rank);
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
        } else if (rank == 1 || rank == length - 2) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, rank);
                CustomPiece customPiece = customPieceFactory.build(PieceType.PAWN, colour, vector, false);
                map.put(vector, customPiece);
            }
        }
        return map;
    }

}
