package com.chess.api.model.movement;

import com.chess.api.model.Board;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.condition.Condition;
import com.chess.api.model.movement.condition.Location;
import com.chess.api.model.movement.condition.Property;
import com.chess.api.model.movement.condition.PropertyState;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.PieceType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.START), new Property<>("type"),
                PropertyState.EQUAL, PieceType.PAWN);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.LAST_MOVED), new Property<>("hasMoved"),
                PropertyState.TRUE, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.LAST_MOVED), new Property<>("lastAdvanced"),
                PropertyState.EQUAL, 1);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.LAST_MOVED), new Property<>("lastAdvanced"),
                PropertyState.EQUAL, 1);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdjacentIsSameColour_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.LAST_MOVED), new Property<>("colour"),
                PropertyState.OPPOSITE, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnMovedTwoToAdjacentIsOppositeColour_isTrue() {
        // Given
        Condition conditionA = new Condition(new Reference(Location.LAST_MOVED), new Property<>("hasMoved"),
                PropertyState.TRUE, null);
        Condition conditionB = new Condition(new Reference(Location.LAST_MOVED), new Property<>("lastAdvanced"),
                PropertyState.EQUAL, 1);
        Condition conditionC = new Condition(new Reference(Location.LAST_MOVED), new Property<>("colour"),
                PropertyState.OPPOSITE, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        boolean result = conditionA.evaluate(board, selected, destination)
                && conditionB.evaluate(board, selected, destination)
                && conditionC.evaluate(board, selected, destination);
        // Then
        assertTrue(result);
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.START), new Property<>("type"),
                PropertyState.EQUAL, PieceType.KING);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.START), new Property<>("hasMoved"),
                PropertyState.FALSE, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0HasMoved_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                new Property<>("hasMoved"), PropertyState.FALSE, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Condition condition = new Condition(new Reference(Location.VECTOR, new Vector2D(1, 0)), null,
                PropertyState.DOES_NOT_EXIST, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        Boolean result = condition.evaluate(board, selected, destination);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Condition conditionA = new Condition(new Reference(Location.START), new Property<>("hasMoved"),
                PropertyState.FALSE, null);
        Condition conditionB = new Condition(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                new Property<>("hasMoved"), PropertyState.FALSE, null);
        Condition conditionC = new Condition(new Reference(Location.VECTOR, new Vector2D(1, 0)), null,
                PropertyState.DOES_NOT_EXIST, null);

        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        // When
        boolean result = conditionA.evaluate(board, selected, destination)
                && conditionB.evaluate(board, selected, destination)
                && conditionC.evaluate(board, selected, destination);
        // Then
        assertTrue(result);
    }


}
