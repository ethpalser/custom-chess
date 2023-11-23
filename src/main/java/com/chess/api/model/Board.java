package com.chess.api.model;

import com.chess.api.model.piece.Bishop;
import com.chess.api.model.piece.King;
import com.chess.api.model.piece.Knight;
import com.chess.api.model.piece.Pawn;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.Queen;
import com.chess.api.model.piece.Rook;
import lombok.Getter;

@Getter
public class Board {

    private final Piece[][] pieces;
    private Piece lastMoved;

    public Board() {
        Piece[][] pieceList = new Piece[8][8];

        int y = 0;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = (switch (x) {
                case 0, 7 -> new Rook(Colour.WHITE, new Coordinate(x, y));
                case 1, 6 -> new Knight(Colour.WHITE, new Coordinate(x, y));
                case 2, 5 -> new Bishop(Colour.WHITE, new Coordinate(x, y));
                case 3 -> new Queen(Colour.WHITE, new Coordinate(x, y));
                case 4 -> new King(Colour.WHITE, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
        }

        y = 1;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = new Pawn(Colour.WHITE, new Coordinate(x, y));
        }

        y = 6;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = new Pawn(Colour.BLACK, new Coordinate(x, y));
        }

        y = 7;
        for (int x = 0; x < 8; x++) {
            pieceList[x][y] = (switch (x) {
                case 0, 7 -> new Rook(Colour.BLACK, new Coordinate(x, y));
                case 1, 6 -> new Knight(Colour.BLACK, new Coordinate(x, y));
                case 2, 5 -> new Bishop(Colour.BLACK, new Coordinate(x, y));
                case 3 -> new Queen(Colour.BLACK, new Coordinate(x, y));
                case 4 -> new King(Colour.BLACK, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
        }
        this.pieces = pieceList;
        this.lastMoved = null;
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

    public Piece getAt(Coordinate co) {
        return pieces[co.getX()][co.getY()];
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
     * @param pieceC
     * @param nextC
     */
    public void movePiece(Coordinate pieceC, Coordinate nextC) {
        if (pieces[pieceC.getX()][pieceC.getY()] == pieces[nextC.getX()][nextC.getY()]) {
            return;
        }
    }

}
