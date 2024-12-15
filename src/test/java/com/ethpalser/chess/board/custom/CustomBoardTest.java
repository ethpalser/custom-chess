package com.ethpalser.chess.board.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class CustomBoardTest {

    @Test
    void initialize_default_is8x8AndHas32PiecesInCorrectLocation() {
        int width = 8;
        int height = 8;
        Space space = new Plane(width, height);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board board = new ChessBoard(space, factory);

        assertEquals(8, space.length(Space.AXIS.X));
        assertEquals(8, space.length(Space.AXIS.X));
        assertEquals(32, board.count());

        Piece customPiece;
        for (int x = 0; x < space.length(Space.AXIS.X); x++) {
            for (int y = 0; y < space.length(Space.AXIS.Y); y++) {
                customPiece = board.get(new Point(x, y));
                if (customPiece == null) {
                    continue;
                }

                Point vector = (Point) customPiece.getCoordinate();
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
        int width = 8;
        int height = 8;
        Space space = new Plane(width, height);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board board = new ChessBoard(space, factory);
        assertEquals(32, board.count());
    }

    @Test
    void count_playedBoardWithNoPawns_has16Pieces() {
        int width = 8;
        int height = 8;
        Space space = new Plane(width, height);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board board = new ChessBoard(space, factory);
        for (int y : new int[]{1, 6}) {
            for (int x = 0; x < space.length(Space.AXIS.X); x++) {
                board.add(new Point(x, y), null);
            }
        }
        assertEquals(16, board.count());

    }

}
