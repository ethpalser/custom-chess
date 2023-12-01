package com.chess.api.model;

import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.Path;
import com.chess.api.model.movement.condition.Condition;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.PieceFactory;
import com.chess.api.model.piece.PieceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

    public List<Piece> getReferencePieces(Reference reference, Vector2D pathStart, Vector2D pathEnd) {
        if (reference == null) {
            return List.of();
        }
        List<Piece> pieces = new ArrayList<>();
        switch (reference.location()) {
            case LAST_MOVED -> pieces.add(this.getLastMoved());
            case AT_START -> pieces.add(this.getPiece(pathStart));
            case AT_DESTINATION -> pieces.add(this.getPiece(pathEnd));
            case AT_COORDINATE -> pieces.add(this.getPiece(reference.vector()));
            case PATH_TO_DESTINATION -> pieces = this.getPieces(new Path(pathStart, pathEnd));
            case PATH_TO_COORDINATE -> pieces = this.getPieces(new Path(pathStart, reference.vector()));
            case BELOW_DESTINATION -> pieces.add(this.getPiece(pathEnd.getX(), pathEnd.getY() - 1));
        }
        return pieces;
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
        Piece source = this.getPiece(start);
        Piece target = this.getPiece(end);

        if (source == null || source.equals(target) || (target != null && source.getColour().equals(target.getColour()))) {
            // Cannot move a piece if there is no piece, both are the same piece, or both are the same colour
            return;
        }
        if (!source.verifyMove(end)) {
            // Not a valid location for the piece to move to
            return;
        }

        boolean hasValidPath = false;
        Iterator<Movement> movementIterator = source.getMovementList().listIterator();
        Movement movement = null;
        while (movementIterator.hasNext() && !hasValidPath) {
            movement = movementIterator.next();
            boolean hasValidConditions = true;
            for (Condition condition : movement.getConditions()) {
                if (!condition.evaluate(this, start, end)) {
                    hasValidConditions = false;
                    break;
                }
            }
            hasValidPath =
                    hasValidConditions && movement.isValidCoordinate(source.getColour(), start, end) && this.isValidPath(movement.getPath(source.getColour(), start, end));
        }
        if (movement == null || !hasValidPath) {
            // Piece is in the middle of the path
            return;
        }
        // Update the piece's internal position
        source.performMove(end);
        // Update the piece on the board
        this.pieceMap.put(end, source);
        this.pieceMap.remove(start);
        this.lastMoved = source;
        if (movement.getExtraMovement() != null) {
            movement.getExtraMovement().move(this, end); // Must be 'end' to work for en passant. Todo: Fix inflexibility
        }
    }

    private boolean isValidPath(Path path) {
        if (path == null) {
            return false;
        }

        Iterator<Vector2D> iterator = path.iterator();
        while (iterator.hasNext()) {
            Vector2D vector = iterator.next();
            if (this.getPiece(vector) != null && iterator.hasNext()) {
                // Piece is in the middle of the path
                return false;
            }
        }
        return true;
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
