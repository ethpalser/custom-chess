package com.chess.api.model;

import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.MovementType;
import com.chess.api.model.movement.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class MovementTest {

    private List<Vector2D> rookCoordinates() {
        List<Vector2D> list = new ArrayList<>();
        for (int x = 1; x <= Vector2D.MAX_X; x++) {
            list.add(new Vector2D(x, 0));
        }
        for (int y = 1; y <= Vector2D.MAX_Y; y++) {
            list.add(new Vector2D(0, y));
        }
        return list;
    }

    private List<Vector2D> bishopCoordinates() {
        List<Vector2D> list = new ArrayList<>();
        for (int i = 1; i <= Vector2D.MAX_X; i++) {
            list.add(new Vector2D(i, i));
        }
        return list;
    }

    @Test
    void getCoordinates_relativeToPieceNoMirror_isOffsetByCoordinateAndOnlyForward() {
        Movement movement = new Movement(new Path(rookCoordinates()), MovementType.ADVANCE, false, false);
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Vector2D co = new Vector2D(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
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
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorX_isOffsetByCoordinateAndOnlyForwardAndBehind() {
        Movement movement = new Movement(new Path(rookCoordinates()), MovementType.ADVANCE, true, false);
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Vector2D co = new Vector2D(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
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
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorY_isOffsetByCoordinateAndOnlyForwardRightAndForwardLeft() {
        Movement movement = new Movement(new Path(bishopCoordinates()), MovementType.ADVANCE, false, true);
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Vector2D co = new Vector2D(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
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
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceMirrorXAndY_isOffsetByCoordinateAndMovesInAllDirections() {
        Movement movement = new Movement(new Path(bishopCoordinates()), MovementType.ADVANCE, true, true);
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Vector2D co = new Vector2D(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.WHITE, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
                assertEquals(boardMove[x][y], baseMove[x - coX][y - coY]);
            }
        }
        // Quadrant 2, mirrored on x-axis
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
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
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertEquals(boardMove[x][y], baseMove[coX - x][y - coY]);
            }
        }
    }

    @Test
    void getCoordinates_relativeToPieceReverseForBlack_isOffsetByCoordinateAndMovesBackwards() {
        Movement movement = new Movement(new Path(bishopCoordinates()), MovementType.ADVANCE, false, false);
        boolean[][] baseMove = movement.drawCoordinates(Colour.WHITE);

        Vector2D co = new Vector2D(3, 3);
        boolean[][] boardMove = movement.drawCoordinates(Colour.BLACK, co);

        final int coX = co.getX();
        final int coY = co.getY();
        // Quadrant 1 (forward movement)
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
                assertFalse(boardMove[x][y]);
            }
        }
        // Quadrant 2, mirrored on x-axis (backward movement)
        for (int y = coY - 1; y >= 0; y--) {
            for (int x = coX; x <= Vector2D.MAX_X; x++) {
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
        for (int y = coY; y <= Vector2D.MAX_Y; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                assertFalse(boardMove[x][y]);
            }
        }
    }

}
