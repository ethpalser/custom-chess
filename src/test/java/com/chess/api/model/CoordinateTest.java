package com.chess.api.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CoordinateTest {

    @Test
    void initialize_fromIntegersAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(0, 8));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(8, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(-1, 0));
    }

    @Test
    void initialize_fromIntegersAndInBounds_isNotNull() {
        Coordinate coordinate = new Coordinate(0, 0);
        assertNotNull(coordinate);
    }
}
