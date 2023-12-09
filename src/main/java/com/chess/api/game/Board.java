package com.chess.api.game;

import com.chess.api.game.movement.Path;
import com.chess.api.game.piece.Piece;
import com.chess.api.game.piece.PieceFactory;
import com.chess.api.game.piece.PieceType;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class Board {

    private final int length;
    private final int width;
    private final Map<Vector2D, Piece> pieceMap;
    private Piece lastMoved;

    public Board() {
        this.length = 8;
        this.width = 8;
        Map<Vector2D, Piece> map = new HashMap<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(this.length - 2));
        map.putAll(this.generatePiecesInRank(this.length - 1));
        this.pieceMap = map;
        this.lastMoved = null;
    }

    private Map<Vector2D, Piece> generatePiecesInRank(int y) {
        Map<Vector2D, Piece> map = new HashMap<>();
        Colour colour = y < (this.length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        PieceFactory pieceFactory = PieceFactory.getInstance();
        if (y == 0 || y == this.length - 1) {
            for (int x = 0; x < 8; x++) {
                Vector2D vector = new Vector2D(x, y);
                Piece piece = switch (x) {
                    case 0, 7 -> pieceFactory.build(PieceType.ROOK, colour, vector);
                    case 1, 6 -> pieceFactory.build(PieceType.KNIGHT, colour, vector);
                    case 2, 5 -> pieceFactory.build(PieceType.BISHOP, colour, vector);
                    case 3 -> pieceFactory.build(PieceType.QUEEN, colour, vector);
                    case 4 -> pieceFactory.build(PieceType.KING, colour, vector);
                    default -> null;
                };
                map.put(vector, piece);
            }
        } else if (y == 1 || y == this.length - 2) {
            for (int x = 0; x < 8; x++) {
                Vector2D vector = new Vector2D(x, y);
                Piece piece = pieceFactory.build(PieceType.PAWN, colour, vector);
                map.put(vector, piece);
            }
        }
        return map;
    }

    public int count() {
        Collection<Piece> pieces = this.pieceMap.values();
        int count = 0;
        for (Piece piece : pieces) {
            if (piece != null)
                count++;
        }
        return count;
    }

    public int length() {
        return this.length;
    }

    public int width() {
        return this.width;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > this.width - 1 || y < 0 || y > this.length - 1) {
            return null;
        }
        return pieceMap.get(new Vector2D(x, y));
    }

    public Piece getPiece(Vector2D vector) {
        if (vector == null) {
            return null;
        }
        return pieceMap.get(vector);
    }

    public void setPiece(@NonNull Vector2D vector, Piece piece) {
        if (piece == null) {
            this.pieceMap.remove(vector);
        } else {
            this.pieceMap.remove(piece.getPosition());
            this.pieceMap.put(vector, piece);
            piece.setPosition(vector);
            this.lastMoved = piece;
        }
    }

    public List<Piece> getPieces() {
        return List.of();
    }

    public List<Piece> getPieces(Path path) {
        if (path == null) {
            return this.getPieces();
        }
        List<Piece> pieceList = new LinkedList<>();
        for (Vector2D vector : path) {
            pieceList.add(this.getPiece(vector));
        }
        return pieceList;
    }

    public Piece getLastMoved() {
        return lastMoved;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                Piece piece = getPiece(x, y);
                if (piece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");
                    if (PieceType.PAWN.equals(piece.getType())) {
                        sb.append("P ");
                    } else {
                        sb.append(piece.getType().getCode()).append(" ");
                    }
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

}
