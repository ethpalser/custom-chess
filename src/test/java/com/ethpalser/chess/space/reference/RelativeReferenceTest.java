package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.game.event.Action;
import com.ethpalser.chess.game.ChessGame;
import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.move.config.Reference;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RelativeReferenceTest {
    @Test
    void pieceRef_getReferences_givenAtLocationAndNotMoved_thenIsItself() {
        // Given
        Point pawn = new Point("e2");
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.AT);
        // Then
        GameContext context = new GameContext();
        List<Coordinate> coordinates = pieceRef.coordinates(context.toRecord(), pawn);

        assertFalse(coordinates.isEmpty());
        assertTrue(coordinates.contains(pawn));
        assertNotNull(context.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pieceRef_getReferences_givenAtLocationAndMoved_thenIsItself() {
        // Given
        Point pawn = new Point("e2");
        Point target = new Point("e4"); // Mimic moving pawn to target
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.AT);
        // When
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, pawn, target));
        // Then
        // Note: ref.coordinates used by a piece would provide its own coordinate, which moved from "point" to "target"
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = pieceRef.coordinates(ctxRecord, target);

        assertFalse(coordinates.isEmpty());
        assertTrue(coordinates.contains(target));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pieceRef_getReferences_givenBackOfLocationAndMovedUpOne_thenIsEmpty() {
        // Given
        Point pawn = new Point("e2");
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.BACK);
        // When
        Game game = new ChessGame();
        Coordinate destination = new Point("e3");
        game.update(new Action(Colour.WHITE, pawn, destination));
        // Then
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = pieceRef.coordinates(ctxRecord, destination);

        assertFalse(coordinates.isEmpty());
        assertTrue(coordinates.contains(destination.translate(1, Direction.BACK)));
        assertNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pieceRef_getReferences_givenRightOfLocationAndPawnToRight_thenIsPawn() {
        // Given
        Point pawnE = new Point("e2");
        Point pawnF = new Point("f2");
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.RIGHT);
        // When
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, pawnE, pawnE.translate(1, Direction.NORTH)));
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // Filler
        game.update(new Action(Colour.WHITE, pawnF, pawnF.translate(1, Direction.NORTH)));
        // Then
        GameContext.Record ctxRecord = game.context().toRecord();
        Coordinate piece = pawnE.translate(1, Direction.NORTH);
        List<Coordinate> coordinates = pieceRef.coordinates(ctxRecord, piece);

        assertFalse(coordinates.isEmpty());
        assertTrue(coordinates.contains(pawnF.translate(1, Direction.NORTH)));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
        // Note: References previously tracked context information, including pieces, which were sensitive to changes.
        // Now, references only involve coordinates and determine what coordinates are desired by its specification.
        // As a result, references are not affected by moving a piece. A piece will provide its location.
    }

    @Test
    void pieceRef_getReferences_givenLeftFourOfKingAtStart_thenIsRook() {
        // Given
        Point king = new Point("e1");
        Point rook = new Point("a1");
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.LEFT, 4);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = pieceRef.coordinates(ctxRecord, king);

        assertFalse(coordinates.isEmpty());
        assertTrue(coordinates.contains(rook));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pieceRef_getReferences_givenOutOfBounds_thenIsEmpty() {
        // Given
        Point king = new Point("e1");
        Reference pieceRef = new Reference(Reference.Location.POINT, Direction.BACK, 2);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = pieceRef.coordinates(ctxRecord, king);

        assertFalse(coordinates.isEmpty());
        assertNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void edgeRef_getCoordinates_givenOriginToWest_thenA1() {
        // Given
        Point origin = new Point(0, 0);
        Reference edgeRef = new Reference(Reference.Location.WEST_EDGE, Direction.AT);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = edgeRef.coordinates(ctxRecord, origin);

        assertFalse(coordinates.isEmpty());
        assertEquals(new Point("a1"), coordinates.get(0));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void edgeRef_getCoordinates_givenOriginToEast_thenH1() {
        // Given
        Point origin = new Point(0, 0);
        Reference edgeRef = new Reference(Reference.Location.EAST_EDGE, Direction.AT);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = edgeRef.coordinates(ctxRecord, origin);

        assertFalse(coordinates.isEmpty());
        assertEquals(new Point("h1"), coordinates.get(0));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void edgeRef_getCoordinates_givenOriginToRightOfWest_thenB1() {
        // Given
        Point origin = new Point(0, 0);
        Reference edgeRef = new Reference(Reference.Location.WEST_EDGE, Direction.RIGHT);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = edgeRef.coordinates(ctxRecord, origin);

        assertFalse(coordinates.isEmpty());
        assertEquals(new Point("b1"), coordinates.get(0));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }

    @Test
    void edgeRef_getCoordinates_givenOriginToLeftOfEast_thenG1() {
        // Given
        Point origin = new Point(0, 0);
        Reference edgeRef = new Reference(Reference.Location.EAST_EDGE, Direction.LEFT);
        // Then
        Game game = new ChessGame();
        GameContext.Record ctxRecord = game.context().toRecord();
        List<Coordinate> coordinates = edgeRef.coordinates(ctxRecord, origin);

        assertFalse(coordinates.isEmpty());
        assertEquals(new Point("g1"), coordinates.get(0));
        assertNotNull(ctxRecord.getBoard().get(coordinates.get(0)));
    }
}
