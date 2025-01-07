package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.game.ChessGame;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AbsoluteReferenceTest {
    @Test
    void absoluteRef_getReferences_givenLocationEmpty_thenIsEmpty() {
        // Given
        Point point = new Point("e4");
        Reference absRef = new Reference(Reference.Location.POINT, Direction.AT, point); // Nothing starts at e4
        // Then
        GameContext context = new GameContext();
        assertFalse(absRef.coordinates(context.toRecord(), null).isEmpty());
        assertTrue(absRef.coordinates(context.toRecord(), null).contains(point));
        assertNull(context.getBoard().get(point));
    }

    @Test
    void absoluteRef_getReferences_givenLocationNotEmpty_thenHasPiece() {
        // Given
        Point point = new Point("a1");
        Reference absRef = new Reference(Reference.Location.POINT, Direction.AT, point); // White Rook is on a1
        // Then
        GameContext context = new GameContext();
        assertFalse(absRef.coordinates(context.toRecord(), null).isEmpty());
        assertTrue(absRef.coordinates(context.toRecord(), null).contains(point));
        assertNotNull(context.getBoard().get(point));
    }

    @Test
    void absoluteRef_getReferences_givenPieceMovedOntoLocation_thenHasPiece() {
        // Given
        Point point = new Point("e4");
        Reference absRef = new Reference(Reference.Location.POINT, Direction.AT, point); // Nothing starts at e4
        // When
        ChessGame game = new ChessGame();
        game.updateGame(new Point("e2"), new Point("e4"), Colour.WHITE); // Moving white pawn e2 to e4
        // Then
        GameContext context = game.info().context();
        assertFalse(absRef.coordinates(context.toRecord(), null).isEmpty());
        assertTrue(absRef.coordinates(context.toRecord(), null).contains(point));
        assertNotNull(context.getBoard().get(point));
        // Note: References previously tracked context information, including pieces, which were sensitive to changes.
        // Now, references only involve coordinates and determine what coordinates are desired by its specification.
        // As a result, references are not affected by moving a piece. An absolute reference is always the same.
    }
}
