package com.ethpalser.chess.board.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardType;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class CustomBoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());

        assertEquals(8, board.getPieces().length());
        assertEquals(8, board.getPieces().width());
        assertEquals(32, board.getPieces().size());

        Piece customPiece;
        for (int x = 0; x < board.getPieces().width(); x++) {
            for (int y = 0; y < board.getPieces().length(); y++) {
                customPiece = board.getPiece(x, y);
                if (customPiece == null) {
                    continue;
                }

                Point vector = customPiece.getPoint();
                if (vector.getY() == 0 || vector.getY() == 1) {
                    assertEquals(Colour.WHITE, customPiece.getColour());
                } else if (vector.getY() == 6 || vector.getY() == 7) {
                    assertEquals(Colour.BLACK, customPiece.getColour());
                }

                if (vector.getY() == 1 || vector.getY() == 6) {
                    assertEquals(PieceType.PAWN.getCode(), customPiece.getCode());
                } else {
                    switch (vector.getX()) {
                        case 0, 7 -> assertEquals(PieceType.ROOK.getCode(), customPiece.getCode());
                        case 1, 6 -> assertEquals(PieceType.KNIGHT.getCode(), customPiece.getCode());
                        case 2, 5 -> assertEquals(PieceType.BISHOP.getCode(), customPiece.getCode());
                        case 3 -> assertEquals(PieceType.QUEEN.getCode(), customPiece.getCode());
                        case 4 -> assertEquals(PieceType.KING.getCode(), customPiece.getCode());
                        default -> fail("Board size is invalid, or test coordinate is outside board bounds");
                    }
                }
            }
        }
    }

    @Test
    void count_newBoard_has32Pieces() {
        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void count_playedBoardWithNoPawns_has16Pieces() {
        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
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
