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
        assertEquals(8, board.getColours().length);
        assertEquals(8, board.getColours()[0].length);
        assertEquals(32, board.getPieces().size());

        for (Piece piece : board.getPieces()) {
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

        Colour[][] occupiedList = board.getColours();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (x == 0 || x == 1) {
                    assertEquals(Colour.WHITE, occupiedList[x][y]);
                } else if (x == 6 || x == 7) {
                    assertEquals(Colour.BLACK, occupiedList[x][y]);
                } else {
                    assertEquals(Colour.NONE, occupiedList[x][y]);
                }
            }
        }
    }

    void movePiece_noPieceAtCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 2;
        int pieceY = 2;
        int nextX = 3;
        int nextY = 4;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // Nothing at location
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.NONE, board.getColours()[pieceX][pieceY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toSameCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = 0;
        int nextY = 1;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getColours()[pieceX][pieceY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toInvalidCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = -1;
        int nextY = 0;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(nextX, nextY));

        Board board = new Board();
        assertEquals(Colour.WHITE, board.getColours()[pieceX][pieceY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toValidSameColourOccupiedCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 1;
        int nextX = 1;
        int nextY = 3;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate nextC = new Coordinate(nextX, nextY); // White Pawn

        Board board = new Board();
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getColours()[pieceX][pieceY]);
        assertEquals(Colour.WHITE, board.getColours()[nextX][nextY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toValidOppositeColourOccupiedCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getColours()[pieceX][pieceY]);
        assertEquals(Colour.BLACK, board.getColours()[nextX][nextY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toValidOppositeColourOccupiedCoordinatePathOpen_pieceMovedAndOneFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.getColours()[0][1] = Colour.NONE; // Can be sufficient for path checks
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.NONE, board.getColours()[pieceX][pieceY]);
        assertEquals(Colour.WHITE, board.getColours()[nextX][nextY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toValidEmptyCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 3;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getColours()[pieceX][pieceY]);
        assertEquals(Colour.NONE, board.getColours()[nextX][nextY]);
        assertEquals(32, board.getPieces().size());
    }

    void movePiece_toValidEmptyCoordinatePathOpen_pieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 3;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate nextC = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.getColours()[0][1] = Colour.NONE; // Can be sufficient for path checks
        Board.movePiece(pieceC, nextC);

        assertEquals(Colour.NONE, board.getColours()[pieceX][pieceY]);
        assertEquals(Colour.WHITE, board.getColours()[nextX][nextY]);
        assertEquals(32, board.getPieces().size());
    }

}
