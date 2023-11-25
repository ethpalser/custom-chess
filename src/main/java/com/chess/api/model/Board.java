package com.chess.api.model;

import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.Path;
import com.chess.api.model.piece.Piece;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Board {

    private final Piece[][] pieces;

    public Board() {
        Piece[][] pieceList = new Piece[8][8];

        int y = 0;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = (switch (x) {
                case 0, 7 -> Piece.rook(Colour.WHITE, new Coordinate(x, y));
                case 1, 6 -> Piece.knight(Colour.WHITE, new Coordinate(x, y));
                case 2, 5 -> Piece.bishop(Colour.WHITE, new Coordinate(x, y));
                case 3 -> Piece.queen(Colour.WHITE, new Coordinate(x, y));
                case 4 -> Piece.king(Colour.WHITE, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
        }

        y = 1;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = Piece.pawn(Colour.WHITE, new Coordinate(x, y));
        }

        y = 6;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = Piece.pawn(Colour.BLACK, new Coordinate(x, y));
        }

        y = 7;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = (switch (x) {
                case 0, 7 -> Piece.rook(Colour.BLACK, new Coordinate(x, y));
                case 1, 6 -> Piece.knight(Colour.BLACK, new Coordinate(x, y));
                case 2, 5 -> Piece.bishop(Colour.BLACK, new Coordinate(x, y));
                case 3 -> Piece.queen(Colour.BLACK, new Coordinate(x, y));
                case 4 -> Piece.king(Colour.BLACK, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
        }
        this.pieces = pieceList;
    }

    public int count() {
        int count = 0;
        for (int x = 0; x < pieces.length; x++) {
            for (int y = 0; y < pieces[x].length; y++) {
                if (pieces[x][y] != null)
                    count++;
            }
        }
        return count;
    }

    public Piece getPiece(int x, int y) {
        return pieces[x][y];
    }

    public Piece getPiece(Coordinate coordinate) {
        return pieces[coordinate.getX()][coordinate.getY()];
    }

    public List<Piece> getPieces(Coordinate source, Coordinate target) {
        Path path = new Path(source, target);
        List<Piece> list = new ArrayList<>();
        for (Coordinate coordinate : path) {
            if (this.getPiece(coordinate) != null) {
                list.add(this.getPiece(coordinate));
            }
        }
        return list;
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
        Piece source = pieces[start.getX()][start.getY()];
        Piece target = pieces[end.getX()][end.getY()];

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
        while (movementIterator.hasNext() && !hasValidPath) {
            Movement movement = movementIterator.next();
            // Todo: validate movement condition is passed
            hasValidPath = this.isValidPath(source.getColour(), movement.getPath(source.getColour(), start, end));
        }
        if (!hasValidPath) {
            return;
        }
        // Update the piece's internal position
        source.performMove(end);
        // Update the piece on the board
        pieces[end.getX()][end.getY()] = pieces[start.getX()][start.getY()];
        pieces[start.getX()][start.getY()] = null;
    }

    private boolean isValidPath(@NonNull Colour colour, Path path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        boolean hasPiece = false;
        for (Coordinate coordinate : path) {
            Piece piece = this.getPiece(coordinate);
            if (piece != null) {
                if (hasPiece || colour.equals(piece.getColour())) {
                    return false;
                }
                hasPiece = true;
            }
        }
        return true;
    }

}
