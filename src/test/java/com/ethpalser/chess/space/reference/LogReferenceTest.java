package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.game.ChessGame;
import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.config.Reference;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LogReferenceTest {

    @Test
    void logRef_getReferences_givenNoMoves_thenIsEmpty() {
        // Given
        Reference logRef = new Reference(Reference.Location.LAST_MOVED, Direction.AT);
        // Then
        GameContext context = new GameContext();
        assertTrue(logRef.coordinates(context.toRecord(), new Point()).isEmpty());
    }

    @Test
    void logRef_getReferences_givenAtLeastOneMove_thenIsNotEmpty() {
        // Given
        Reference logRef = new Reference(Reference.Location.LAST_MOVED, Direction.AT);
        // When
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        // Then
        GameContext context = game.context();
        assertFalse(logRef.coordinates(context.toRecord(), new Point()).isEmpty());
        assertTrue(logRef.coordinates(context.toRecord(),
                new Point()).contains(context.getLog().peek().notation().toRecord().target()));
    }
}
