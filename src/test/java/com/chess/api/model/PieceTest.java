package com.chess.api.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void initialize_fromIntegersAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece(0, 8));
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece(8, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece(-1, 0));
    }

    @Test
    void initialize_fromIntegersAndInBounds_isNotNull() {
        Coordinate coordinate = new Coordinate(0, 0);
        assertNotNull(coordinate);
    }

    @Test
    void initialize_fromStringAndInvalid_throwsInvalidArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Piece("a"));
        assertThrows(IllegalArgumentException.class, () -> new Piece("abc"));
    }

    @Test
    void initialize_fromStringAndOutOfBounds_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece("a9"));
        assertThrows(IndexOutOfBoundsException.class, () -> new Piece("i1"));
    }

    @Test
    void initialize_fromStringAndInBounds_isNotNull() {
        Piece piece = new Piece("a1");
        assertNotEquals(null, piece);
    }

}
