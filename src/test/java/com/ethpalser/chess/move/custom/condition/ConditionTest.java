package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.custom.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.space.custom.reference.LogReference;
import com.ethpalser.chess.space.custom.reference.PathReference;
import com.ethpalser.chess.space.custom.reference.PieceReference;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH_START),
                Comparator.EQUAL,
                new Property<>("type"), PieceType.PAWN);

        CustomBoard board = new CustomBoard(new ChessLog());

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);

        CustomBoard board = new CustomBoard(new ChessLog());

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        // En Passant condition requires moving 2
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

        CustomBoard board = new CustomBoard(new ChessLog());
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 2), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                Comparator.EQUAL, new Property<>("lastMoveDistance"), 1);

        CustomBoard board = new CustomBoard(new ChessLog());
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 3), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                Comparator.NOT_EQUAL, new Property<>("colour"), null);

        CustomBoard board = new CustomBoard(new ChessLog());

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Log<Point, Piece> log = new ChessLog();

        CustomBoard board = new CustomBoard(log);
        Piece white = board.getPiece(4, 1);
        board.addPiece(new Point(4, 4), white);
        Piece black = board.getPiece(5, 6);
        board.addPiece(new Point(5, 4), black);

        Conditional<Piece> conditionA = new PropertyCondition<>(new LogReference<>(log), Comparator.EQUAL,
                new Property<>("type"), PieceType.PAWN);
        Conditional<Piece> conditionB = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        Conditional<Piece> conditionC = new ReferenceCondition<>(new LogReference<>(log), Comparator.EQUAL,
                new PieceReference(white, Direction.AT, 1, 0)); // Testing en passant to right

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        log.add(new ChessLogEntry(selected, destination, board.getPiece(selected), board.getPiece(destination)));
        // Then
        assertTrue(conditionA.isExpected(board.getPieces()));
        assertTrue(conditionB.isExpected(board.getPieces()));
        assertTrue(conditionC.isExpected(board.getPieces()));
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH_START),
                Comparator.EQUAL, new Property<>("type"), PieceType.KING);

        // When
        CustomBoard board = new CustomBoard(new ChessLog());
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH_START),
                Comparator.FALSE, new Property<>("hasMoved"), null);

        CustomBoard board = new CustomBoard(new ChessLog());
        board.addPiece(new Point(4, 1), null);
        Piece king = board.getPiece(4, 0);
        board.addPiece(new Point(4, 1), king);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.POINT, new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);

        CustomBoard board = new CustomBoard(new ChessLog());
        Piece rook = board.getPiece(0, 0);
        // Forcing an illegal move, so it is marked as having moved
        board.addPiece(new Point(0, 2), rook);
        board.addPiece(new Point(0, 0), rook);

        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Conditional<Piece> condition = new ReferenceCondition<>(new PathReference<>(Location.POINT, new Point(1, 0)),
                Comparator.DOES_NOT_EXIST, null);

        CustomBoard board = new CustomBoard(new ChessLog());
        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Conditional<Piece> conditionA = new PropertyCondition<>(new PathReference<>(Location.PATH_START),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional<Piece> conditionB = new PropertyCondition<>(new PathReference<>(Location.POINT, new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), null);
        Conditional<Piece> conditionC = new PropertyCondition<>(new PathReference<>(Location.POINT, new Point(1, 0)),
                Comparator.DOES_NOT_EXIST);

        CustomBoard board = new CustomBoard(new ChessLog());
        board.addPiece(new Point(1, 0), null);
        board.addPiece(new Point(2, 0), null);
        board.addPiece(new Point(3, 0), null);

        // When
        Point selected = new Point(4, 0);
        Point destination = new Point(2, 0);
        Action action = new Action(Colour.WHITE, selected, destination);
        boolean result = conditionA.isExpected(board.getPieces())
                && conditionB.isExpected(board.getPieces())
                && conditionC.isExpected(board.getPieces());
        // Then
        assertTrue(result);
    }


}
