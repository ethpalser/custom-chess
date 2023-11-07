package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class KingTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(2, 0);
        King king = new King(Colour.WHITE, coordinate);
        assertNotEquals(null, king.getCoordinate());
        assertFalse(king.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 4;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        King king = new King(Colour.WHITE, start);

        Coordinate next = new Coordinate(x, y);
        king.setCoordinate(next);
        assertEquals(x, king.getCoordinate().getX());
        assertEquals(y, king.getCoordinate().getY());
        assertFalse(king.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 4;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        King king = new King(Colour.WHITE, start);

        int nextX = 5;
        int nextY = 1;
        Coordinate moved = new Coordinate(nextX, nextY);
        king.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        king.setCoordinate(next);
        assertEquals(nextX, king.getCoordinate().getX());
        assertEquals(nextY, king.getCoordinate().getY());
        assertTrue(king.isMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 4;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        King king = new King(Colour.WHITE, start);

        int nextX = 5;
        int nextY = 1;
        Coordinate next = new Coordinate(nextX, nextY);
        king.setCoordinate(next);
        assertEquals(nextX, king.getCoordinate().getX());
        assertEquals(nextY, king.getCoordinate().getY());
        assertTrue(king.isMoved());
    }
}
