package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Piece;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PlaneTest {

    @Test
    void testIsInBounds_givenPointWithinMinAndMax_thenTrue() {
        Plane<Piece> plane = new Plane<>(7, 7);
        Point point = new Point(4,3);
        boolean isInBounds = plane.isInBounds(point);

        assertTrue(isInBounds);
    }

    @Test
    void testIsInBounds_givenPointOutsideMin_thenTrue() {
        Plane<Piece> plane = new Plane<>(7, 7);
        Point point = new Point(-1,3);
        boolean isInBounds = plane.isInBounds(point);

        assertFalse(isInBounds);
    }

    @Test
    void testIsInBounds_givenPointOutsideMax_thenTrue() {
        Plane<Piece> plane = new Plane<>(7, 7);
        Point point = new Point(4,8);
        boolean isInBounds = plane.isInBounds(point);

        assertFalse(isInBounds);
    }
}
