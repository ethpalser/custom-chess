package com.chess.api.model;

import com.chess.api.model.piece.Bishop;
import com.chess.api.model.piece.King;
import com.chess.api.model.piece.Knight;
import com.chess.api.model.piece.Pawn;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.Queen;
import com.chess.api.model.piece.Rook;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new Board();
        Piece[][] pieces = board.getPieces();

        assertEquals(8, pieces.length);
        assertEquals(8, pieces[0].length);
        assertEquals(32, board.count());

        Piece piece;
        for (int x = 0; x < board.getPieces().length; x++) {
            for (int y = 0; y < board.getPieces()[x].length; y++) {
                piece = board.getPieces()[x][y];
                if (piece == null) {
                    continue;
                }

                Coordinate coordinate = piece.getCoordinate();
                if (coordinate.getY() == 0 || coordinate.getY() == 1) {
                    assertEquals(Colour.WHITE, piece.getColour());
                } else if (coordinate.getY() == 6 || coordinate.getY() == 7) {
                    assertEquals(Colour.BLACK, piece.getColour());
                }

                if (coordinate.getY() == 1 || coordinate.getY() == 6) {
                    assertInstanceOf(Pawn.class, piece);
                } else {
                    switch (coordinate.getX()) {
                        case 0, 7 -> assertInstanceOf(Rook.class, piece);
                        case 1, 6 -> assertInstanceOf(Knight.class, piece);
                        case 2, 5 -> assertInstanceOf(Bishop.class, piece);
                        case 3 -> assertInstanceOf(Queen.class, piece);
                        case 4 -> assertInstanceOf(King.class, piece);
                        default -> assertInstanceOf(Piece.class, piece); // Should never occur
                    }
                }
            }
        }
    }

    @Test
    void count_newBoard_has32Pieces() {
        Board board = new Board();
        assertEquals(32, board.count());
    }

    @Test
    void count_playedBoardWithNoPawns_has16Pieces() {
        Board board = new Board();
        int y = 1;
        for (int x = 0; x < board.getPieces().length; x++) {
            board.getPieces()[x][y] = null;
        }

        y = 6;
        for (int x = 0; x < board.getPieces().length; x++) {
            board.getPieces()[x][y] = null;
        }
        assertEquals(16, board.count());

    }

    @Test
    void movePiece_noPieceAtCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 2;
        int pieceY = 2;
        int nextX = 3;
        int nextY = 4;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // Nothing at location
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertNull(board.getPieces()[pieceX][pieceY]);
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toSameCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = 0;
        int nextY = 1;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY]);
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toInvalidCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = -1;
        int nextY = 0;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(nextX, nextY));

        Board board = new Board();
        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidSameColourOccupiedCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = 1;
        int nextY = 3;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate nextC = new Coordinate(nextX, nextY); // White Pawn

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertEquals(Colour.WHITE, board.getPieces()[nextX][nextY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidOppositeColourOccupiedCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertEquals(Colour.BLACK, board.getPieces()[nextX][nextY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidOppositeColourOccupiedCoordinatePathOpen_pieceMovedAndOneFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.getPieces()[1][0] = null; // Can be sufficient for path checks
        board.movePiece(pieceC, nextC);

        assertNull(board.getPieces()[pieceX][pieceY]);
        assertEquals(Colour.WHITE, board.getPieces()[nextX][nextY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidEmptyCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 2;
        int nextX = 2;
        int nextY = 4;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Bishop
        Coordinate nextC = new Coordinate(nextX, nextY); // Empty

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertNull(board.getPieces()[nextX][nextY]);
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidEmptyCoordinatePathOpen_pieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 2;
        int nextX = 2;
        int nextY = 4;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Bishop
        Coordinate nextC = new Coordinate(nextX, nextY); // Empty

        Board board = new Board();
        board.getPieces()[1][3] = null;
        board.movePiece(pieceC, nextC);

        assertNull(board.getPieces()[pieceX][pieceY]);
        assertEquals(Colour.WHITE, board.getPieces()[nextX][nextY].getColour());
        assertEquals(32, board.count());
    }

}
