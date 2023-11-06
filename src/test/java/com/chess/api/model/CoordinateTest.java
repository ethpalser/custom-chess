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

    @Test
    void parseString_invalidFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Coordinate.parseString("aa"));
        assertThrows(IllegalArgumentException.class, Coordinate.parseString("abc"));
    }

    @Test
    void parseString_outOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, Coordinate.parseString("a9"));
        assertThrows(IndexOutOfBoundsException.class, Coordinate.parseString("i1"));
    }

    @Test
    void parseString_inBounds_isNotNull() {
        Coordinate coordinate = Coordinate.parseString("a1");
        assertNotNull(coordinate);
    }

}
