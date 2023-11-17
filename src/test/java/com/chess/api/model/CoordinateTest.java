package com.chess.api.model;

import static org.junit.jupiter.api.Assertions.*;
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
    void initialize_fromCharsAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate('a', 'a'));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate('1', '1'));
    }

    @Test
    void initialize_fromCharsAndInBounds_isNotNull() {
        Coordinate coordinate = new Coordinate('a', '1');
        assertNotNull(coordinate);
        assertEquals(0, coordinate.getX());
        assertEquals(0, coordinate.getY());
    }

    @Test
    void parseString_invalidFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parseString("a"));
        assertThrows(IllegalArgumentException.class, () -> Coordinate.parseString("abc"));
    }

    @Test
    void parseString_outOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> Coordinate.parseString("a9"));
        assertThrows(IndexOutOfBoundsException.class, () -> Coordinate.parseString("i1"));
    }

    @Test
    void parseString_inBounds_isNotNull() {
        Coordinate coordinate = Coordinate.parseString("a1");
        assertNotNull(coordinate);
        assertEquals(0, coordinate.getX());
        assertEquals(0, coordinate.getY());
    }

    @Test
    void equals_null_isFalse() {
        Coordinate coA = new Coordinate(5, 7);
        Coordinate coB = null;
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentX_isFalse() {
        Coordinate coA = new Coordinate(5, 7);
        Coordinate coB = new Coordinate(2, 7);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentY_isFalse() {
        Coordinate coA = new Coordinate(5, 7);
        Coordinate coB = new Coordinate(5, 5);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithSameXAndY_isTrue() {
        Coordinate coA = new Coordinate(5, 7);
        Coordinate coB = new Coordinate(5, 7);
        boolean isEqual = coA.equals(coB);
        assertTrue(isEqual);
    }

}
