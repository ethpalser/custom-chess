package com.ethpalser.chess.board;

import com.ethpalser.chess.board.custom.CustomBoard;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class StandardBoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new StandardBoard();

        assertEquals(8, board.getPieces().length());
        assertEquals(8, board.getPieces().width());
        assertEquals(32, board.getPieces().size());

        Piece piece;
        for (int x = 0; x < board.getPieces().width(); x++) {
            for (int y = 0; y < board.getPieces().length(); y++) {
                piece = board.getPiece(x, y);
                if (piece == null) {
                    continue;
                }

                Point vector = piece.getPoint();
                if (vector.getY() == 0 || vector.getY() == 1) {
                    assertEquals(Colour.WHITE, piece.getColour());
                } else if (vector.getY() == 6 || vector.getY() == 7) {
                    assertEquals(Colour.BLACK, piece.getColour());
                }

                if (vector.getY() == 1 || vector.getY() == 6) {
                    assertEquals(PieceType.PAWN.getCode(), piece.getCode());
                } else {
                    switch (vector.getX()) {
                        case 0, 7 -> assertEquals(PieceType.ROOK.getCode(), piece.getCode());
                        case 1, 6 -> assertEquals(PieceType.KNIGHT.getCode(), piece.getCode());
                        case 2, 5 -> assertEquals(PieceType.BISHOP.getCode(), piece.getCode());
                        case 3 -> assertEquals(PieceType.QUEEN.getCode(), piece.getCode());
                        case 4 -> assertEquals(PieceType.KING.getCode(), piece.getCode());
                        default -> fail("Board size is invalid, or test coordinate is outside board bounds");
                    }
                }
            }
        }
    }

    @Test
    void count_newBoard_has32Pieces() {
        CustomBoard board = new CustomBoard();
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void count_playedBoardWithNoPawns_has16Pieces() {
        CustomBoard board = new CustomBoard();
        int y = 1;
        for (int x = 0; x < board.getPieces().width(); x++) {
            board.addPiece(new Point(x, y), null);
        }

        y = 6;
        for (int x = 0; x < board.getPieces().width(); x++) {
            board.addPiece(new Point(x, y), null);
        }
        assertEquals(16, board.getPieces().size());

    }
}
