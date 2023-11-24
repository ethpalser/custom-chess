package com.chess.api.model;

import com.chess.api.model.piece.Piece;
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
                case 0, 7 -> Piece.ROOK(Colour.WHITE, new Coordinate(x, y));
                case 1, 6 -> Piece.KNIGHT(Colour.WHITE, new Coordinate(x, y));
                case 2, 5 -> Piece.BISHOP(Colour.WHITE, new Coordinate(x, y));
                case 3 -> Piece.QUEEN(Colour.WHITE, new Coordinate(x, y));
                case 4 -> Piece.KING(Colour.WHITE, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
        }

        y = 1;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = Piece.PAWN(Colour.WHITE, new Coordinate(x, y));
        }

        y = 6;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = Piece.PAWN(Colour.BLACK, new Coordinate(x, y));
        }

        y = 7;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = (switch (x) {
                case 0, 7 -> Piece.ROOK(Colour.BLACK, new Coordinate(x, y));
                case 1, 6 -> Piece.KNIGHT(Colour.BLACK, new Coordinate(x, y));
                case 2, 5 -> Piece.BISHOP(Colour.BLACK, new Coordinate(x, y));
                case 3 -> Piece.QUEEN(Colour.BLACK, new Coordinate(x, y));
                case 4 -> Piece.KING(Colour.BLACK, new Coordinate(x, y));
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
     * @param destination
     */
    public void movePiece(@NonNull Coordinate start, @NonNull Coordinate destination) {
        Piece source = pieces[start.getX()][start.getY()];
        Piece target = pieces[destination.getX()][destination.getY()];

        if (source == null || source.equals(target) || (target != null && source.getColour().equals(target.getColour()))) {
            return;
        }
        // Verify the destination is a valid coordinate for this piece to move to
        if (!source.verifyMove(destination)) {
            return;
        }
        // Todo: validate movement condition is passed
        // Todo: verify path is empty, but only if the piece traverses along a path
        // Update the piece's internal position
        source.performMove(destination);
        // Update the piece on the board
        pieces[destination.getX()][destination.getY()] = pieces[start.getX()][start.getY()];
        pieces[start.getX()][start.getY()] = null;
    }

}
