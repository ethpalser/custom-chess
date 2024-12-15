package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Piece;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PlaneTest {

    @Test
    void testIsInBounds_givenPointWithinMinAndMax_thenTrue() {
        Space plane = new Plane(8, 8);
        Point point = new Point(4,3);
        boolean isInBounds = plane.isOutOfBounds(point);

        assertTrue(isInBounds);
    }

    @Test
    void testIsInBounds_givenPointOutsideMin_thenTrue() {
        Space plane = new Plane(8, 8);
        Point point = new Point(-1,3);
        boolean isInBounds = !plane.isOutOfBounds(point);

        assertFalse(isInBounds);
    }

    @Test
    void testIsInBounds_givenPointOutsideMax_thenTrue() {
        Space plane = new Plane(8, 8);
        Point point = new Point(4,8);
        boolean isInBounds = !plane.isOutOfBounds(point);

        assertFalse(isInBounds);
    }
}
