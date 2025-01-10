package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.move.config.Reference;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PathReferenceTest {
    @Test
    void pathRef_getReferences_givenVectorLocationAndEmpty_thenIsEmpty() {
        // Given
        Reference pathRef = new Reference(Reference.Location.PATH, Direction.AT, new Point("e4"));
        // Then
        GameContext context = new GameContext();
        List<Coordinate> coordinates = pathRef.coordinates(context.toRecord(), null);

        assertFalse(coordinates.isEmpty());
        assertNull(context.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pathRef_getReferences_givenVectorLocationAndFilled_thenIsNotEmpty() {
        // Given
        Coordinate coordinate = new Point("e2");
        Reference pathRef = new Reference(Reference.Location.PATH, Direction.AT, coordinate); // White pawn should be here
        // Then
        GameContext context = new GameContext();
        List<Coordinate> coordinates = pathRef.coordinates(context.toRecord(), null);

        assertFalse(coordinates.isEmpty());
        assertNotNull(context.getBoard().get(coordinates.get(0)));
    }

    @Test
    void pathRef_getReferences_givenPathToDestinationAndClear_thenIsEmpty() {
        // Given
        Path path = new Path(new Point("a3"), new Point("a6")); // Space between a-rank pawn on standard board
        Reference pathRef = new Reference(Reference.Location.PATH, Direction.AT, path);
        // Then
        GameContext context = new GameContext();
        List<Coordinate> coordinates = pathRef.coordinates(context.toRecord(), null);

        assertFalse(coordinates.isEmpty());
        assertNull(context.getBoard().get(coordinates.get(0)));
    }
}
