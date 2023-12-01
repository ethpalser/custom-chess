package com.chess.api.model;

import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.Path;
import com.chess.api.model.movement.condition.Direction;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.PieceFactory;
import com.chess.api.model.piece.PieceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class Board {

    private final Map<Vector2D, Piece> pieceMap;
    private Piece lastMoved;

    public Board() {
        Map<Vector2D, Piece> map = new HashMap<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(Vector2D.MAX_Y - 1));
        map.putAll(this.generatePiecesInRank(Vector2D.MAX_Y));
        this.pieceMap = map;
        this.lastMoved = null;
    }

    private Map<Vector2D, Piece> generatePiecesInRank(int y) {
        Map<Vector2D, Piece> map = new HashMap<>();
        Colour colour = y < Vector2D.MAX_Y / 2 ? Colour.WHITE : Colour.BLACK;

        PieceFactory pieceFactory = PieceFactory.getInstance();
        if (y == 0 || y == Vector2D.MAX_Y) {
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
        } else if (y == 1 || y == Vector2D.MAX_Y - 1) {
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
        return Vector2D.MAX_Y + 1;
    }

    public int width() {
        return Vector2D.MAX_X + 1;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > Vector2D.MAX_X || y < 0 || y > Vector2D.MAX_Y) {
            return null;
        }
        return pieceMap.get(Vector2D.at(x, y));
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
            this.pieceMap.put(vector, piece);
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

    /**
     * Move a piece to a new coordinate within the board.
     * <p>For a piece to move the following must be valid:</p>
     * <ol>
     * <li>The piece to move exists</li>
     * <li>The end location is not occupied by a piece with the same colour</li>
     * <li>The piece can end on that location by moving or capturing</li>
     * <li>The piece can move to that location after restrictions apply by moving or capturing</li>
     * </ol>
     *
     * @param start
     * @param end
     */
    public void movePiece(@NonNull Vector2D start, @NonNull Vector2D end) {
        Piece atStart = this.getPiece(start);
        Piece atEnd = this.getPiece(end);
        if (atStart == null || atStart.equals(atEnd) || (atEnd != null && atStart.getColour().equals(atEnd.getColour()))) {
            return;
        }
        Movement movement = atStart.getMovement(this, end);
        if (movement == null) {
            return;
        }
        atStart.setPosition(end);
        // Update the piece on the board
        this.pieceMap.put(end, atStart);
        this.pieceMap.remove(start);
        if (movement.getExtraMovement() != null) {

        }
        this.lastMoved = atStart;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = Vector2D.MAX_Y; y >= 0; y--) {
            for (int x = 0; x <= Vector2D.MAX_X; x++) {
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
