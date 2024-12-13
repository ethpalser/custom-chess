package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.StandardPieceFactory;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.Locale;

public class ChessBoard implements Board {

    private final Plane<Piece> pieces;

    public ChessBoard() {
        this(new StandardPieceFactory());
    }

    public ChessBoard(PieceFactory factory) {
        Plane<Piece> plane = new Plane<>(); // 8 x 8 plane, origin at (0, 0)

        // Add all pieces for each rank
        for (int rank : new int[]{plane.getMinY(), plane.getMinY() + 1, plane.getMaxY() - 1, plane.getMaxY()}) {
            Colour colour = rank < plane.getMaxY() / 2 ? Colour.WHITE : Colour.BLACK;

            for (int file = plane.getMinX(); file <= plane.getMaxX(); file++) {
                Piece piece;
                if (rank == plane.getMinY() || rank == plane.getMaxY()) {
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
                    piece.setPoint(point);
                    plane.put(point, piece);
                }
            }
        }
        this.pieces = plane;
    }

    public ChessBoard(PieceFactory factory, List<String> pieceStrings) {
        Plane<Piece> plane = new Plane<>();
        for (String s : pieceStrings) {
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(s);
            // Expecting five tokens in the order of: Colour, Code (Type), File, Rank, hasMoved
            Colour colour = Colour.fromCode(tokenizer.nextToken());
            String code = tokenizer.nextToken();
            Point point = new Point(tokenizer.nextToken() + tokenizer.nextToken());
            boolean hasMoved = Boolean.parseBoolean(tokenizer.nextToken());

            Piece piece = factory.create(colour, code);
            piece.setPoint(point);
            piece.setHasMoved(hasMoved);
            plane.put(point, piece);
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
            if (this.pieces.get(piece.getPoint()) != null && this.pieces.get(piece.getPoint()).equals(piece)) {
                // Removes the piece from its original location
                this.pieces.remove(piece.getPoint());
            }
            // Replaces the piece at the new point
            this.pieces.put(point, piece);
            // Update the position of the piece, but not that it has moved. This insertion is not treated as a move.
            piece.setPoint(point);
        }
        this.pieces.remove(null);
    }

    @Override
    public boolean isInBounds(Point point) {
        return point != null && this.pieces.getMinX() <= point.getX() && point.getX() <= this.pieces.getMaxX()
                && this.pieces.getMinY() <= point.getY() && point.getY() <= this.pieces.getMaxY();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.pieces.length() - 1; y >= 0; y--) {
            for (int x = 0; x <= this.pieces.width() - 1; x++) {
                Piece piece = this.pieces.get(this.pieces.at(x, y));
                if (piece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");

                    String code = piece.getCode();
                    if ("".equals(code)) {
                        code = "P"; // In some cases that pawn's code is an empty string
                    }
                    if (Colour.WHITE.equals(piece.getColour())) {
                        code = code.toLowerCase(Locale.ROOT);
                    }
                    sb.append(code).append(" ");
                }
            }
            sb.append("| ").append(1 + y).append("\n");
        }
        for (int x = 0; x < this.pieces.width(); x++) {
            sb.append("  ").append((char) ('a' + x)).append(" ");
        }
        return sb.toString();
    }

}
