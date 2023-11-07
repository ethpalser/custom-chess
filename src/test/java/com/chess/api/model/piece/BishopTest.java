package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BishopTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(2, 0);
        Bishop bishop = new Bishop(Colour.WHITE, coordinate);
        assertNotEquals(null, bishop.getCoordinate());
        assertFalse(bishop.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Bishop bishop = new Bishop(Colour.WHITE, start);

        Coordinate next = new Coordinate(2, 0);
        bishop.setCoordinate(next);
        assertEquals(x, bishop.getCoordinate().getX());
        assertEquals(y, bishop.getCoordinate().getY());
        assertFalse(bishop.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Bishop bishop = new Bishop(Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Coordinate moved = new Coordinate(nextX, nextY);
        bishop.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        bishop.setCoordinate(next);
        assertEquals(nextX, bishop.getCoordinate().getX());
        assertEquals(nextY, bishop.getCoordinate().getY());
        assertTrue(bishop.isMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Bishop bishop = new Bishop(Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Coordinate next = new Coordinate(nextX, nextY);
        bishop.setCoordinate(next);
        assertEquals(nextX, bishop.getCoordinate().getX());
        assertEquals(nextY, bishop.getCoordinate().getY());
        assertTrue(bishop.isMoved());
    }
}
