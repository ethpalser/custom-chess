package com.ethpalser.chess.board;

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

public class CustomBoard implements Board {

    private final Plane<Piece> pieces;

    public CustomBoard() {
        Plane<Piece> map = new Plane<>(0, 0, 8, 8);
        int length = map.length();
        map.putAll(this.generatePiecesInRank(length, 0));
        map.putAll(this.generatePiecesInRank(length, 1));
        map.putAll(this.generatePiecesInRank(length, map.length() - 2));
        map.putAll(this.generatePiecesInRank(length, map.length() - 1));
        this.pieces = map;
    }

    public CustomBoard(List<String> pieces) {
        Plane<Piece> map = new Plane<>();
        CustomPieceFactory pf = CustomPieceFactory.getInstance();
        for (String s : pieces) {
            CustomPiece customPiece = pf.build(s);
            map.put(customPiece.getPoint(), customPiece);
        }
        this.pieces = map;
    }

    @Override
    public Plane<Piece> getPieces() {
        return pieces;
    }

    public List<Piece> getPieces(Path path) {
        if (path == null) {
            List<Piece> list = new LinkedList<>();
            for (Piece piece : this.pieces) {
                list.add(piece);
            }
            return list;
        } else {
            return path.toSet().stream().map(this::getPiece).collect(Collectors.toList());
        }
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
            this.pieces.remove(piece.getPoint());
            this.pieces.put(point, piece);
            piece.move(point);
        }
    }

    @Override
    public void movePiece(Point start, Point end) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        this.addPiece(end, this.getPiece(start));
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return this.pieces.isInBounds(x, y);
    }

    // PRIVATE METHODS

    private Map<Point, CustomPiece> generatePiecesInRank(int length, int y) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = y < (length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = CustomPieceFactory.getInstance();
        if (y == 0 || y == length - 1) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = switch (x) {
                    case 0, 7 -> customPieceFactory.build(PieceType.ROOK, colour, vector);
                    case 1, 6 -> customPieceFactory.build(PieceType.KNIGHT, colour, vector);
                    case 2, 5 -> customPieceFactory.build(PieceType.BISHOP, colour, vector);
                    case 3 -> customPieceFactory.build(PieceType.QUEEN, colour, vector);
                    case 4 -> customPieceFactory.build(PieceType.KING, colour, vector);
                    default -> null;
                };
                map.put(vector, customPiece);
            }
        } else if (y == 1 || y == length - 2) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = customPieceFactory.build(PieceType.PAWN, colour, vector);
                map.put(vector, customPiece);
            }
        }
        return map;
    }
}
