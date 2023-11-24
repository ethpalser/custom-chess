package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class QueenTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(3, 0);
        Queen queen = new Queen(Colour.WHITE, coordinate);
        assertNotEquals(null, queen.getCoordinate());
        assertFalse(queen.getHasMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 3;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Queen queen = new Queen(Colour.WHITE, start);

        Coordinate next = new Coordinate(x, y);
        queen.setCoordinate(next);
        assertEquals(x, queen.getCoordinate().getX());
        assertEquals(y, queen.getCoordinate().getY());
        assertFalse(queen.getHasMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 3;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Queen queen = new Queen(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 1;
        Coordinate moved = new Coordinate(nextX, nextY);
        queen.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        queen.setCoordinate(next);
        assertEquals(nextX, queen.getCoordinate().getX());
        assertEquals(nextY, queen.getCoordinate().getY());
        assertTrue(queen.getHasMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 3;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Queen queen = new Queen(Colour.WHITE, start);

        int nextX = 2;
        int nextY = 1;
        Coordinate next = new Coordinate(nextX, nextY);
        queen.setCoordinate(next);
        assertEquals(nextX, queen.getCoordinate().getX());
        assertEquals(nextY, queen.getCoordinate().getY());
        assertTrue(queen.getHasMoved());
    }
}
