package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.StandardPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChessBoard implements Board<Coordinate> {

    private static final String OUT_OF_BOUNDS_MESSAGE = "Coordinate at %s is out of bounds";
    private static final String UNAVAILABLE_MESSAGE = "Coordinate at %s is unavailable";

    private final Space space;
    private final Map<Coordinate, Piece> pieces;

    public ChessBoard() {
        this(new Plane(8, 8), new StandardPieceFactory());
    }

    public ChessBoard(ChessBoard original) {
        this.space = original.space;
        this.pieces = new HashMap<>(original.pieces);
    }

    public ChessBoard(Space space, PieceFactory factory) {
        this.space = space;
        Map<Coordinate, Piece> plane = new HashMap<>();

        int minX = this.space.min(1);
        int minY = this.space.min(2);
        int maxX = this.space.max(1);
        int maxY = this.space.max(2);

        // Add all pieces for each rank
        for (int rank : new int[]{minY, minY + 1, maxY - 1, maxY}) {
            Colour colour = rank < maxY / 2 ? Colour.WHITE : Colour.BLACK;

            for (int file = minX; file <= maxX; file++) {
                Piece piece;
                if (rank == minY || rank == maxY) {
                    piece = switch (file) {
                        case 0, 7 -> factory.create(colour, PieceType.ROOK.getCode());
                        case 1, 6 -> factory.create(colour, PieceType.KNIGHT.getCode());
                        case 2, 5 -> factory.create(colour, PieceType.BISHOP.getCode());
                        case 3 -> factory.create(colour, PieceType.QUEEN.getCode());
                        case 4 -> factory.create(colour, PieceType.KING.getCode());
                        default -> null; // Default boards do not have custom pieces
                    };
                } else {
                    piece = factory.create(colour, PieceType.PAWN.getCode());
                }

                if (piece != null) {
                    Point point = new Point(file, rank);
                    piece.setCoordinate(point);
                    plane.put(point, piece);
                }
            }
        }
        this.pieces = plane;
    }

    public ChessBoard(Space space, PieceFactory factory, List<String> pieceStrings) {
        this.space = space;
        Map<Coordinate, Piece> plane = new HashMap<>();

        for (String s : pieceStrings) {
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(s);
            // Expecting five tokens in the order of: Colour, Code (Type), File, Rank, hasMoved
            Colour colour = Colour.fromCode(tokenizer.nextToken());
            String code = tokenizer.nextToken();
            Point point = new Point(tokenizer.nextToken() + tokenizer.nextToken());
            boolean hasMoved = Boolean.parseBoolean(tokenizer.nextToken());

            Piece piece = factory.create(colour, code);
            piece.setCoordinate(point);
            piece.setHasMoved(hasMoved);
            plane.put(point, piece);
        }
        this.pieces = plane;
    }

    @Override
    public Piece get(Coordinate point) {
        if (this.space.isOutOfBounds(point)) {
            return null;
        }
        return this.pieces.get(point);
    }

    @Override
    public void add(Coordinate point, Piece piece) throws IndexOutOfBoundsException {
        if (this.space.isOutOfBounds(point)) {
            throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_MESSAGE, point));
        }
        if (this.space.isUnavailable(point)) {
            throw new IndexOutOfBoundsException(String.format(UNAVAILABLE_MESSAGE, point));
        }
        if (piece == null) {
            this.pieces.remove(point);
        } else {
            if (this.pieces.get(piece.getCoordinate()) != null && this.pieces.get(piece.getCoordinate()).equals(piece)) {
                // Removes the piece from its original location
                this.pieces.remove(piece.getCoordinate());
            }
            // Replaces the piece at the new point
            this.pieces.put(point, piece);
            // Update the position of the piece, but not that it has moved. This insertion is not treated as a move.
            piece.setCoordinate(point);
        }
        this.pieces.remove(null);
    }

    @Override
    public void remove(Coordinate point) throws IndexOutOfBoundsException {
        if (point != null && this.space.isOutOfBounds(point)) {
            throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_MESSAGE, point));
        }
        this.pieces.remove(point);
    }

    @Override
    public int count() {
        return this.pieces.size();
    }

    @Override
    public Space space() {
        return this.space;
    }

    @Override
    public boolean rejects(Coordinate point) {
        return point == null || this.space.isOutOfBounds(point) || this.space.isUnavailable(point);
    }

    @Override
    public Collection<Coordinate> occupied() {
        List<Coordinate> occupiedList = new ArrayList<>();
        for (Map.Entry<Coordinate, Piece> e : this.pieces.entrySet()) {
            if (e.getValue() != null) {
                occupiedList.add(e.getKey());
            }
        }
        return occupiedList;
    }

    @Override
    public String toString() {
        int width = this.space.length(1);
        int height = this.space.length(2);

        StringBuilder sb = new StringBuilder();
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x <= width - 1; x++) {
                Piece piece = this.get(new Point(x, y));
                String code;
                if (piece == null) {
                    code = " ";
                } else if (Colour.BLACK.equals(piece.getColour())) {
                    code = piece.getCode();
                } else {
                    code = piece.getCode().toLowerCase(Locale.ROOT);
                }
                sb.append("| ").append(code).append(" ");
            }
            sb.append("| ").append(1 + y).append("\n");
        }
        for (int x = 0; x < width; x++) {
            sb.append("  ").append((char) ('a' + x)).append(" ");
        }
        return sb.toString();
    }

    @Override
    public Iterator<Piece> iterator() {
        return this.pieces.values().iterator();
    }
}
