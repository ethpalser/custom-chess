package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RookTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(0, 0);
        Rook rook = new Rook(Colour.WHITE, coordinate);
        assertNotEquals(null, rook.getCoordinate());
        assertFalse(rook.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 0;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Rook rook = new Rook(Colour.WHITE, start);

        Coordinate next = new Coordinate(2, 0);
        rook.setCoordinate(next);
        assertEquals(x, rook.getCoordinate().getX());
        assertEquals(y, rook.getCoordinate().getY());
        assertFalse(rook.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 0;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Rook rook = new Rook(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 0;
        Coordinate moved = new Coordinate(nextX, nextY);
        rook.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        rook.setCoordinate(next);
        assertEquals(nextX, rook.getCoordinate().getX());
        assertEquals(nextY, rook.getCoordinate().getY());
        assertTrue(rook.isMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 0;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Rook rook = new Rook(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 0;
        Coordinate next = new Coordinate(nextX, nextY);
        rook.setCoordinate(next);
        assertEquals(nextX, rook.getCoordinate().getX());
        assertEquals(nextY, rook.getCoordinate().getY());
        assertTrue(rook.isMoved());
    }
}
