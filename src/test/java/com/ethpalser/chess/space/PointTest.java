package com.ethpalser.chess.space;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    void initialize_fromIntegersAndInBounds_isNotNull() {
        Point vector = new Point(0, 0);
        assertNotNull(vector);
    }

    @Test
    void initialize_fromCharsAndInBounds_isNotNull() {
        Point vector = new Point('a', '1');
        assertNotNull(vector);
        assertEquals(0, vector.getX());
        assertEquals(0, vector.getY());
    }

    @Test
    void equals_null_isFalse() {
        Point coA = new Point(5, 7);
        Point coB = null;
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_differentClass_isFalse() {
        Point coA = new Point(5, 7);
        Integer b = 5;
        boolean isEqual = coA.equals(b);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentX_isFalse() {
        Point coA = new Point(5, 7);
        Point coB = new Point(2, 7);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithDifferentY_isFalse() {
        Point coA = new Point(5, 7);
        Point coB = new Point(5, 5);
        boolean isEqual = coA.equals(coB);
        assertFalse(isEqual);
    }

    @Test
    void equals_coordinateWithSameXAndY_isTrue() {
        Point coA = new Point(5, 7);
        Point coB = new Point(5, 7);
        boolean isEqual = coA.equals(coB);
        assertTrue(isEqual);
    }

    @Test
    void hashCode_notEqualCoordinate_isNotEqual() {
        Point coA = new Point(5, 7);
        Point coB = new Point(5, 5);
        int hashA = coA.hashCode();
        int hashB = coB.hashCode();
        boolean isEqual = hashA == hashB;
        assertFalse(isEqual);
    }

    @Test
    void hashCode_equalCoordinate_isEqual() {
        Point coA = new Point(5, 7);
        Point coB = new Point(5, 7);
        int hashA = coA.hashCode();
        int hashB = coB.hashCode();
        boolean isEqual = hashA == hashB;
        assertTrue(isEqual);
    }

}
