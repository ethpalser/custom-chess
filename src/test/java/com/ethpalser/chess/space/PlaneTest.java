package com.ethpalser.chess.space;

import com.ethpalser.chess.piece.Piece;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PlaneTest {

    @Test
    void testIsInBounds_givenPointWithinMinAndMax_thenTrue() {
        Plane<Piece> plane = new Plane<>(0, 0, 7, 7);
        Point point = new Point(4,3);
        boolean isInBounds = plane.isInBounds(point);

        assertTrue(isInBounds);
    }

}
