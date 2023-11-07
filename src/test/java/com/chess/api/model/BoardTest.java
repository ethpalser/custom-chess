package com.chess.api.model;

import com.chess.api.model.piece.Bishop;
import com.chess.api.model.piece.King;
import com.chess.api.model.piece.Knight;
import com.chess.api.model.piece.Pawn;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.Queen;
import com.chess.api.model.piece.Rook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new Board();
        assertEquals(8, board.getBoard().length);
        assertEquals(8, board.getBoard()[0].length);
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
    }
}
