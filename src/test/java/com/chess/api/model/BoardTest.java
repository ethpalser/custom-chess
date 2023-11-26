package com.chess.api.model;

import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.PieceType;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
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

                Coordinate coordinate = piece.getPosition();
                if (coordinate.getY() == 0 || coordinate.getY() == 1) {
                    assertEquals(Colour.WHITE, piece.getColour());
                } else if (coordinate.getY() == 6 || coordinate.getY() == 7) {
                    assertEquals(Colour.BLACK, piece.getColour());
                }

                if (coordinate.getY() == 1 || coordinate.getY() == 6) {
                    assertEquals(PieceType.PAWN, piece.getType());
                } else {
                    switch (coordinate.getX()) {
                        case 0, 7 -> assertEquals(PieceType.ROOK, piece.getType());
                        case 1, 6 -> assertEquals(PieceType.KNIGHT, piece.getType());
                        case 2, 5 -> assertEquals(PieceType.BISHOP, piece.getType());
                        case 3 -> assertEquals(PieceType.QUEEN, piece.getType());
                        case 4 -> assertEquals(PieceType.KING, piece.getType());
                        default -> fail("Board size is invalid, or test coordinate is outside board bounds");
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
        int nextX = 4;
        int nextY = 3;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // Nothing at location
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertNull(board.getPieces()[pieceX][pieceY]);
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toSameCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 1;
        int nextY = 0;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate nextC = new Coordinate(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toInvalidCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 0;
        int nextY = -2;

        Coordinate pieceC = new Coordinate(pieceX, pieceY); // White Knight
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(nextX, nextY));

        Board board = new Board();
        assertEquals(Colour.WHITE, board.getPieces()[pieceX][pieceY].getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidSameColourOccupiedCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 2;
        int nextY = 2;

        Coordinate source = new Coordinate(pieceX, pieceY); // White Knight
        Coordinate target = new Coordinate(nextX, nextY); // White Pawn

        Board board = new Board();
        board.movePiece(Coordinate.at(nextX, 1), Coordinate.at(nextX, nextY));
        board.movePiece(source, target);

        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidOppositeColourOccupiedCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate source = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate target = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.movePiece(source, target);

        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.BLACK, board.getPiece(target).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidOppositeColourOccupiedCoordinatePathOpen_pieceMovedAndOneFewerPieces() {
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;

        Coordinate source = new Coordinate(pieceX, pieceY); // White Rook
        Coordinate target = new Coordinate(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.getPieces()[0][1] = null; // Can be sufficient for path checks
        board.movePiece(source, target);

        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(30, board.count()); // Two fewer pieces due to forced removal and capture
    }

    @Test
    void movePiece_toValidEmptyCoordinatePathBlocked_noPieceMovedAndNoFewerPieces() {
        int pieceX = 2;
        int pieceY = 0;
        int nextX = 4;
        int nextY = 2;

        Coordinate source = new Coordinate(pieceX, pieceY); // White Bishop
        Coordinate target = new Coordinate(nextX, nextY); // Empty

        Board board = new Board();
        board.movePiece(source, target);

        assertNotNull(board.getPiece(source));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertNull(board.getPiece(target));
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidEmptyCoordinatePathOpen_pieceMovedAndNoFewerPieces() {
        int pieceX = 2;
        int pieceY = 0;
        int nextX = 4;
        int nextY = 2;

        Coordinate source = new Coordinate(pieceX, pieceY); // White Bishop
        Coordinate target = new Coordinate(nextX, nextY); // Empty

        Board board = new Board();
        board.getPieces()[3][1] = null; // Clearing the path for a Bishop's move
        board.movePiece(source, target);

        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(31, board.count()); // One fewer piece from forced removal
    }

}
