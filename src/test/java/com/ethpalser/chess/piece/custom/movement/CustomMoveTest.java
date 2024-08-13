package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Colour;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class CustomMoveTest {

    private static final int MAX_X = 7;
    private static final int MAX_Y = 7;

    private List<Point> bishopCoordinates() {
        List<Point> list = new ArrayList<>();
        for (int i = 1; i <= MAX_X; i++) {
            list.add(new Point(i, i));
        }
        return list;
    }

    @Test
    void getCoordinates_relativeToPieceNoMirror_isOffsetByCoordinateAndOnlyForward() {
        CustomMove customMove = new CustomMove(new Path(bishopCoordinates()), CustomMoveType.ADVANCE, false, false);
        boolean[][] baseMove = customMove.drawCoordinates(Colour.WHITE);

        Point co = new Point(3, 3);
        boolean[][] boardMove = customMove.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= 7; y++) {
            for (int x = coX; x <= 7; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= MAX_X; x++) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 3, mirrored on x-axis and y-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 4, mirrored on y-axis
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorX_isOffsetByCoordinateAndOnlyForwardAndBehind() {
        CustomMove customMove = new CustomMove(new Path(bishopCoordinates()), CustomMoveType.ADVANCE, true, false);
        boolean[][] baseMove = customMove.drawCoordinates(Colour.WHITE);

        Point co = new Point(3, 3);
        boolean[][] boardMove = customMove.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][coY - y]);
            }
        }
        // Quadrant 3, mirrored on x-axis and y-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 4, mirrored on y-axis
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorY_isOffsetByCoordinateAndOnlyForwardRightAndForwardLeft() {
        CustomMove customMove = new CustomMove(new Path(bishopCoordinates()), CustomMoveType.ADVANCE, false, true);
        boolean[][] baseMove = customMove.drawCoordinates(Colour.WHITE);

        Point co = new Point(3, 3);
        boolean[][] boardMove = customMove.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= MAX_X; x++) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 3, mirrored on x-axis and y-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 4, mirrored on y-axis
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorXAndY_isOffsetByCoordinateAndMovesInAllDirections() {
        CustomMove customMove = new CustomMove(new Path(bishopCoordinates()), CustomMoveType.ADVANCE, true, true);
        boolean[][] baseMove = customMove.drawCoordinates(Colour.WHITE);

        Point co = new Point(3, 3);
        boolean[][] boardMove = customMove.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][coY - y]);
            }
        }
        // Quadrant 3, mirrored on x-axis and y-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][coY - y]);
            }
        }
        // Quadrant 4, mirrored on y-axis
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceReverseForBlack_isOffsetByCoordinateAndMovesBackwards() {
        CustomMove customMove = new CustomMove(new Path(bishopCoordinates()), CustomMoveType.ADVANCE, false, false);
        boolean[][] baseMove = customMove.drawCoordinates(Colour.WHITE);

        Point co = new Point(3, 3);
        boolean[][] boardMove = customMove.drawCoordinates(Colour.BLACK, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1 (forward movement)
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX; x <= MAX_X; x++) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 2, mirrored on x-axis (backward movement)
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][coY - y]);
            }
        }
        // Quadrant 3, mirrored on x-axis and y-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 4, mirrored on y-axis
        for (int y = coY; y <= MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

}
