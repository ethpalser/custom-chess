package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardType;
import com.ethpalser.chess.board.ChessBoard;
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
import com.ethpalser.chess.space.custom.reference.AbsoluteReference;
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
        Log<Point, Piece> log = new ChessLog();
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH),
                Comparator.EQUAL,
                new Property<>("type"), PieceType.PAWN);

        Board board = new ChessBoard(BoardType.CUSTOM, log);

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        Piece black = board.getPiece(selected);
        board.addPiece(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));
        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Log<Point, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.NOT_EQUAL, PropertyType.TYPE, PieceType.PAWN);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        Piece black = board.getPiece(selected);
        board.addPiece(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));

        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        // En Passant condition requires moving 2
        Log<Point, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);

        Board board = new ChessBoard(BoardType.CUSTOM, log);
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 2), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        Piece black = board.getPiece(selected);
        board.addPiece(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));

        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isTrue() {
        // Given
        Log<Point, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 3), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(4, 3);
        Piece white = board.getPiece(selected);
        board.addPiece(destination, white);
        log.add(new ChessLogEntry(selected, destination, white, null));

        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertTrue(result);
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Log<Point, Piece> log = new ChessLog();

        Board board = new ChessBoard(BoardType.CUSTOM, log);
        Piece customPiece = board.getPiece(2, 1);
        board.addPiece(new Point(2, 3), customPiece);

        Conditional<Piece> condition = new LogCondition<>(log, Comparator.NOT_EQUAL, PropertyType.COLOUR,
                customPiece.getColour());

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(4, 3);
        Piece white = board.getPiece(selected);
        board.addPiece(destination, white);
        log.add(new ChessLogEntry(selected, destination, white, null));

        boolean result = condition.isExpected(board.getPieces());
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Log<Point, Piece> log = new ChessLog();

        Board board = new ChessBoard(BoardType.CUSTOM, log);
        Piece white = board.getPiece(4, 1);
        board.addPiece(new Point(4, 4), white);

        Conditional<Piece> conditionA = new PropertyCondition<>(new LogReference<>(log), Comparator.EQUAL,
                new Property<>("code"), PieceType.PAWN.getCode());
        Conditional<Piece> conditionB = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        Conditional<Piece> conditionC = new ReferenceCondition<>(new LogReference<>(log), Comparator.EQUAL,
                new PieceReference(white, Direction.AT, 1, 0)); // Testing en passant to right

        // When
        Point enPassantTargetStart = new Point(5, 6);
        Point enPassantTargetEnd = new Point(5, 4);
        Piece black = board.getPiece(enPassantTargetStart);
        board.addPiece(enPassantTargetEnd, black);
        log.add(new ChessLogEntry(enPassantTargetStart, enPassantTargetEnd, black, null));

        // Then
        assertTrue(conditionA.isExpected(board.getPieces()));
        assertTrue(conditionB.isExpected(board.getPieces()));
        assertTrue(conditionC.isExpected(board.getPieces()));
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH),
                Comparator.EQUAL, new Property<>("type"), PieceType.KING);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        // Then
        boolean result = condition.isExpected(board.getPieces());
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.PATH),
                Comparator.FALSE, new Property<>("hasMoved"), null);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        board.addPiece(new Point(4, 1), null);
        Piece king = board.getPiece(4, 0);
        board.addPiece(new Point(4, 1), king);

        // Then
        boolean result = condition.isExpected(board.getPieces());
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference<>(Location.POINT, new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        Piece rook = board.getPiece(0, 0);
        // Forcing an illegal move, so it is marked as having moved
        board.addPiece(new Point(0, 2), rook);
        board.addPiece(new Point(0, 0), rook);

        // Then
        boolean result = condition.isExpected(board.getPieces());
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Conditional<Piece> condition = new ReferenceCondition<>(new PathReference<>(Location.POINT, new Point(1, 0)),
                Comparator.DOES_NOT_EXIST, null);

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());

        // Then
        boolean result = condition.isExpected(board.getPieces());
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given

        Board board = new ChessBoard(BoardType.CUSTOM, new ChessLog());
        board.addPiece(new Point(1, 0), null);
        board.addPiece(new Point(2, 0), null);
        board.addPiece(new Point(3, 0), null);

        Conditional<Piece> conditionA = new PropertyCondition<>(new PieceReference(board.getPiece(4, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);
        Conditional<Piece> conditionB = new PropertyCondition<>(new AbsoluteReference<>(new Point(0, 0)),
                Comparator.FALSE, new Property<>("hasMoved"), false);
        Conditional<Piece> conditionC = new PropertyCondition<>(new AbsoluteReference<>(new Point(0, 0)),
                Comparator.EQUAL, new Property<>("code"), PieceType.ROOK.getCode());
        Conditional<Piece> conditionD = new ReferenceCondition<>(new PathReference<>(Location.PATH,
                new Point(3, 0), new Point(2, 0)), Comparator.DOES_NOT_EXIST, null);

        // Then
        assertTrue(conditionA.isExpected(board.getPieces()));
        assertTrue(conditionB.isExpected(board.getPieces()));
        assertTrue(conditionC.isExpected(board.getPieces()));
        assertTrue(conditionD.isExpected(board.getPieces()));
    }


}
