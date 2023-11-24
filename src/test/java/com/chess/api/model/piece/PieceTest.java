package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate start = new Coordinate(2, 0);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);
        assertNotEquals(null, bishop.getPosition());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        Coordinate next = new Coordinate(x, y);
        bishop.performMove(next);
        assertEquals(x, bishop.getPosition().getX());
        assertEquals(y, bishop.getPosition().getY());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Coordinate moved = new Coordinate(nextX, nextY);
        bishop.performMove(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        bishop.performMove(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.getHasMoved());
    }

    @Test
    void performMove_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Coordinate next = new Coordinate(nextX, nextY);
        bishop.performMove(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.getHasMoved());
    }
}
