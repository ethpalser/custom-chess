package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.Property;
import com.ethpalser.chess.move.custom.condition.PropertyCondition;
import com.ethpalser.chess.move.custom.condition.ReferenceCondition;
import com.ethpalser.chess.space.reference.Location;
import com.ethpalser.chess.space.reference.PathReference;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.START), Comparator.EQUAL,
                new Property<>("type"), PieceType.PAWN);

        CustomBoard board = new CustomBoard();

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);

        CustomBoard board = new CustomBoard();

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        // En Passant condition requires moving 2
        Conditional condition = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

        CustomBoard board = new CustomBoard();
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 2), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 1);

        CustomBoard board = new CustomBoard();
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 3), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        CustomBoard board = new CustomBoard();

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Conditional conditionA = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.TRUE, new Property<>("hasMoved"), null);
        Conditional conditionB = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);
        Conditional conditionC = new PropertyCondition(new PathReference(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        CustomBoard board = new CustomBoard();
        Piece white = board.getPiece(4, 1);
        board.addPiece(new Point(4, 4), white);
        Piece black = board.getPiece(5, 6);
        board.addPiece(new Point(5, 4), black);

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        Action action = new Action(Colour.WHITE, selected, destination);
        // Then
        assertTrue(conditionA.isExpected(board, action));
        assertTrue(conditionB.isExpected(board, action));
        assertTrue(conditionC.isExpected(board, action));
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.START),
                Comparator.EQUAL, new Property<>("type"), PieceType.KING);

        // When
        CustomBoard board = new CustomBoard();
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.START),
                Comparator.FALSE, new Property<>("hasMoved"), null);

        CustomBoard board = new CustomBoard();
        board.addPiece(new Point(4, 1), null);
        Piece king = board.getPiece(4, 0);
        board.addPiece(new Point(4, 1), king);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional condition = new PropertyCondition(new PathReference(Location.VECTOR, new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);

        CustomBoard board = new CustomBoard();
        Piece rook = board.getPiece(0, 0);
        // Forcing an illegal move, so it is marked as having moved
        board.addPiece(new Point(0, 2), rook);
        board.addPiece(new Point(0, 0), rook);

        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Conditional condition = new ReferenceCondition(new PathReference(Location.VECTOR, new Point(1, 0)),
                Comparator.DOES_NOT_EXIST, null);

        CustomBoard board = new CustomBoard();
        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        boolean result = condition.isExpected(board, new Action(Colour.WHITE, selected, destination));
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Conditional conditionA = new PropertyCondition(new PathReference(Location.START),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional conditionB = new PropertyCondition(new PathReference(Location.VECTOR, new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional conditionC = new PropertyCondition(new PathReference(Location.VECTOR, new Point(1, 0)),
                Comparator.DOES_NOT_EXIST);

        CustomBoard board = new CustomBoard();
        board.addPiece(new Point(1, 0), null);
        board.addPiece(new Point(2, 0), null);
        board.addPiece(new Point(3, 0), null);

        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        Action action = new Action(Colour.WHITE, selected, destination);
        boolean result = conditionA.isExpected(board, action)
                && conditionB.isExpected(board, action)
                && conditionC.isExpected(board, action);
        // Then
        assertTrue(result);
    }


}
