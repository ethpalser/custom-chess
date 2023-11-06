package com.chess.api.model;

import java.text.ParseException;
import org.junit.Assert;

public class CoordinateTest {

    public void initialize_fromIntegersAndOutOfBounds_throwsIndexOutOfBoundsException() {
        Assert.assertThrows(null, IndexOutOfBoundsException.class, new Coordinate(0, 8));
        Assert.assertThrows(null, IndexOutOfBoundsException.class, new Coordinate(0, -1));
        Assert.assertThrows(null, IndexOutOfBoundsException.class, new Coordinate(8, 0));
        Assert.assertThrows(null, IndexOutOfBoundsException.class, new Coordinate(-1, 0));
    }

    public void initialize_fromIntegersAndInBounds_isNotNull() {
        Coordinate coordinate = new Coordinate(0, 0);
        Assert.assertNotNull(coordinate);
    }
}
