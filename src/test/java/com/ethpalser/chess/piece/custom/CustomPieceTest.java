package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Colour;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CustomPieceTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Point start = new Point(2, 0);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);
        assertNotEquals(null, bishop.getPosition());
        assertFalse(bishop.hasMoved());
    }

    @Test
    void performMove_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        Point next = new Point(x, y);
        bishop.move(next);
        assertEquals(x, bishop.getPosition().getX());
        assertEquals(y, bishop.getPosition().getY());
        assertFalse(bishop.hasMoved());
    }

    @Test
    void performMove_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Point moved = new Point(nextX, nextY);
        bishop.move(moved);

        Point next = new Point(nextX, nextY);
        bishop.move(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.hasMoved());
    }

    @Test
    void performMove_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Point next = new Point(nextX, nextY);
        bishop.move(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.hasMoved());
    }
}
