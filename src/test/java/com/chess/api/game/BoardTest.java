package com.chess.api.game;

import com.chess.api.game.piece.Piece;
import com.chess.api.game.piece.PieceType;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new Board();

        assertEquals(8, board.length());
        assertEquals(8, board.width());
        assertEquals(32, board.count());

        Piece piece;
        for (int x = 0; x < board.width(); x++) {
            for (int y = 0; y < board.length(); y++) {
                piece = board.getPiece(x, y);
                if (piece == null) {
                    continue;
                }

                Vector2D vector = piece.getPosition();
                if (vector.getY() == 0 || vector.getY() == 1) {
                    assertEquals(Colour.WHITE, piece.getColour());
                } else if (vector.getY() == 6 || vector.getY() == 7) {
                    assertEquals(Colour.BLACK, piece.getColour());
                }

                if (vector.getY() == 1 || vector.getY() == 6) {
                    assertEquals(PieceType.PAWN, piece.getType());
                } else {
                    switch (vector.getX()) {
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
        for (int x = 0; x < board.width(); x++) {
            board.setPiece(new Vector2D(x, y), null);
        }

        y = 6;
        for (int x = 0; x < board.width(); x++) {
            board.setPiece(new Vector2D(x, y), null);
        }
        assertEquals(16, board.count());

    }

    @Test
    void movePiece_noPieceAtCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 2;
        int pieceY = 2;
        int nextX = 4;
        int nextY = 3;

        Vector2D pieceC = new Vector2D(pieceX, pieceY); // Nothing at location
        Vector2D nextC = new Vector2D(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertNull(board.getPiece(pieceX, pieceY));
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toSameCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 1;
        int nextY = 0;

        Vector2D pieceC = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D nextC = new Vector2D(nextX, nextY);

        Board board = new Board();
        board.movePiece(pieceC, nextC);

        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toInvalidCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 0;
        int nextY = -2;

        Vector2D pieceC = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D invalid = new Vector2D(nextX, nextY);

        Board board = new Board();
        assertThrows(IndexOutOfBoundsException.class, () -> board.movePiece(pieceC, invalid));

        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void movePiece_toValidSameColourOccupiedCoordinate_noPieceMovedAndNoFewerPieces() {
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 2;
        int nextY = 2;

        Vector2D source = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D target = new Vector2D(nextX, nextY); // White Pawn

        Board board = new Board();
        board.movePiece(new Vector2D(nextX, 1), new Vector2D(nextX, nextY));
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

        Vector2D source = new Vector2D(pieceX, pieceY); // White Rook
        Vector2D target = new Vector2D(nextX, nextY); // Black Pawn

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

        Vector2D source = new Vector2D(pieceX, pieceY); // White Rook
        Vector2D target = new Vector2D(nextX, nextY); // Black Pawn

        Board board = new Board();
        board.setPiece(new Vector2D(0, 1), null); // Can be sufficient for path checks
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

        Vector2D source = new Vector2D(pieceX, pieceY); // White Bishop
        Vector2D target = new Vector2D(nextX, nextY); // Empty

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

        Vector2D source = new Vector2D(pieceX, pieceY); // White Bishop
        Vector2D target = new Vector2D(nextX, nextY); // Empty

        Board board = new Board();
        board.setPiece(new Vector2D(3, 1), null); // Clearing the path for a Bishop's move
        board.movePiece(source, target);

        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(31, board.count()); // One fewer piece from forced removal
    }

    @Test
    void movePiece_castleKingSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        Vector2D source = new Vector2D(4, 0);
        Vector2D target = new Vector2D(6, 0);

        Board board = new Board();
        board.setPiece(new Vector2D(5, 0), null);
        board.setPiece(new Vector2D(6, 0), null);

        board.movePiece(source, target);

        assertNull(board.getPiece(4, 0));
        assertNull(board.getPiece(7, 0));
        assertNotNull(board.getPiece(target));
        assertNotNull(board.getPiece(5,0));
    }


    @Test
    void movePiece_castleQueenSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        Vector2D source = new Vector2D(4, 0);
        Vector2D target = new Vector2D(2, 0);

        Board board = new Board();
        board.setPiece(new Vector2D(1, 0), null);
        board.setPiece(new Vector2D(2, 0), null);
        board.setPiece(new Vector2D(3, 0), null);

        board.movePiece(source, target);

        assertNull(board.getPiece(4, 0));
        assertNull(board.getPiece(0, 0));
        assertNotNull(board.getPiece(target));
        assertNotNull(board.getPiece(3,0));
    }

    @Test
    void movePiece_pawnEnPassantRightAndValid_pawnMovedAndOtherRemoved() {
        Vector2D source = new Vector2D(4, 6);
        Vector2D target = new Vector2D(4, 4);

        Board board = new Board();
        board.movePiece(new Vector2D(3, 1), new Vector2D(3,3));
        board.movePiece(new Vector2D(3,3), new Vector2D(3, 4));

        board.movePiece(source, target);
        board.movePiece(new Vector2D(3, 4), new Vector2D(4, 5)); // En Passant
        assertNull(board.getPiece(3,4));
        assertNull(board.getPiece(4,4));
        assertNotNull(board.getPiece(4,5));
    }

    @Test
    void movePiece_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        Vector2D source = new Vector2D(2, 6);
        Vector2D target = new Vector2D(2, 4);

        Board board = new Board();
        board.movePiece(new Vector2D(3, 1), new Vector2D(3,3));
        board.movePiece(new Vector2D(3,3), new Vector2D(3, 4));

        board.movePiece(source, target);
        board.movePiece(new Vector2D(3, 4), new Vector2D(2, 5)); // En Passant

        assertNull(board.getPiece(3,4));
        assertNull(board.getPiece(2,4));
        assertNotNull(board.getPiece(2,5));
    }

}
