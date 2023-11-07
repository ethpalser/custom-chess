package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PawnTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(2, 0);
        Pawn pawn = new Pawn(Colour.WHITE, coordinate);
        assertNotEquals(null, pawn.getCoordinate());
        assertFalse(pawn.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Pawn pawn = new Pawn(Colour.WHITE, start);

        Coordinate next = new Coordinate(2, 0);
        pawn.setCoordinate(next);
        assertEquals(x, pawn.getCoordinate().getX());
        assertEquals(y, pawn.getCoordinate().getY());
        assertFalse(pawn.isMoved());
    }

    @Test
    void setCoordinate_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Pawn pawn = new Pawn(Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Coordinate moved = new Coordinate(nextX, nextY);
        pawn.setCoordinate(moved);

        Coordinate next = new Coordinate(nextX, nextY);
        pawn.setCoordinate(next);
        assertEquals(nextX, pawn.getCoordinate().getX());
        assertEquals(nextY, pawn.getCoordinate().getY());
        assertTrue(pawn.isMoved());
    }

    @Test
    void setCoordinate_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Coordinate start = new Coordinate(x, y);
        Pawn pawn = new Pawn(Colour.WHITE, start);

        Coordinate next = new Coordinate(3, 1);
        pawn.setCoordinate(next);
        assertEquals(x, pawn.getCoordinate().getX());
        assertEquals(y, pawn.getCoordinate().getY());
        assertTrue(pawn.isMoved());
    }

}
