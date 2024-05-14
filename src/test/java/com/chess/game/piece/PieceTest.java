package com.chess.game.piece;

import com.chess.game.Colour;
import com.chess.game.Vector2D;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Vector2D start = new Vector2D(2, 0);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);
        assertNotEquals(null, bishop.getPosition());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Vector2D start = new Vector2D(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        Vector2D next = new Vector2D(x, y);
        bishop.setPosition(next);
        assertEquals(x, bishop.getPosition().getX());
        assertEquals(y, bishop.getPosition().getY());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Vector2D start = new Vector2D(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Vector2D moved = new Vector2D(nextX, nextY);
        bishop.setPosition(moved);

        Vector2D next = new Vector2D(nextX, nextY);
        bishop.setPosition(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.getHasMoved());
    }

    @Test
    void performMove_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Vector2D start = new Vector2D(x, y);
        Piece bishop = new Piece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Vector2D next = new Vector2D(nextX, nextY);
        bishop.setPosition(next);
        assertEquals(nextX, bishop.getPosition().getX());
        assertEquals(nextY, bishop.getPosition().getY());
        assertTrue(bishop.getHasMoved());
    }
}
