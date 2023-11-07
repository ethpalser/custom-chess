package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class KnightTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(1, 0);
        Knight knight = new Knight(Colour.WHITE, coordinate);
        assertNotEquals(null, knight.getCoordinate());
        assertFalse(knight.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 1;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Knight knight = new Knight(Colour.WHITE, start);

        Coordinate next = new Coordinate(x, y);
        knight.setCoordinate(next);
        assertEquals(x, knight.getCoordinate().getX());
        assertEquals(y, knight.getCoordinate().getY());
        assertFalse(knight.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 1;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Knight knight = new Knight(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 2;
        Coordinate moved = new Coordinate(nextX, nextY);
        knight.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        knight.setCoordinate(next);
        assertEquals(nextX, knight.getCoordinate().getX());
        assertEquals(nextY, knight.getCoordinate().getY());
        assertTrue(knight.isMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 1;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Knight knight = new Knight(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 2;
        Coordinate next = new Coordinate(nextX, nextY);
        knight.setCoordinate(next);
        assertEquals(nextX, knight.getCoordinate().getX());
        assertEquals(nextY, knight.getCoordinate().getY());
        assertTrue(knight.isMoved());
    }

}
