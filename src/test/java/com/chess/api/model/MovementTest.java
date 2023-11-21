package com.chess.api.model;

import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.PathType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class MovementTest {

    @Test
    void getCoordinates_relativeToPieceNoMirror_isOffsetByCoordinateAndOnlyForward() {
        Movement movement = new Movement(PathType.ADVANCE, false, false, false, new Coordinate(0, 1));
        boolean[][] baseMove = movement.drawCoordinates();

        Coordinate co = new Coordinate(1, 0);
        boolean[][] boardMove = movement.drawCoordinates(co);

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
        Movement movement = new Movement(PathType.ADVANCE, true, false, false, new Coordinate(0, 1));
        boolean[][] baseMove = movement.drawCoordinates();

        Coordinate co = new Coordinate(1, 0);
        boolean[][] boardMove = movement.drawCoordinates(co);

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
        Movement movement = new Movement(PathType.ADVANCE, false, true, false, new Coordinate(0, 1));
        boolean[][] baseMove = movement.drawCoordinates();

        Coordinate co = new Coordinate(1, 0);
        boolean[][] boardMove = movement.drawCoordinates(co);

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
        Movement movement = new Movement(PathType.ADVANCE, true, true, false, new Coordinate(0, 1));
        boolean[][] baseMove = movement.drawCoordinates();

        Coordinate co = new Coordinate(1, 0);
        boolean[][] boardMove = movement.drawCoordinates(co);

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
        Movement movement = new Movement(PathType.ADVANCE, false, false, true, new Coordinate(0, 1));
        boolean[][] baseMove = movement.drawCoordinates();

        Coordinate co = new Coordinate(1, 0);
        boolean[][] boardMove = movement.drawCoordinates(co, Colour.BLACK);

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
