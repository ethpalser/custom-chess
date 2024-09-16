package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardTestCases;
import com.ethpalser.chess.board.BoardType;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.view.GameView;
import com.google.gson.Gson;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ChessGameTest {

    @Test
    void testToJson_givenNewStandardBoard_thenHas32PiecesAndEmptyLogAndNoCustomPieces() {
        // given
        Board board = new ChessBoard(BoardType.STANDARD);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        // when
        String jsonString = game.toJson();

        // then
        assertTrue(jsonString.contains("turn"));
        assertTrue(jsonString.contains("log"));
        assertTrue(jsonString.contains("board"));
        assertTrue(jsonString.contains("pieces"));
        assertTrue(jsonString.contains("width"));
        assertTrue(jsonString.contains("length"));
        assertTrue(jsonString.contains("pieceSpecs"));

        Gson gson = new Gson();
        GameView root = gson.fromJson(jsonString, GameView.class);
        assertEquals(32, root.getBoard().getPieces().size());
        assertTrue(root.getPieceSpecs().isEmpty());
        assertTrue(root.getLog().isEmpty());
    }

    @Test
    void testToJson_givenNewCustomBoard_thenHas32PiecesAndEmptyLogAndNoCustomPieces() {
        // given
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        // when
        String jsonString = game.toJson();

        // then
        assertTrue(jsonString.contains("turn"));
        assertTrue(jsonString.contains("log"));
        assertTrue(jsonString.contains("board"));
        assertTrue(jsonString.contains("pieces"));
        assertTrue(jsonString.contains("width"));
        assertTrue(jsonString.contains("length"));
        assertTrue(jsonString.contains("pieceSpecs"));

        Gson gson = new Gson();
        GameView root = gson.fromJson(jsonString, GameView.class);
        assertEquals(32, root.getBoard().getPieces().size());
        assertTrue(root.getPieceSpecs().isEmpty());
        assertTrue(root.getLog().isEmpty());
    }

    @Test
    void testFromJsonConstructor_givenSaveGameView_thenMatchesOriginal() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        // when
        String jsonString = game.toJson();
        Gson gson = new Gson();
        GameView root = gson.fromJson(jsonString, GameView.class);
        assertEquals(32, root.getBoard().getPieces().size());

        // then
        Game copy = new ChessGame(root);
        for (Piece p : copy.getBoard().getPieces()) {
            assertNotNull(game.getBoard().getPiece(p.getPoint()));
            assertEquals(game.getBoard().getPiece(p.getPoint()).getCode(), p.getCode());
        }
    }

    @Test
    void testPotentialUpdates_givenPieceCaptured_thenCapturedNotInUpdates() {

        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        GameStatus s1 = game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.updateGame(new Action(Colour.BLACK, new Point("g8"), new Point("f6")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("c3")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.updateGame(new Action(Colour.BLACK, new Point("f6"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s4);
        GameStatus s5 = game.updateGame(new Action(Colour.WHITE, new Point("c3"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s5);

        Iterable<Action> blackActions = game.potentialUpdates();
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board.getPieces(), log);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board.getPieces(), log, whiteThreats);

        for (Action action : blackActions) {
            Piece piece = board.getPiece(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board.getPieces(), log, whiteThreats, action.getEnd()));
        }

        // Checking that a bug does not occur
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("f6")));
        game.undoUpdate(2, false);

        Iterable<Action> blackActions2 = game.potentialUpdates();
        ThreatMap whiteThreats2 = new ThreatMap(Colour.WHITE, board.getPieces(), log);
        MoveMap blackMoves2 = new MoveMap(Colour.BLACK, board.getPieces(), log, whiteThreats);

        for (Action action : blackActions2) {
            Piece piece = board.getPiece(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves2.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board.getPieces(), log, whiteThreats2, action.getEnd()));
        }
        game.undoUpdate(1, false);
    }

    @Test
    void testPotentialUpdates_givenProgressedQueens_thenKingCannotMoveToThreatenedSpace() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("g4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d8"), new Point("d6")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("e5")));

        Iterable<Action> blackActions = game.potentialUpdates();
        // These actions are for black, so only the white threat map is needed
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board.getPieces(), log);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board.getPieces(), log, whiteThreats);

        for (Action action : blackActions) {
            Piece piece = board.getPiece(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board.getPieces(), log, whiteThreats, action.getEnd()));
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheck_thenNonBlockingMovesCauseNoChange() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        GameStatus s1 = game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.BLACK_IN_CHECK, s3);
        GameStatus s4 = game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.NO_CHANGE, s4);

        Iterable<Action> blackActions = game.potentialUpdates();
        for (Action action : blackActions) {
            GameStatus result = game.updateGame(action);
            if (result == GameStatus.NO_CHANGE) {
                fail("available actions must prevent check");
            }
            game.undoUpdate(1, true);
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheckmate_thenNoPotentialMoves() {

        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        GameStatus s1 = game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.updateGame(new Action(Colour.WHITE, new Point("b1"), new Point("c3")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.ONGOING, s4);
        GameStatus s5 = game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.WHITE_WIN, s5);
        GameStatus s6 = game.updateGame(new Action(Colour.BLACK, new Point("g5"), new Point("g4")));
        assertEquals(GameStatus.WHITE_WIN, s6);

        Iterable<Action> blackActions = game.potentialUpdates();
        // These actions are for black, so only the white threat map is needed
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board.getPieces(), log);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board.getPieces(), log, whiteThreats);

        for (Action action : blackActions) {
            fail("actions must be empty");
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheckFromAdjacentPiece_thenKingCanCapture() {

        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        GameStatus s1 = game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        assertEquals(GameStatus.ONGOING, s1);
        GameStatus s2 = game.updateGame(new Action(Colour.BLACK, new Point("g7"), new Point("g5")));
        assertEquals(GameStatus.ONGOING, s2);
        GameStatus s3 = game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("h5")));
        assertEquals(GameStatus.ONGOING, s3);
        GameStatus s4 = game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e5")));
        assertEquals(GameStatus.ONGOING, s4);
        GameStatus s5 = game.updateGame(new Action(Colour.WHITE, new Point("h5"), new Point("f7")));
        assertEquals(GameStatus.BLACK_IN_CHECK, s5);

        Iterable<Action> blackActions = game.potentialUpdates();
        for (Action action : blackActions) {
            GameStatus result = game.updateGame(action);
            if (result == GameStatus.NO_CHANGE) {
                fail("available actions must prevent check");
            }
            game.undoUpdate(1, true);
        }

        // Checking that a bug does not occur
        GameStatus afterUndoG5F7 = game.undoUpdate(1, true);
        assertEquals(GameStatus.ONGOING, afterUndoG5F7);
        GameStatus afterUndoE7E5 = game.undoUpdate(1, true);
        assertEquals(GameStatus.ONGOING, afterUndoE7E5);
        GameStatus s6 = game.updateGame(new Action(Colour.BLACK, new Point("f7"), new Point("f5")));
        assertEquals(GameStatus.NO_CHANGE, s6);
    }

    @Test
    void testEvaluateState_givenStartingBoard_thenZeroForBothPlayers() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenEdgePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e3")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d6")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnThreatenCenterForBothPlayers_thenZeroForBothPlayers() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenWhiteCapturePawn_thenPositiveState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("d5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenBlackCapturePawn_thenNegativeState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d5"), new Point("e4")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }


    @Test
    void testEvaluateState_givenWhiteControlCenter_thenPositiveState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("d2"), new Point("d4")));
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h5")));

        int value = game.evaluateState();
        assertTrue(value > 0);
    }


    @Test
    void testEvaluateState_givenBlackControlCenter_thenNegativeState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("h2"), new Point("h4")));
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e5")));

        int value = game.evaluateState();
        assertTrue(value < 0);
    }

    @Test
    void testEvaluateState_givenWhitePawnChain_thenPositiveState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.updateGame(new Action(Colour.WHITE, new Point("b2"), new Point("b3")));
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h5")));

        int value = game.evaluateState();
        assertTrue(value > 0);
    }


    @Test
    void testEvaluateState_givenBlackPawnChain_thenNegativeState() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));
        game.updateGame(new Action(Colour.WHITE, new Point("h2"), new Point("h4")));
        game.updateGame(new Action(Colour.BLACK, new Point("b7"), new Point("b6")));

        int value = game.evaluateState();
        assertTrue(value < 0);
    }

    @Test
    void testBotMovement_givenStartingBoard_thenBoardChanges() {
        Board board = new ChessBoard(BoardType.CUSTOM);
        Log<Point, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);
        GameTree tree = new GameTree(game);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6")));
        // When
        Action botBest = tree.nextBest(4);
        game.updateGame(botBest);

        // Then
        assertNull(game.getBoard().getPiece(botBest.getStart()));
        assertNotNull(game.getBoard().getPiece(botBest.getEnd()));
    }

    // region Piece Movement
    @Test
    void executeAction_noPieceAtCoordinate_throwsIllegalActionException() {
        // Given
        int pieceX = 2;
        int pieceY = 2;
        int nextX = 4;
        int nextY = 3;
        Point pieceC = new Point(pieceX, pieceY); // Nothing at location
        Point nextC = new Point(nextX, nextY);
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        Action action = new Action(Colour.WHITE, pieceC, nextC);

        // When
        GameStatus status = game.updateGame(action);
        assertEquals(GameStatus.NO_CHANGE, status);

        // Then
        assertNull(board.getPiece(pieceX, pieceY));
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toSameCoordinate_throwsIllegalActionException() {
        // Given
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 1;
        int nextY = 0;
        Point pieceC = new Point(pieceX, pieceY); // White Knight
        Point nextC = new Point(nextX, nextY);
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, pieceC, nextC);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toInvalidCoordinate_throwsIndexOutOfBoundsException() {
        // Given
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 0;
        int nextY = -2;
        Point pieceC = new Point(pieceX, pieceY); // White Knight
        Point invalid = new Point(nextX, nextY);
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, pieceC, invalid);
        GameStatus status = game.updateGame(action);
        assertEquals(GameStatus.NO_CHANGE, status);

        // Then
        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toValidSameColourOccupiedCoordinate_throwsIllegalActionException() {
        // Given
        int pieceX = 1;
        int pieceY = 0;
        int nextX = 2;
        int nextY = 2;
        Point source = new Point(pieceX, pieceY); // White Knight
        Point target = new Point(nextX, nextY); // White Pawn
        Board board = new ChessBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        board.movePiece(new Point(nextX, 1), new Point(nextX, nextY), log, threatMap); // Filler
        board.movePiece(new Point(0, 6), new Point(0, 5), log, threatMap); // Filler

        ChessGame game = new ChessGame(board, log);

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toValidOppositeColourOccupiedCoordinatePathBlocked_throwsIllegalActionException() {
        // Given
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;
        Point source = new Point(pieceX, pieceY); // White Rook
        Point target = new Point(nextX, nextY); // Black Pawn
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.BLACK, board.getPiece(target).getColour());
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toValidOppositeColourOccupiedCoordinatePathOpen_pieceMovedAndOneFewerPieces() {
        // Given
        int pieceX = 0;
        int pieceY = 0;
        int nextX = 0;
        int nextY = 6;
        Point source = new Point(pieceX, pieceY); // White Rook
        Point target = new Point(nextX, nextY); // Black Pawn
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());
        board.addPiece(new Point(0, 1), null); // Can be sufficient for path checks

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(30, board.getPieces().size()); // Two fewer pieces due to forced removal and capture
    }

    @Test
    void executeAction_toValidEmptyCoordinatePathBlocked_throwsIllegalActionException() {
        // Given
        int pieceX = 2;
        int pieceY = 0;
        int nextX = 4;
        int nextY = 2;
        Point source = new Point(pieceX, pieceY); // White Bishop
        Point target = new Point(nextX, nextY); // Empty
        Board board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.getPiece(source));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertNull(board.getPiece(target));
        assertEquals(32, board.getPieces().size());
    }

    @Test
    void executeAction_toValidEmptyCoordinatePathOpen_pieceMovedAndNoFewerPieces() {
        // Given
        int pieceX = 2;
        int pieceY = 0;
        int nextX = 4;
        int nextY = 2;
        Point source = new Point(pieceX, pieceY); // White Bishop
        Point target = new Point(nextX, nextY); // Empty
        Board board = new ChessBoard();
        board.addPiece(new Point(3, 1), null); // Clearing the path for a Bishop's move

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(31, board.getPieces().size()); // One fewer piece from forced removal
    }

    @Test
    void executeAction_castleKingSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Point source = new Point(4, 0);
        Point target = new Point(6, 0);
        Board board = new ChessBoard();
        board.addPiece(new Point(5, 0), null);
        board.addPiece(new Point(6, 0), null);

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.getPiece(4, 0));
        assertNull(board.getPiece(7, 0));
        assertNotNull(board.getPiece(target));
        assertNotNull(board.getPiece(5, 0));
    }


    @Test
    void executeAction_castleQueenSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Point source = new Point(4, 0);
        Point target = new Point(2, 0);
        Board board = new ChessBoard();
        board.addPiece(new Point(1, 0), null);
        board.addPiece(new Point(2, 0), null);
        board.addPiece(new Point(3, 0), null);


        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.getPiece(4, 0));
        assertNull(board.getPiece(0, 0));
        assertNotNull(board.getPiece(target));
        assertNotNull(board.getPiece(3, 0));
    }

    @Test
    void executeAction_pawnEnPassantRightAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Point source = new Point(4, 6);
        Point target = new Point(4, 4);
        Board board = new ChessBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        LogEntry<Point, Piece> entry1 = new ChessLogEntry(new Point(3, 3), new Point(3, 4),
                board.getPiece(new Point(3, 3)));
        board.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        log.push(entry1);
        // Black move (with log updated for piece to check)
        LogEntry<Point, Piece> entry2 = new ChessLogEntry(source, target, board.getPiece(source));
        board.movePiece(source, target, log, threatMap);
        log.push(entry2);

        ChessGame game = new ChessGame(board, log);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(4, 5));

        game.updateGame(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNotNull(board.getPiece(4, 5));
        assertNull(board.getPiece(4, 4));
    }

    @Test
    void executeAction_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Point source = new Point(2, 6);
        Point target = new Point(2, 4);
        Board board = new ChessBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        LogEntry<Point, Piece> entry1 = new ChessLogEntry(new Point(3, 3), new Point(3, 4),
                board.getPiece(new Point(3, 3)));
        board.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        log.push(entry1);
        // Black move
        LogEntry<Point, Piece> entry2 = new ChessLogEntry(source, target, board.getPiece(source));
        board.movePiece(source, target, log, threatMap);
        log.push(entry2);

        ChessGame game = new ChessGame(board, log);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(2, 5));
        game.updateGame(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNotNull(board.getPiece(2, 5));
        assertNull(board.getPiece(2, 4));
    }

    // endregion
    // region In Progress Game
    @Test
    void executeAction_kingH8PieceCanMove_gameIsInProgress() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.inProgressPieceCanMove);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCanCapture_gameIsInProgress() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.inProgressPieceCanCapture);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKingsAndAdditionalPiece_gameIsInProgress() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.inProgressNotOnlyKings);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '2'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Stalemate Game
    @Test
    void executeAction_kingH8PieceCannotMove_gameIsStalemate() {
        // Given
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.stalematePieceCannotMove);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCannotMove_gameIsStalemate() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.stalematePieceCannotCapture);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKings_gameIsStalemate() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.stalemateOnlyKings);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('e', '1'), new Point('e', '2'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Check in Game
    @Test
    void executeAction_kingD8PieceCanCapture_gameHasCheck() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkPieceCanCapture);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCanBlock_gameHasCheck() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkPieceCanBlock);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCanMove_gameHasCheck() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkKingCanMove);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    // endregion
    // region Checkmate in Game
    @Test
    void executeAction_kingD8PieceCannotCapture_gameHasCheckmate() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkmatePieceCannotCapture);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCannotBlock_gameHasCheckmate() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkmatePieceCannotBlock);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCannotMove_gameHasCheckmate() {
        Log<Point, Piece> log = new ChessLog();
        Board board = new ChessBoard(BoardType.CUSTOM, log, BoardTestCases.checkmateKingCannotMove);
        ChessGame game = new ChessGame(board, log);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }
    // endregion

}
