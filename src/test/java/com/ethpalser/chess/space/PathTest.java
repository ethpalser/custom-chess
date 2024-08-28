package com.ethpalser.chess.space;

import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void testConstructor_givenNullPoint_thenEmpty() {
        Path path = new Path((Point) null);

        Assertions.assertTrue(path.toSet().isEmpty());
    }

    @Test
    void testConstructor_givenOnePoint_thenOnlyHasPoint() {
        Point point = new Point();
        Path path = new Path(point);

        Assertions.assertFalse(path.toSet().isEmpty());
        Assertions.assertTrue(path.toSet().contains(point));
    }

    @Test
    void testConstructor_givenTwoAdjacentPoints_thenOnlyHasTwoPoints() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(0, 1);
        Path path = new Path(point1, point2);

        Set<Point> set = path.toSet();
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertTrue(set.contains(point1));
        Assertions.assertTrue(set.contains(point2));
    }

    @Test
    void testConstructor_givenTwoDistantHorizontalPoints_thenHasBothPointsAndBetween() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(7, 0);
        Path path = new Path(point1, point2);

        Set<Point> set = path.toSet();
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertTrue(set.contains(point1));
        Assertions.assertTrue(set.contains(point2));
        Assertions.assertEquals(8, set.size());
    }

    @Test
    void testConstructor_givenTwoDistantVerticalPoints_thenHasBothPointsAndBetween() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(0, 7);
        Path path = new Path(point1, point2);

        Set<Point> set = path.toSet();
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertTrue(set.contains(point1));
        Assertions.assertTrue(set.contains(point2));
        Assertions.assertEquals(8, set.size());
    }

    @Test
    void testConstructor_givenTwoDistantDiagonalPoints_thenHasBothPointsAndBetween() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(7, 7);
        Path path = new Path(point1, point2);

        Set<Point> set = path.toSet();
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertTrue(set.contains(point1));
        Assertions.assertTrue(set.contains(point2));
        Assertions.assertEquals(8, set.size());
    }

    @Test
    void testConstructor_givenTwoDistantNonLinearPoints_thenHasOnlyBothPoints() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(1, 3);
        Path path = new Path(point1, point2);

        Set<Point> set = path.toSet();
        Assertions.assertFalse(set.isEmpty());
        Assertions.assertTrue(set.contains(point1));
        Assertions.assertTrue(set.contains(point2));
        Assertions.assertEquals(2, set.size());
    }

}
