package com.chess.api.model.piece;

import com.chess.api.model.Coordinate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class BishopTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(2, 0);
        Bishop bishop = new Bishop(coordinate, Colour.WHITE);
        assertNotEquals(null, bishop.getCoordinate());
        assertFalse(bishop.isMoved());
    }
    }

}
