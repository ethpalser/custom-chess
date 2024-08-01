package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new Board();

        assertEquals(8, board.length());
        assertEquals(8, board.width());
        assertEquals(32, board.count());

        CustomPiece customPiece;
        for (int x = 0; x < board.width(); x++) {
            for (int y = 0; y < board.length(); y++) {
                customPiece = board.getPiece(x, y);
                if (customPiece == null) {
                    continue;
                }

                Point vector = customPiece.getPosition();
                if (vector.getY() == 0 || vector.getY() == 1) {
                    assertEquals(Colour.WHITE, customPiece.getColour());
                } else if (vector.getY() == 6 || vector.getY() == 7) {
                    assertEquals(Colour.BLACK, customPiece.getColour());
                }

                if (vector.getY() == 1 || vector.getY() == 6) {
                    assertEquals(PieceType.PAWN, customPiece.getType());
                } else {
                    switch (vector.getX()) {
                        case 0, 7 -> assertEquals(PieceType.ROOK, customPiece.getType());
                        case 1, 6 -> assertEquals(PieceType.KNIGHT, customPiece.getType());
                        case 2, 5 -> assertEquals(PieceType.BISHOP, customPiece.getType());
                        case 3 -> assertEquals(PieceType.QUEEN, customPiece.getType());
                        case 4 -> assertEquals(PieceType.KING, customPiece.getType());
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
            board.setPiece(new Point(x, y), null);
        }

        y = 6;
        for (int x = 0; x < board.width(); x++) {
            board.setPiece(new Point(x, y), null);
        }
        assertEquals(16, board.count());

    }

}
