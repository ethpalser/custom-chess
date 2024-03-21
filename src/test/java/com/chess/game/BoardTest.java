package test.java.com.chess.game;

import main.java.com.chess.game.Board;
import main.java.com.chess.game.Vector2D;
import main.java.com.chess.game.piece.Piece;
import main.java.com.chess.game.piece.PieceType;
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

}
