package com.ethpalser.chess.board;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomBoard implements Board<CustomPiece> {

    private final Plane<CustomPiece> pieces;

    public CustomBoard() {
        Plane<CustomPiece> map = new Plane<>(0, 0, 8, 8);
        int length = map.length();
        map.putAll(this.generatePiecesInRank(length, 0));
        map.putAll(this.generatePiecesInRank(length, 1));
        map.putAll(this.generatePiecesInRank(length, map.length() - 2));
        map.putAll(this.generatePiecesInRank(length, map.length() - 1));
        this.pieces = map;
    }

    public CustomBoard(List<String> pieces) {
        Plane<CustomPiece> map = new Plane<>();
        CustomPieceFactory pf = new CustomPieceFactory(this, null);
        for (String s : pieces) {
            CustomPiece customPiece = pf.build(s);
            map.put(customPiece.getPoint(), customPiece);
        }
        this.pieces = map;
    }

    @Override
    public Plane<CustomPiece> getPieces() {
        return pieces;
    }

    public List<CustomPiece> getPieces(Path path) {
        if (path == null) {
            List<CustomPiece> list = new LinkedList<>();
            for (CustomPiece piece : this.pieces) {
                list.add(piece);
            }
            return list;
        } else {
            return path.toSet().stream().map(this::getPiece).collect(Collectors.toList());
        }
    }

    @Override
    public CustomPiece getPiece(Point vector) {
        if (vector == null) {
            return null;
        }
        return pieces.get(vector);
    }

    @Override
    public void addPiece(Point point, CustomPiece piece) {
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
    public void movePiece(Point start, Point end) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        CustomPiece piece = this.pieces.get(start);
        if (piece == null) {
            throw new IllegalActionException("the moving piece does not exists at " + start);
        }
        Move move = piece.getMoves(this, null, null).getMove(end);
        if (move == null) {
            throw new IllegalActionException("this piece cannot move to " + end);
        }

        this.pieces.remove(start);
        this.pieces.put(end, piece);
        piece.move(end);

        move.getFollowUpMove().ifPresent(m -> {
            Piece followUp = m.getStartObject();
            this.pieces.remove(m.getStart());
            this.pieces.put(m.getEnd(), (CustomPiece) followUp);
            this.pieces.remove(null); // If the piece is meant to be removed it was put here
        });
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.pieces.isInBounds(x, y);
    }

    // PRIVATE METHODS

    private Map<Point, CustomPiece> generatePiecesInRank(int length, int y) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = y < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = new CustomPieceFactory(this, null);
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
