package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardTestCases;
import com.ethpalser.chess.game.logic.GameTree;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ChessGameTest {

    @Test
    void testBotMovement_givenStartingBoard_thenBoardChanges() {
        // Given
        Game game = new ChessGame();
        GameTree tree = new GameTree(game);
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e6")));

        // When
        Action botBest = tree.nextBest(4);
        game.update(botBest);

        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(botBest.getStart()));
        assertNotNull(updatedBoard.get(botBest.getEnd()));
    }

    @Test
    void testPotentialUpdates_givenPieceCaptured_thenCapturedNotInUpdates() {
        // Given
        Game game = new ChessGame();
        GameStatus s1 = game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.update(new Action(Colour.BLACK, new Point("g8"), new Point("f6")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.update(new Action(Colour.WHITE, new Point("b1"), new Point("c3")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.update(new Action(Colour.BLACK, new Point("f6"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s4);

        // When
        // Capture piece at e4
        GameStatus s5 = game.update(new Action(Colour.WHITE, new Point("c3"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s5);

        // Then
        // Asserting that actions are all legal moves
        GameContext.Record ctxRecord = game.context().toRecord();
        Iterable<Action> blackActions = game.potentialUpdates();
        // Black moves are all legal moves by this player
        MoveMap blackMoves = new MoveMap(Colour.BLACK, ctxRecord);
        for (Action action : blackActions) {
            Piece piece = ctxRecord.getBoard().get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.getMoves(ctxRecord).moves().stream().anyMatch(m -> m.path().toSet().contains(action.getEnd())));
        }
        // Checking that a bug does not occur
        game.update(new Action(Colour.BLACK, new Point("a7"), new Point("a6")));
        game.update(new Action(Colour.WHITE, new Point("e4"), new Point("f6")));
        game.undo();
        game.undo();

        // Asserting that actions are all legal moves after undo
        Iterable<Action> blackActions2 = game.potentialUpdates();
        GameContext.Record ctxRecord2 = game.context().toRecord();

        MoveMap blackMoves2 = new MoveMap(Colour.BLACK, ctxRecord);
        for (Action action : blackActions2) {
            Piece piece = ctxRecord2.getBoard().get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves2.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.getMoves(ctxRecord2).moves().stream().anyMatch(m -> m.path().toSet().contains(action.getEnd())));
        }
        game.undo();
    }

    @Test
    void testPotentialUpdates_givenProgressedQueens_thenKingCannotMoveToThreatenedSpace() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.update(new Action(Colour.WHITE, new Point("d1"), new Point("g4")));
        game.update(new Action(Colour.BLACK, new Point("d8"), new Point("d6")));
        game.update(new Action(Colour.WHITE, new Point("e4"), new Point("e5")));

        // Then
        Iterable<Action> blackActions = game.potentialUpdates();
        GameContext.Record ctxRecord = game.context().toRecord();
        // Assert that all actions are legal moves
        MoveMap blackMoves = new MoveMap(Colour.BLACK, ctxRecord);
        for (Action action : blackActions) {
            Piece piece = ctxRecord.getBoard().get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.getMoves(ctxRecord).moves().stream().anyMatch(m -> m.path().toSet().contains(action.getEnd())));
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheck_thenNonBlockingMovesCauseNoChange() {
        // Given
        Game game = new ChessGame();
        GameStatus s1 = game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.BLACK_IN_CHECK, s3);

        // When
        // This move does not block the threat from h5 to e8
        GameStatus s4 = game.update(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.NO_CHANGE, s4);

        // Then
        Iterable<Action> blackActions = game.potentialUpdates();
        for (Action action : blackActions) {
            GameStatus result = game.update(action);
            // Any action that does not prevent check should not exist
            if (result == GameStatus.NO_CHANGE) {
                fail("available actions must prevent check");
            }
            game.undo();
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheckmate_thenNoPotentialMoves() {
        // Given
        Game game = new ChessGame();
        GameStatus s1 = game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.update(new Action(Colour.WHITE, new Point("b1"), new Point("c3")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.update(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.ONGOING, s4);

        // When
        GameStatus s5 = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.WHITE_WIN, s5);
        // Asserting that the game state cannot change if there is checkmate
        GameStatus s6 = game.update(new Action(Colour.BLACK, new Point("g5"), new Point("g4")));
        assertEquals(GameStatus.WHITE_WIN, s6);

        // Then
        Iterable<Action> blackActions = game.potentialUpdates();
        if (blackActions.iterator().hasNext()) {
            fail("actions must be empty");
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheckFromAdjacentPiece_thenKingCanCapture() {
        Game game = new ChessGame();
        GameStatus s1 = game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.update(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e5")));
        assertEquals(GameStatus.ONGOING, s4);
        GameStatus s5 = game.update(new Action(Colour.WHITE, new Point("h5"), new Point("f7")));
        assertEquals(GameStatus.BLACK_IN_CHECK, s5);

        Iterable<Action> blackActions = game.potentialUpdates();
        for (Action action : blackActions) {
            GameStatus result = game.update(action);
            if (result == GameStatus.NO_CHANGE) {
                fail("available actions must prevent check");
            }
            game.undo();
        }

        // Threats should be updated to reflect the king in check
        assertEquals(GameStatus.BLACK_IN_CHECK, game.status());
        // Checking that a bug does not occur
        GameStatus afterUndoG5F7 = game.undo();
        assertEquals(GameStatus.ONGOING, afterUndoG5F7);
        GameStatus afterUndoE7E5 = game.undo();
        assertEquals(GameStatus.ONGOING, afterUndoE7E5);
        // This is illegal, as this black pawn moving will open a path for the white queen to capture the black king
        GameStatus s6 = game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.NO_CHANGE, s6);
    }

    @Test
    void testEvaluateState_givenStartingBoard_thenZeroForBothPlayers() {
        // Given
        Game game = new ChessGame();
        // Then
        int value = game.score();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenEdgePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.update(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));

        int value = game.score();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e3")));
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e6")));

        int value = game.score();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnThreatenCenterForBothPlayers_thenZeroForBothPlayers() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));

        int value = game.score();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenWhiteCapturePawn_thenPositiveState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.update(new Action(Colour.WHITE, new Point("e4"), new Point("d5")));

        int value = game.score();
        assertTrue(value >= 0);
    }

    @Test
    void testEvaluateState_givenBlackCapturePawn_thenNegativeState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.update(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.update(new Action(Colour.BLACK, new Point("d5"), new Point("e4")));

        int value = game.score();
        assertTrue(value <= 0);
    }


    @Test
    void testEvaluateState_givenWhiteControlCenter_thenPositiveState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d4")));
        game.update(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.update(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.update(new Action(Colour.BLACK, new Point("h7"), new Point("h5")));

        int value = game.score();
        assertTrue(value > 0);
    }


    @Test
    void testEvaluateState_givenBlackControlCenter_thenNegativeState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.update(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.update(new Action(Colour.WHITE, new Point("h2"), new Point("h4")));
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e5")));

        int value = game.score();
        assertTrue(value < 0);
    }

    @Test
    void testEvaluateState_givenWhitePawnChain_thenPositiveState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.update(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.update(new Action(Colour.WHITE, new Point("b2"), new Point("b3")));
        game.update(new Action(Colour.BLACK, new Point("h7"), new Point("h5")));

        int value = game.score();
        assertTrue(value > 0);
    }


    @Test
    void testEvaluateState_givenBlackPawnChain_thenNegativeState() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.update(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.update(new Action(Colour.WHITE, new Point("h2"), new Point("h4")));
        game.update(new Action(Colour.BLACK, new Point("b7"), new Point("b6")));

        int value = game.score();
        assertTrue(value < 0);
    }

    @Test
    void updateGame_pawnPromotion_changesToQueen() {
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("b2"), new Point("b4")));
        game.update(new Action(Colour.BLACK, new Point("h7"), new Point("h6")));
        game.update(new Action(Colour.WHITE, new Point("b4"), new Point("b5")));
        game.update(new Action(Colour.BLACK, new Point("h6"), new Point("h5")));
        game.update(new Action(Colour.WHITE, new Point("b5"), new Point("b6")));
        game.update(new Action(Colour.BLACK, new Point("h5"), new Point("h4")));
        game.update(new Action(Colour.WHITE, new Point("b6"), new Point("a7")));
        game.update(new Action(Colour.BLACK, new Point("h4"), new Point("h3")));

        // When
        Point start = new Point("a7");
        Point end = new Point("b8");
        game.update(new Action(Colour.WHITE, start, end));

        // Then
        GameInfo info = game.info();
        Board<Coordinate> updatedBoard = info.context().getBoard();
        assertNotNull(updatedBoard.get(end));

        // Todo: Auto promote pawn to queen, then update this to make an update with a Promote event
        assertNotNull(info.context().getLog().peek().notation().toRecord().promoteCode());
        assertEquals("Q", updatedBoard.get(end).getCode());

        // Testing Undo and Redo as well
        game.undo();
        updatedBoard = info.context().getBoard();
        assertNotNull(updatedBoard.get(start));
        assertEquals("P", updatedBoard.get(start).getCode());

        game.redo();
        updatedBoard = info.context().getBoard();
        assertNotNull(updatedBoard.get(end));
        assertEquals("Q", updatedBoard.get(end).getCode());
    }

    // region Piece Movement
    @Test
    void executeAction_noPieceAtCoordinate_hasNoChange() {
        // Given
        Game game = new ChessGame();
        // When
        Point unoccupied = new Point(2, 2);
        Point target = new Point(4, 3);
        GameStatus status = game.update(new Action(Colour.WHITE, unoccupied, target));
        // Then
        assertEquals(GameStatus.NO_CHANGE, status);

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(unoccupied));
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toSameCoordinate_hasNoChange() {
        // Given
        Game game = new ChessGame();
        // When
        Point pawn = new Point(2, 1);
        GameStatus status = game.update(new Action(Colour.WHITE, pawn, pawn));
        // Then
        assertEquals(GameStatus.NO_CHANGE, status);

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNotNull(updatedBoard.get(pawn));
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toInvalidCoordinate_hasNoChange() {
        // Given
        Game game = new ChessGame();
        // When
        Point knight = new Point(2, 0);
        Point target = new Point(0, -1); // -1 is out of bounds for a default board's space
        GameStatus status = game.update(new Action(Colour.WHITE, knight, target));
        // Then
        assertEquals(GameStatus.NO_CHANGE, status);

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNotNull(updatedBoard.get(knight));
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toValidSameColourOccupiedCoordinate_hasNoChange() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("c2"), new Point("c3"))); // Filler
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // Filler
        // When
        Point knight = new Point("b1"); // White Knight
        Point target = new Point("c3"); // White Pawn
        GameStatus status = game.update(new Action(Colour.WHITE, knight, target));
        // Then
        assertEquals(GameStatus.NO_CHANGE, status);

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNotNull(updatedBoard.get(knight));
        assertNotNull(updatedBoard.get(target));
        assertEquals(Colour.WHITE, updatedBoard.get(knight).getColour());
        assertEquals(Colour.WHITE, updatedBoard.get(target).getColour());
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toValidOppositeColourOccupiedCoordinatePathBlocked_hasNoChange() {
        // Given
        Game game = new ChessGame();
        // When
        Point rook = new Point("a1"); // White Rook
        Point target = new Point("a7"); // Black Pawn
        GameStatus status = game.update(new Action(Colour.WHITE, rook, target));
        // Then
        assertEquals(GameStatus.NO_CHANGE, status); // Blocked at a2 by white pawn

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNotNull(updatedBoard.get(rook));
        assertNotNull(updatedBoard.get(target));
        assertEquals(Colour.WHITE, updatedBoard.get(rook).getColour());
        assertEquals(Colour.BLACK, updatedBoard.get(target).getColour());
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toValidOppositeColourOccupiedCoordinatePathOpen_pieceMovedAndOneFewerPieces() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d3"))); // Bishop can move to e3
        game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("c1"), new Point("e3"))); // Bishop ready to capture a7
        game.update(new Action(Colour.BLACK, new Point("f6"), new Point("f5"))); // filler
        // When
        Point bishop = new Point("e3"); // White Bishop
        Point target = new Point("a7"); // Black Pawn
        game.update(new Action(Colour.WHITE, bishop, target)); // Bishop capture at b7
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(bishop));
        assertNotNull(updatedBoard.get(target));
        assertEquals(Colour.WHITE, updatedBoard.get(target).getColour());
        assertEquals(31, updatedBoard.count()); // Black pawn should be captured
    }

    @Test
    void executeAction_toValidEmptyCoordinatePathBlocked_hasNoChange() {
        // Given
        Game game = new ChessGame();
        // When
        Point bishop = new Point("c1"); // White Bishop
        Point target = new Point("e3"); // Empty
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, bishop, target));

        // Then
        assertEquals(GameStatus.NO_CHANGE, status);

        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNotNull(updatedBoard.get(bishop));
        assertEquals(Colour.WHITE, updatedBoard.get(bishop).getColour());
        assertNull(updatedBoard.get(target));
        assertEquals(32, updatedBoard.count());
    }

    @Test
    void executeAction_toValidEmptyCoordinatePathOpen_pieceMovedAndNoFewerPieces() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d3"))); // Bishop can move to d3
        game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        // When
        Point bishop = new Point("c1"); // White Bishop
        Point target = new Point("e3"); // Empty
        game.update(new Action(Colour.WHITE, bishop, target));
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(bishop));
        assertNotNull(updatedBoard.get(target));
        assertEquals(Colour.WHITE, updatedBoard.get(target).getColour());
        assertEquals(32, updatedBoard.count()); // One fewer piece from forced removal
    }

    @Test
    void executeAction_castleKingSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("g2"), new Point("g4"))); // Bishop can move to h3
        game.update(new Action(Colour.BLACK, new Point("h7"), new Point("h6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("f1"), new Point("h3"))); // Bishop now at h3
        game.update(new Action(Colour.BLACK, new Point("g7"), new Point("g6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("g1"), new Point("f3"))); // Path to castle now clear
        game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        // When
        Point king = new Point("e1");
        Point target = new Point("g1");
        game.update(new Action(Colour.WHITE, king, target));
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        // Did these pieces move?
        assertNotNull(updatedBoard.get(target));
        assertNotNull(updatedBoard.get(target.translate(1, Direction.LEFT))); // Rook at f1 exists
        // Were these pieces removed from their original location
        assertNull(updatedBoard.get(king));
        assertNull(updatedBoard.get(new Point("h1"))); // Rook at h1 should be at f1
    }


    @Test
    void executeAction_castleQueenSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Bishop can move to e3
        game.update(new Action(Colour.BLACK, new Point("h7"), new Point("h6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("c1"), new Point("e3"))); // Bishop now at e3
        game.update(new Action(Colour.BLACK, new Point("g7"), new Point("g6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("b1"), new Point("c3"))); // Knight now out of path
        game.update(new Action(Colour.BLACK, new Point("f7"), new Point("f6"))); // filler
        game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d2"))); // Path to castle now clear
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e6"))); // filler
        // When
        Point king = new Point("e1");
        Point target = new Point("c1");
        game.update(new Action(Colour.WHITE, king, target));
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        // Did these pieces move?
        assertNotNull(updatedBoard.get(target));
        assertNotNull(updatedBoard.get(target.translate(1, Direction.RIGHT))); // Rook at d1 exists
        // Were these pieces removed from their original location
        assertNull(updatedBoard.get(king));
        assertNull(updatedBoard.get(new Point("a1"))); // Rook at a1 should be at d1
    }

    @Test
    void executeAction_pawnEnPassantRightAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Starting to move d2 to d5
        game.update(new Action(Colour.BLACK, new Point("b7"), new Point("b5"))); // filler
        game.update(new Action(Colour.WHITE, new Point("d4"), new Point("d5"))); // Pawn d5 ready
        game.update(new Action(Colour.BLACK, new Point("e7"), new Point("e5"))); // Pawn e5 can get en passant
        // When
        Point pawn = new Point("d5");
        Point target = new Point("e6");
        game.update(new Action(Colour.WHITE, pawn, target)); // En Passant
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(pawn));
        assertNull(updatedBoard.get(target.translate(1, Direction.BACK)));
        assertNotNull(updatedBoard.get(target));
    }

    @Test
    void executeAction_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Game game = new ChessGame();
        game.update(new Action(Colour.WHITE, new Point("d2"), new Point("d4"))); // Starting to move d2 to d5
        game.update(new Action(Colour.BLACK, new Point("b7"), new Point("b5"))); // filler
        game.update(new Action(Colour.WHITE, new Point("d4"), new Point("d5"))); // Pawn d5 ready
        game.update(new Action(Colour.BLACK, new Point("c7"), new Point("c5"))); // Pawn c5 can get en passant
        // When
        Point pawn = new Point("d5");
        Point target = new Point("c6");
        game.update(new Action(Colour.WHITE, pawn, target)); // En Passant
        // Then
        Board<Coordinate> updatedBoard = game.context().getBoard();
        assertNull(updatedBoard.get(pawn));
        assertNull(updatedBoard.get(target.translate(1, Direction.BACK)));
        assertNotNull(updatedBoard.get(target));
    }

    // endregion
    // region In Progress Game
    @Test
    void executeAction_kingH8PieceCanMove_gameIsInProgress() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.inProgressPieceCanMove, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("g4")));
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCanCapture_gameIsInProgress() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.inProgressPieceCanCapture, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKingsAndAdditionalPiece_gameIsInProgress() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.inProgressNotOnlyKings, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d2")));
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Stalemate Game
    @Test
    void executeAction_kingH8PieceCannotMove_gameIsStalemate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.stalematePieceCannotMove, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("g4")));
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCannotMove_gameIsStalemate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.stalematePieceCannotCapture, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKings_gameIsStalemate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.stalemateOnlyKings, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("e1"), new Point("e2")));
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Check in Game
    @Test
    void executeAction_kingD8PieceCanCapture_gameHasCheck() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkPieceCanCapture, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCanBlock_gameHasCheck() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkPieceCanBlock, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d8")));
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCanMove_gameHasCheck() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkKingCanMove, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Checkmate in Game
    @Test
    void executeAction_kingD8PieceCannotCapture_gameHasCheckmate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkmatePieceCannotCapture, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCannotBlock_gameHasCheckmate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkmatePieceCannotBlock, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d8")));
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCannotMove_gameHasCheckmate() {
        // Given
        GameSaveData saveData = new GameSaveData(BoardTestCases.checkmateKingCannotMove, null, null, null);
        Game game = new ChessGame(new GameOptions(), saveData);
        // When
        GameStatus status = game.update(new Action(Colour.WHITE, new Point("d1"), new Point("d7")));
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }
    // endregion

}
