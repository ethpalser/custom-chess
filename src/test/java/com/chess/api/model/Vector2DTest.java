package com.chess.api.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class Vector2DTest {

    @Test
    void initialize_fromIntegersAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D(0, 8));
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D(8, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D(-1, 0));
    }

    @Test
    void initialize_fromIntegersAndInBounds_isNotNull() {
        Vector2D vector = new Vector2D(0, 0);
        assertNotNull(vector);
    }

    @Test
    void initialize_fromCharsAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D('a', 'a'));
        assertThrows(IndexOutOfBoundsException.class, () -> new Vector2D('1', '1'));
    }

    @Test
    void initialize_fromCharsAndInBounds_isNotNull() {
        Vector2D vector = new Vector2D('a', '1');
        assertNotNull(vector);
        assertEquals(0, vector.getX());
        assertEquals(0, vector.getY());
    }

    @Test
    void parseString_invalidFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Vector2D.parseString("a"));
        assertThrows(IllegalArgumentException.class, () -> Vector2D.parseString("abc"));
    }

    @Test
    void parseString_outOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> Vector2D.parseString("a9"));
        assertThrows(IndexOutOfBoundsException.class, () -> Vector2D.parseString("i1"));
    }

    @Test
    void parseString_inBounds_isNotNull() {
        Vector2D vector = Vector2D.parseString("a1");
        assertNotNull(vector);
        assertEquals(0, vector.getX());
        assertEquals(0, vector.getY());
    }

    @Test
    void equals_null_isFalse() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = null;
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_differentClass_isFalse() {
        Vector2D coA = new Vector2D(5, 7);
        Integer b = 5;
        boolean isEqual = coA.equals(b);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentX_isFalse() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = new Vector2D(2, 7);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentY_isFalse() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = new Vector2D(5, 5);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithSameXAndY_isTrue() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = new Vector2D(5, 7);
        boolean isEqual = coA.equals(coB);
        assertTrue(isEqual);
    }

    @Test
    void hashCode_notEqualCoordinate_isNotEqual() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = new Vector2D(5, 5);
        int hashA = coA.hashCode();
        int hashB = coB.hashCode();
        boolean isEqual = hashA == hashB;
        assertFalse(isEqual);
    }

    @Test
    void hashCode_equalCoordinate_isEqual() {
        Vector2D coA = new Vector2D(5, 7);
        Vector2D coB = new Vector2D(5, 7);
        int hashA = coA.hashCode();
        int hashB = coB.hashCode();
        boolean isEqual = hashA == hashB;
        assertTrue(isEqual);
    }

}
