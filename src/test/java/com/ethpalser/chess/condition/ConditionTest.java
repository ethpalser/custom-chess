package com.ethpalser.chess.condition;

import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.game.ChessGame;
import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.config.ConditionalFactory;
import com.ethpalser.chess.move.config.ConditionalOptions;
import com.ethpalser.chess.move.config.Reference;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void evaluate_enPassantAtStartIsNotPawn_isFalse() {
        // Given
        Game game = new ChessGame();
        game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("c3"))); // Knight moves to c3

        Conditional condition = new PieceStateConditional(
                new Reference(Reference.Location.LAST_MOVED, Direction.AT),
                Operator.EQUAL,
                PropertyType.TYPE,
                PieceType.PAWN
        );
        // Then
        Coordinate enPassantPawn = new Point("d2"); // Not moved
        assertFalse(condition.isExpected(game.info().context().toRecord(), enPassantPawn));
    }

    @Test
    void evaluate_enPassantLastMovedIsNotPawn_isFalse() {
        // Given
        Game game = new ChessGame();
        game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("c3"))); // Knight moves to c3
        Conditional condition = new GameHistoryConditional(Operator.NOT_EQUAL, PropertyType.TYPE, PieceType.PAWN);

        // Then
        Coordinate enPassantPawn = new Point("d2"); // Not moved
        assertFalse(condition.isExpected(game.info().context().toRecord(), enPassantPawn));
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedOneSpace_isFalse() {
        // Given
        Game game = new ChessGame();
        Coordinate enPassantReady = new Point("d5");
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Pawn moving to be ready
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6"))); // Filler
        game.updateGame(new Action(Colour.WHITE, new Point("d4"), enPassantReady)); // Pawn ready for en passant
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // Only moved one

        Conditional condition = new GameHistoryConditional(Operator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        // Then
        assertFalse(condition.isExpected(game.info().context().toRecord(), enPassantReady));
    }

    @Test
    void evaluate_enPassantLastMovedAdvancedTwoSpaces_isTrue() {
        // Given
        Game game = new ChessGame();
        Coordinate enPassantReady = new Point("d5");
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Pawn moving to be ready
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6"))); // Filler
        game.updateGame(new Action(Colour.WHITE, new Point("d4"), enPassantReady)); // Pawn ready for en passant
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e5"))); // Can get en passant

        Conditional condition = new GameHistoryConditional(Operator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        // Then
        assertTrue(condition.isExpected(game.info().context().toRecord(), enPassantReady));
    }

    @Test
    void evaluate_enPassantLastMovedAndAdjacentIsSameColour_isFalse() {
        // Given
        Game game = new ChessGame();
        Coordinate enPassantReady = new Point("d5");
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Pawn moving to be ready
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6"))); // Filler
        game.updateGame(new Action(Colour.WHITE, new Point("d4"), enPassantReady)); // Pawn ready for en passant
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // Ignored
        game.updateGame(new Action(Colour.BLACK, new Point("e2"), new Point("e4"))); // Matches distance moved
        // Note: Each piece will provide its own colour when setting up this condition, and in this case it is WHITE
        Conditional condition = new GameHistoryConditional(Operator.NOT_EQUAL, PropertyType.COLOUR, Colour.WHITE);
        // Then
        assertFalse(condition.isExpected(game.info().context().toRecord(), enPassantReady));
    }

    @Test
    void evaluate_enPassantLastMovedIsPawnAndMovedTwoAndIsAdjacentAndIsOppositeColour_isTrue() {
        // Given
        Game game = new ChessGame();
        Coordinate enPassantReady = new Point("d5");
        Coordinate enPassantVictim = new Point("e5");
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Pawn moving to be ready
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6"))); // Filler
        game.updateGame(new Action(Colour.WHITE, new Point("d4"), enPassantReady)); // Pawn ready for en passant
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), enPassantVictim));

        Conditional condLastMovedIsPawn = new PieceStateConditional(new Reference(Reference.Location.LAST_MOVED,
                Direction.AT),
                Operator.EQUAL, PropertyType.CODE, PieceType.PAWN.toCode());
        Conditional condLastMovedTwo = new GameHistoryConditional(Operator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
        // Todo: Update LogCondition to verify Colour and Code
        Conditional condLastMovedNotAllied = new PieceStateConditional(new Reference(Reference.Location.LAST_MOVED,
                Direction.AT),
                Operator.NOT_EQUAL, PropertyType.COLOUR, Colour.WHITE);

        // When
        GameContext.Record ctxRecord = game.info().context().toRecord();
        assertTrue(condLastMovedIsPawn.isExpected(ctxRecord, enPassantReady));
        assertTrue(condLastMovedTwo.isExpected(ctxRecord, enPassantReady));
        assertTrue(condLastMovedNotAllied.isExpected(ctxRecord, enPassantReady));

        Coordinate enPassantDestination = new Point("e6");
        game.updateGame(new Action(Colour.WHITE, enPassantReady, enPassantDestination)); // EN PASSANT !!!

        // Then
        GameContext.Record ctxRecordAfter = game.info().context().toRecord();
        assertNotNull(ctxRecordAfter.getBoard().get(enPassantDestination));
        assertNull(ctxRecordAfter.getBoard().get(enPassantVictim)); // Piece should be captured by en passant
        assertNotNull(ctxRecordAfter.getLog().peek().getStartObject());
        assertEquals(ctxRecordAfter.getLog().peek().getStartObject().getCode(), PieceType.PAWN.toCode());
    }

    @Test
    void evaluate_castleAtStartIsNotKing_isFalse() {
        // Given
        Game game = new ChessGame();
        Conditional condition = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT),
                Operator.EQUAL, PropertyType.TYPE, PieceType.KING);
        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        assertFalse(condition.isExpected(ctxRecord, new Point("e2"))); // Pawn is at e2, not a king!
    }

    @Test
    void evaluate_castleAtStartHasMoved_isFalse() {
        // Given
        Game game = new ChessGame();
        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e3"))); // Opening space for king
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // filler
        Coordinate kingDestination = new Point("e2");
        game.updateGame(new Action(Colour.WHITE, new Point("e1"), kingDestination)); // Condition now fails

        Conditional condition = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT),
                Operator.FALSE, PropertyType.HAS_MOVED, null);
        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        assertFalse(condition.isExpected(ctxRecord, kingDestination));
    }

    @Test
    void evaluate_castleAtCoordinateA0PreviouslyMoved_isFalse() {
        // Given
        Game game = new ChessGame();
        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a3"))); // Opening space for rook
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // filler
        Coordinate queenSideRook = new Point("a1");
        Coordinate rookDestination = new Point("a2");
        game.updateGame(new Action(Colour.WHITE, queenSideRook, rookDestination)); // Condition now fails
        game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        game.updateGame(new Action(Colour.WHITE, rookDestination, queenSideRook)); // Reposition back to a1
        // This uses an absolute reference, so only the provided coordinate is used
        Conditional condition = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT,
                queenSideRook),
                Operator.FALSE, PropertyType.HAS_MOVED, false);
        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        assertFalse(condition.isExpected(ctxRecord, new Point("e1"))); // This is the king's condition
    }

    @Test
    void evaluate_castleAtCoordinateB0NotNull_isFalse() {
        // Given
        Game game = new ChessGame();
        Conditional condition = new PieceCompareConditional(new Reference(Reference.Location.POINT, Direction.AT,
                new Point("b1")),
                Operator.EQUAL, null);
        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        assertFalse(condition.isExpected(ctxRecord, new Point("e1"))); // This is the king's condition
    }

    @Test
    void evaluate_castleAtStartAndAtCoordinateA0NotMovedAndPathToCoordinateA0Empty_isTrue() {
        // Given
        Game game = new ChessGame();
        // Simulate all moves to setup a queen-side castle
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Open moving Bishop and Queen
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("c1"), new Point("e3"))); // Move Bishop out
        game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("a3"))); // Move Knight out
        game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("d2"))); // Move Queen out, path clear

        Conditional conditionA = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT),
                Operator.FALSE, PropertyType.HAS_MOVED, null);
        Conditional conditionB = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT,
                new Point("a1")),
                Operator.FALSE, PropertyType.HAS_MOVED, null);
        Conditional conditionC = new PieceStateConditional(new Reference(Reference.Location.POINT, Direction.AT,
                new Point("a1")),
                Operator.EQUAL, PropertyType.CODE, PieceType.ROOK.toCode());
        Conditional conditionD = new PieceCompareConditional(
                new Reference(Reference.Location.PATH, Direction.AT, new Path(new Point("b1"), new Point("c1"))),
                Operator.EQUAL, null);

        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        Coordinate king = new Point("e1");
        // These are all the king's conditions. It provides its own point wherever this is checked
        assertTrue(conditionA.isExpected(ctxRecord, king));
        assertTrue(conditionB.isExpected(ctxRecord, king));
        assertTrue(conditionC.isExpected(ctxRecord, king));
        assertTrue(conditionD.isExpected(ctxRecord, king));
    }

    @Test
    void evaluateOptions_castleQueenWithPiecesNotMovedAndClearPath_isTrue() {
        // Given
        Game game = new ChessGame();
        // Simulate all moves to setup a queen-side castle
        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Open moving Bishop and Queen
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("c1"), new Point("e3"))); // Move Bishop out
        game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("a3"))); // Move Knight out
        game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("d2"))); // Move Queen out, path clear

        ConditionalOptions conditionA = ConditionalOptions.pieceState(new Reference(Reference.Location.POINT,
                        Direction.AT),
                PropertyType.HAS_MOVED, Operator.FALSE, false);

        Reference qsRookRef = new Reference(Reference.Location.WEST_EDGE, Direction.AT);
        ConditionalOptions conditionB = ConditionalOptions.pieceState(qsRookRef, PropertyType.HAS_MOVED,
                Operator.FALSE, false);
        ConditionalOptions conditionC = ConditionalOptions.pieceState(qsRookRef, PropertyType.CODE, Operator.EQUAL,
                PieceType.ROOK.toCode());

        Reference start = new Reference(Reference.Location.POINT, Direction.LEFT);
        Reference end = new Reference(Reference.Location.WEST_EDGE, Direction.RIGHT);
        ConditionalOptions conditionD = ConditionalOptions.pathState(start, end, Operator.FALSE, null);

        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        Coordinate king = new Point("e1");
        // These are all the king's conditions. It provides its own point wherever this is checked
        ConditionalFactory factory = ConditionalFactory.getInstance();
        assertTrue(factory.create(conditionA).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionB).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionC).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionD).isExpected(ctxRecord, king));
    }

    @Test
    void evaluateOptions_castleKingWithPiecesNotMovedAndClearPath_isTrue() {
        // Given
        Game game = new ChessGame();
        // Simulate all moves to set up a king-side castle
        game.updateGame(new Action(Colour.WHITE, new Point("g2"), new Point("g4"))); // Bishop can move to h3
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("f1"), new Point("h3"))); // Bishop now at h3
        game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g6"))); // filler
        game.updateGame(new Action(Colour.WHITE, new Point("g1"), new Point("f3"))); // Path to castle now clear
        game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler

        ConditionalOptions conditionA = ConditionalOptions.pieceState(new Reference(Reference.Location.POINT,
                        Direction.AT),
                PropertyType.HAS_MOVED, Operator.FALSE, false);

        Reference qsRookRef = new Reference(Reference.Location.EAST_EDGE, Direction.AT);
        ConditionalOptions conditionB = ConditionalOptions.pieceState(qsRookRef, PropertyType.HAS_MOVED,
                Operator.FALSE, false);
        ConditionalOptions conditionC = ConditionalOptions.pieceState(qsRookRef, PropertyType.CODE, Operator.EQUAL,
                PieceType.ROOK.toCode());

        Reference start = new Reference(Reference.Location.POINT, Direction.RIGHT);
        Reference end = new Reference(Reference.Location.EAST_EDGE, Direction.LEFT);
        ConditionalOptions conditionD = ConditionalOptions.pathState(start, end, Operator.FALSE, null);

        // Then
        GameContext.Record ctxRecord = game.info().context().toRecord();
        Coordinate king = new Point("e1");
        // These are all the king's conditions. It provides its own point wherever this is checked
        ConditionalFactory factory = ConditionalFactory.getInstance();
        assertTrue(factory.create(conditionA).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionB).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionC).isExpected(ctxRecord, king));
        assertTrue(factory.create(conditionD).isExpected(ctxRecord, king));
    }

}
