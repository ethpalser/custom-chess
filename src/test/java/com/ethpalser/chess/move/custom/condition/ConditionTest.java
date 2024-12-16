package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.space.custom.reference.AbsoluteReference;
import com.ethpalser.chess.space.custom.reference.LogReference;
import com.ethpalser.chess.space.custom.reference.PathReference;
import com.ethpalser.chess.space.custom.reference.PieceReference;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Log<Coordinate, Piece> log = new ChessLog();
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference(Location.PATH),
                Comparator.EQUAL,
                PropertyType.TYPE, PieceType.PAWN);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        Piece black = board.get(selected);
        board.add(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));
        boolean result = condition.isExpected(board);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Log<Coordinate, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.NOT_EQUAL, PropertyType.TYPE, PieceType.PAWN);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);

        // When
        Point selected = new Point(4, 4);
        Point destination = new Point(5, 5);
        Piece black = board.get(selected);
        board.add(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));

        boolean result = condition.isExpected(board);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        // En Passant condition requires moving 2
        Log<Coordinate, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Piece customPiece = board.get(new Point(2, 1));
        board.add(new Point(2, 2), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(5, 2);
        Piece black = board.get(selected);
        board.add(destination, black);
        log.add(new ChessLogEntry(selected, destination, black, null));

        boolean result = condition.isExpected(board);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isTrue() {
        // Given
        Log<Coordinate, Piece> log = new ChessLog();
        Conditional<Piece> condition = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Piece customPiece = board.get(new Point(2, 1));
        board.add(new Point(2, 3), customPiece);

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(4, 3);
        Piece white = board.get(selected);
        board.add(destination, white);
        log.add(new ChessLogEntry(selected, destination, white, null));

        boolean result = condition.isExpected(board);
        // Then
        assertTrue(result);
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Log<Coordinate, Piece> log = new ChessLog();

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Piece customPiece = board.get(new Point(2, 1));
        board.add(new Point(2, 3), customPiece);

        Conditional<Piece> condition = new LogCondition<>(log, Comparator.NOT_EQUAL, PropertyType.COLOUR,
                customPiece.getColour());

        // When
        Point selected = new Point(4, 1);
        Point destination = new Point(4, 3);
        Piece white = board.get(selected);
        board.add(destination, white);
        log.add(new ChessLogEntry(selected, destination, white, null));

        boolean result = condition.isExpected(board);
        // Then
        assertFalse(result);
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Log<Coordinate, Piece> log = new ChessLog();

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Piece white = board.get(new Point(4, 1));
        board.add(new Point(4, 4), white);

        Conditional<Piece> conditionA = new PropertyCondition<>(new LogReference<>(log), Comparator.EQUAL,
                PropertyType.CODE, PieceType.PAWN.getCode());
        Conditional<Piece> conditionB = new LogCondition<>(log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        Conditional<Piece> conditionC = new ReferenceCondition<>(new LogReference<>(log), Comparator.EQUAL,
                new PieceReference(white, Direction.AT, 1, 0)); // Testing en passant to right

        // When
        Point enPassantTargetStart = new Point(5, 6);
        Point enPassantTargetEnd = new Point(5, 4);
        Piece black = board.get(enPassantTargetStart);
        board.add(enPassantTargetEnd, black);
        log.add(new ChessLogEntry(enPassantTargetStart, enPassantTargetEnd, black, null));

        // Then
        assertNotNull(board.get(new Point(4, 4)));
        assertNotNull(board.get(enPassantTargetEnd));
        assertNotNull(log.peek().getStartObject());
        assertEquals(log.peek().getStartObject().getCode(), PieceType.PAWN.getCode());

        assertTrue(conditionA.isExpected(board));
        assertTrue(conditionB.isExpected(board));
        assertTrue(conditionC.isExpected(board));
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference(Location.PATH),
                Comparator.EQUAL, PropertyType.TYPE, PieceType.KING);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        // Then
        boolean result = condition.isExpected(board);
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference(Location.PATH),
                Comparator.FALSE, PropertyType.HAS_MOVED, null);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        board.add(new Point(4, 1), null);
        Piece king = board.get(new Point(4, 0));
        board.add(new Point(4, 1), king);

        // Then
        boolean result = condition.isExpected(board);
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Conditional<Piece> condition = new PropertyCondition<>(new PathReference(Location.POINT, new Point(0, 0)),
                Comparator.FALSE, PropertyType.HAS_MOVED, false);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Piece rook = board.get(new Point(0, 0));
        // Forcing an illegal move, so it is marked as having moved
        board.add(new Point(0, 2), rook);
        board.get(new Point(0, 2)).setHasMoved(true);
        board.add(new Point(0, 0), rook);
        board.get(new Point(0, 0)).setHasMoved(true);

        // Then
        boolean result = condition.isExpected(board);
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Conditional<Piece> condition = new ReferenceCondition<>(new PathReference(Location.POINT, new Point(1, 0)),
                Comparator.EQUAL, null);

        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);

        // Then
        boolean result = condition.isExpected(board);
        assertFalse(result);
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        board.add(new Point(1, 0), null);
        board.add(new Point(2, 0), null);
        board.add(new Point(3, 0), null);

        Conditional<Piece> conditionA = new PropertyCondition<>(new PieceReference(board.get(new Point(4, 0))),
                Comparator.FALSE, PropertyType.HAS_MOVED, false);
        Conditional<Piece> conditionB = new PropertyCondition<>(new AbsoluteReference<>(new Point(0, 0)),
                Comparator.FALSE, PropertyType.HAS_MOVED, false);
        Conditional<Piece> conditionC = new PropertyCondition<>(new AbsoluteReference<>(new Point(0, 0)),
                Comparator.EQUAL, PropertyType.CODE, PieceType.ROOK.getCode());
        Conditional<Piece> conditionD = new ReferenceCondition<>(new PathReference(Location.PATH,
                new Point(3, 0), new Point(2, 0)), Comparator.EQUAL, null);

        // Then
        assertTrue(conditionA.isExpected(board));
        assertTrue(conditionB.isExpected(board));
        assertTrue(conditionC.isExpected(board));
        assertTrue(conditionD.isExpected(board));
    }


}
