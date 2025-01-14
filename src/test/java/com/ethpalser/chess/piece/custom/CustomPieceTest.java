package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 2
)
class CustomPieceTest {

    @Test
    void initialize_fromValidCoordinate_isNotNullAndHasCoordinateAndNotMoved() {
        Point start = new Point(2, 0);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);
        assertNotEquals(null, bishop.getCoordinate());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationAndHasNotMoved_isNotUpdatedAndHasMovedIsFalse() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        Point next = new Point(x, y);
        bishop.move(next);
        assertEquals(x, ((Point) bishop.getCoordinate()).getX());
        assertEquals(y, ((Point) bishop.getCoordinate()).getY());
        assertFalse(bishop.getHasMoved());
    }

    @Test
    void performMove_toSameLocationHasMoved_isNotUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Point moved = new Point(nextX, nextY);
        bishop.move(moved);

        Point next = new Point(nextX, nextY);
        bishop.move(next);
        assertEquals(nextX, ((Point) bishop.getCoordinate()).getX());
        assertEquals(nextY, ((Point) bishop.getCoordinate()).getY());
        assertTrue(bishop.getHasMoved());
    }

    @Test
    void performMove_toNewLocation_isUpdatedAndHasMovedIsTrue() {
        int x = 2;
        int y = 0;
        Point start = new Point(x, y);
        CustomPiece bishop = new CustomPiece(PieceType.BISHOP, Colour.WHITE, start);

        int nextX = 3;
        int nextY = 1;
        Point next = new Point(nextX, nextY);
        bishop.move(next);
        assertEquals(nextX, ((Point) bishop.getCoordinate()).getX());
        assertEquals(nextY, ((Point) bishop.getCoordinate()).getY());
        assertTrue(bishop.getHasMoved());
    }
}
