package com.chess.api.model.movement;

import com.chess.api.model.Action;
import com.chess.api.model.Board;
import com.chess.api.model.Colour;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.condition.Comparator;
import com.chess.api.model.movement.condition.Conditional;
import com.chess.api.model.movement.condition.Location;
import com.chess.api.model.movement.condition.Property;
import com.chess.api.model.movement.condition.PropertyCondition;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.movement.condition.ReferenceCondition;
import com.chess.api.model.piece.PieceType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.START), Comparator.EQUAL,
                new Property<>("type"), PieceType.PAWN);

        Board board = new Board();

        // When
        Vector2D selected = new Vector2D(4, 4);
        Vector2D destination = new Vector2D(5, 5);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);

        Board board = new Board();

        // When
        Vector2D selected = new Vector2D(4, 4);
        Vector2D destination = new Vector2D(5, 5);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        // En Passant condition requires moving 2
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

        Board board = new Board();
        board.movePiece(new Vector2D(2, 1), new Vector2D(2, 2));

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 1);

        Board board = new Board();
        board.movePiece(new Vector2D(2, 1), new Vector2D(2, 3));

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdjacentIsSameColour_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        Board board = new Board();

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnMovedTwoToAdjacentIsOppositeColour_isTrue() {
        // Given
        Conditional conditionA = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.TRUE, new Property<>("hasMoved"), null);
        Conditional conditionB = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);
        Conditional conditionC = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        Board board = new Board();
        board.movePiece(new Vector2D(4, 1), new Vector2D(4, 3));
        board.movePiece(new Vector2D(4, 3), new Vector2D(4, 4));
        board.movePiece(new Vector2D(5, 6), new Vector2D(5, 4));

        // When
        Vector2D selected = new Vector2D(4, 4);
        Vector2D destination = new Vector2D(5, 5);
        Action action = new Action(Colour.WHITE, selected, destination);
        boolean result = conditionA.isExpected(board, action)
                && conditionB.isExpected(board, action)
                && conditionC.isExpected(board, action);
        // Then
        assertTrue(result);
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.START),
                Comparator.EQUAL, new Property<>("type"), PieceType.KING);

        // When
        Board board = new Board();
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.START),
                Comparator.FALSE, new Property<>("hasMoved"), null);

        Board board = new Board();
        board.setPiece(new Vector2D(4, 1), null);
        board.movePiece(new Vector2D(4, 0), new Vector2D(4, 1));

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);

        Board board = new Board();
        board.movePiece(new Vector2D(0, 1), new Vector2D(0, 2));
        board.movePiece(new Vector2D(0, 0), new Vector2D(0,1));
        board.movePiece(new Vector2D(0, 1), new Vector2D(0,0));

        // When
        Vector2D selected = new Vector2D(4, 0);
        Vector2D destination = new Vector2D(2, 0);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Conditional condition = new ReferenceCondition(new Reference(Location.VECTOR, new Vector2D(1, 0)),
                Comparator.DOES_NOT_EXIST, null);

        Board board = new Board();
        // When
        Vector2D selected = new Vector2D(4, 0);
        Vector2D destination = new Vector2D(2, 0);
        Boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Conditional conditionA = new PropertyCondition(new Reference(Location.START),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional conditionB = new PropertyCondition(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional conditionC = new PropertyCondition(new Reference(Location.VECTOR, new Vector2D(1, 0)),
                Comparator.DOES_NOT_EXIST);

        Board board = new Board();
        board.setPiece(new Vector2D(1, 0), null);
        board.setPiece(new Vector2D(2, 0), null);
        board.setPiece(new Vector2D(3, 0), null);

        // When
        Vector2D selected = new Vector2D(4, 0);
        Vector2D destination = new Vector2D(2, 0);
        Action action = new Action(Colour.WHITE, selected, destination);
        boolean result = conditionA.isExpected(board, action)
                && conditionB.isExpected(board, action)
                && conditionC.isExpected(board, action);
        // Then
        assertTrue(result);
    }


}
