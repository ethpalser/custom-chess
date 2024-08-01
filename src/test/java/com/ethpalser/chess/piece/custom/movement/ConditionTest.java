package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.Vector2D;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.custom.condition.Comparator;
import com.ethpalser.chess.piece.custom.condition.Conditional;
import com.ethpalser.chess.piece.custom.condition.Property;
import com.ethpalser.chess.piece.custom.condition.PropertyCondition;
import com.ethpalser.chess.piece.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.custom.reference.Location;
import com.ethpalser.chess.piece.custom.reference.Reference;
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
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
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
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
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
        CustomPiece customPiece = board.getPiece(2, 1);
        board.setPiece(new Vector2D(2, 2), customPiece);

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 1);

        Board board = new Board();
        CustomPiece customPiece = board.getPiece(2, 1);
        board.setPiece(new Vector2D(2, 3), customPiece);

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        Board board = new Board();

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Conditional conditionA = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.TRUE, new Property<>("hasMoved"), null);
        Conditional conditionB = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);
        Conditional conditionC = new PropertyCondition(new Reference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        Board board = new Board();
        CustomPiece white = board.getPiece(4, 1);
        board.setPiece(new Vector2D(4, 4), white);
        CustomPiece black = board.getPiece(5, 6);
        board.setPiece(new Vector2D(5, 4), black);
        board.setLastMoved(black);

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
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
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
        CustomPiece king = board.getPiece(4, 0);
        board.setPiece(new Vector2D(4, 1), king);

        // When
        Vector2D selected = new Vector2D(4, 1);
        Vector2D destination = new Vector2D(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);

        Board board = new Board();
        CustomPiece rook = board.getPiece(0, 0);
        // Forcing an illegal move, so it is marked as having moved
        board.setPiece(new Vector2D(0, 2), rook);
        board.setPiece(new Vector2D(0, 0), rook);

        // When
        Vector2D selected = new Vector2D(4, 0);
        Vector2D destination = new Vector2D(2, 0);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
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
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
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
