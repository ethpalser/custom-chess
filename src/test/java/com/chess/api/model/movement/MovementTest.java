package com.chess.api.model.movement;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class MovementTest {

    private List<Coordinate> rookCoordinates() {
        List<Coordinate> list = new ArrayList<>();
        for (int x = 1; x <= Coordinate.MAX_X; x++) {
            list.add(new Coordinate(x, 0));
        }
        for (int y = 1; y <= Coordinate.MAX_Y; y++) {
            list.add(new Coordinate(0, y));
        }
        return list;
    }

    private List<Coordinate> bishopCoordinates() {
        List<Coordinate> list = new ArrayList<>();
        for (int i = 1; i <= Coordinate.MAX_X; i++) {
            list.add(new Coordinate(i, i));
        }
        return list;
    }

    @Test
    void getCoordinates_relativeToPieceNoMirror_isOffsetByCoordinateAndOnlyForward() {
        Movement movement = new Movement(PathType.ADVANCE, false, false, false, rookCoordinates());
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Coordinate co = new Coordinate(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
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
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorX_isOffsetByCoordinateAndOnlyForwardAndBehind() {
        Movement movement = new Movement(PathType.ADVANCE, true, false, false, rookCoordinates());
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Coordinate co = new Coordinate(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
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
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorY_isOffsetByCoordinateAndOnlyForwardRightAndForwardLeft() {
        Movement movement = new Movement(PathType.ADVANCE, false, true, false, bishopCoordinates());
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Coordinate co = new Coordinate(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
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
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorXAndY_isOffsetByCoordinateAndMovesInAllDirections() {
        Movement movement = new Movement(PathType.ADVANCE, true, true, false, bishopCoordinates());
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Coordinate co = new Coordinate(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
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
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceReverseForBlack_isOffsetByCoordinateAndMovesBackwards() {
        Movement movement = new Movement(PathType.ADVANCE, false, false, true, bishopCoordinates());
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Coordinate co = new Coordinate(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.BLACK, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1 (forward movement)
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 2, mirrored on x-axis (backward movement)
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Coordinate.MAX_X; x++) {
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
        for (int y = coY; y <= Coordinate.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

}
