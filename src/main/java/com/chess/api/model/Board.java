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

    private final Map<Coordinate, Piece> pieceMap;
    private Piece lastMoved;

    public Board() {
        Map<Coordinate, Piece> map = new HashMap<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(Coordinate.MAX_Y - 1));
        map.putAll(this.generatePiecesInRank(Coordinate.MAX_Y));
        this.pieceMap = map;
        this.lastMoved = null;
    }

    private Map<Coordinate, Piece> generatePiecesInRank(int y) {
        Map<Coordinate, Piece> map = new HashMap<>();
        Colour colour = y < Coordinate.MAX_Y / 2 ? Colour.WHITE : Colour.BLACK;

        PieceFactory pieceFactory = PieceFactory.getInstance();
        if (y == 0 || y == Coordinate.MAX_Y) {
            for (int x = 0; x < 8; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Piece piece = switch (x) {
                    case 0, 7 -> pieceFactory.build(PieceType.ROOK, colour, coordinate);
                    case 1, 6 -> pieceFactory.build(PieceType.KNIGHT, colour, coordinate);
                    case 2, 5 -> pieceFactory.build(PieceType.BISHOP, colour, coordinate);
                    case 3 -> pieceFactory.build(PieceType.QUEEN, colour, coordinate);
                    case 4 -> pieceFactory.build(PieceType.KING, colour, coordinate);
                    default -> null;
                };
                map.put(coordinate, piece);
            }
        } else if (y == 1 || y == Coordinate.MAX_Y - 1) {
            for (int x = 0; x < 8; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Piece piece = pieceFactory.build(PieceType.PAWN, colour, coordinate);
                map.put(coordinate, piece);
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
        return Coordinate.MAX_Y + 1;
    }

    public int width() {
        return Coordinate.MAX_X + 1;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > Coordinate.MAX_X || y < 0 || y > Coordinate.MAX_Y) {
            return null;
        }
        return pieceMap.get(Coordinate.at(x, y));
    }

    public Piece getPiece(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        return pieceMap.get(coordinate);
    }

    public void setPiece(@NonNull Coordinate coordinate, Piece piece) {
        if (piece == null) {
            this.pieceMap.remove(coordinate);
        } else {
            this.pieceMap.put(coordinate, piece);
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
        for (Coordinate coordinate : path) {
            pieceList.add(this.getPiece(coordinate));
        }
        return pieceList;
    }

    public Piece getLastMoved() {
        return lastMoved;
    }

    public List<Piece> getReferencePieces(Reference reference, Coordinate pathStart, Coordinate pathEnd) {
        if (reference == null) {
            return List.of();
        }
        List<Piece> pieces = new ArrayList<>();
        switch (reference.location()) {
            case LAST_MOVED -> pieces.add(this.getLastMoved());
            case AT_START -> pieces.add(this.getPiece(pathStart));
            case AT_DESTINATION -> pieces.add(this.getPiece(pathEnd));
            case AT_COORDINATE -> pieces.add(this.getPiece(reference.coordinate()));
            case PATH_TO_DESTINATION -> pieces = this.getPieces(new Path(pathStart, pathEnd));
            case PATH_TO_COORDINATE -> pieces = this.getPieces(new Path(pathStart, reference.coordinate()));
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
    public void movePiece(@NonNull Coordinate start, @NonNull Coordinate end) {
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
            movement.getExtraMovement().move(this, start);
        }
    }

    private boolean isValidPath(Path path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        Iterator<Coordinate> iterator = path.iterator();
        while (iterator.hasNext()) {
            Coordinate coordinate = iterator.next();
            if (this.getPiece(coordinate) != null && iterator.hasNext()) {
                // Piece is in the middle of the path
                return false;
            }
        }
        return true;
    }

}
