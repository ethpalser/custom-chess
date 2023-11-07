package com.chess.api.model.piece;

import com.chess.api.model.Coordinate;
import org.junit.jupiter.api.Test;

public class BishopTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Coordinate coordinate = new Coordinate(2, 0);
        Bishop bishop = new Bishop(coordinate, Colour.White);
        assertNotEquals(null, bishop.getCoordinate());
        assertFalse(bishop.hasMoved());
    }

}
