package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardTestCases;
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
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.standard.StandardPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.view.GameView;
import com.google.gson.Gson;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ChessGameTest {

    @Test
    void testToJson_givenNewStandardBoard_thenHas32PiecesAndEmptyLogAndNoCustomPieces() {
        // given
        Board<Coordinate> board = new ChessBoard();
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        // when
        String jsonString = game.toJson();
        Gson gson = new Gson();
        GameView root = gson.fromJson(jsonString, GameView.class);
        assertEquals(32, root.getBoard().getPieces().size());

        // then
        Game copy = new ChessGame(root);
        for (Piece p : copy.getBoard()) {
            assertNotNull(game.getBoard().get(p.getCoordinate()));
            assertEquals(game.getBoard().get(p.getCoordinate()).getCode(), p.getCode());
        }
    }

    @Test
    void testFromJson_givenSaveGameView_thenMatchesOriginal() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);
        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));

        String json = game.toJson();

        // when
        Game copy = ChessGame.fromJson(json);

        // then
        for (Piece p : copy.getBoard()) {
            assertNotNull(game.getBoard().get(p.getCoordinate()));
            assertEquals(game.getBoard().get(p.getCoordinate()).getCode(), p.getCode());
        }
        assertEquals(game.getTurn(), copy.getTurn());
    }

    @Test
    void testPotentialUpdates_givenPieceCaptured_thenCapturedNotInUpdates() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board, log, space);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board, log, whiteThreats);

        for (Action action : blackActions) {
            Piece piece = board.get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board, log, whiteThreats, action.getEnd()));
        }

        // Checking that a bug does not occur
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a6")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("f6")));
        game.undoUpdate(2, false);

        Iterable<Action> blackActions2 = game.potentialUpdates();
        ThreatMap whiteThreats2 = new ThreatMap(Colour.WHITE, board, log, space);
        MoveMap blackMoves2 = new MoveMap(Colour.BLACK, board, log, whiteThreats);

        for (Action action : blackActions2) {
            Piece piece = board.get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves2.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board, log, whiteThreats2, action.getEnd()));
        }
        game.undoUpdate(1, false);
    }

    @Test
    void testPotentialUpdates_givenProgressedQueens_thenKingCannotMoveToThreatenedSpace() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("d1"), new Point("g4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d8"), new Point("d6")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("e5")));

        Iterable<Action> blackActions = game.potentialUpdates();
        // These actions are for black, so only the white threat map is needed
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board, log, space);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board, log, whiteThreats);

        for (Action action : blackActions) {
            Piece piece = board.get(action.getStart());
            assertNotNull(piece);
            assertTrue(blackMoves.getPieces(action.getEnd()).contains(piece));
            assertTrue(piece.canMove(board, log, whiteThreats, action.getEnd()));
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheck_thenNonBlockingMovesCauseNoChange() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(space, factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        ThreatMap whiteThreats = new ThreatMap(Colour.WHITE, board, log, space);
        MoveMap blackMoves = new MoveMap(Colour.BLACK, board, log, whiteThreats);

        for (Action action : blackActions) {
            fail("actions must be empty");
        }
    }

    @Test
    void testPotentialUpdates_givenKingInCheckFromAdjacentPiece_thenKingCanCapture() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenEdgePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("a2"), new Point("a4")));
        game.updateGame(new Action(Colour.BLACK, new Point("a7"), new Point("a5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnMovedForBothPlayers_thenZeroForBothPlayers() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e3")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d6")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenCentrePawnThreatenCenterForBothPlayers_thenZeroForBothPlayers() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenWhiteCapturePawn_thenPositiveState() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("d7"), new Point("d5")));
        game.updateGame(new Action(Colour.WHITE, new Point("e4"), new Point("d5")));

        int value = game.evaluateState();
        assertEquals(0, value);
    }

    @Test
    void testEvaluateState_givenBlackCapturePawn_thenNegativeState() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
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
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);
        GameTree tree = new GameTree(game);

        game.updateGame(new Action(Colour.WHITE, new Point("e2"), new Point("e4")));
        game.updateGame(new Action(Colour.BLACK, new Point("e7"), new Point("e6")));
        // When
        Action botBest = tree.nextBest(4);
        game.updateGame(botBest);

        // Then
        assertNull(game.getBoard().get(botBest.getStart()));
        assertNotNull(game.getBoard().get(botBest.getEnd()));
    }

    @Test
    void updateGame_pawnPromotion_changesToQueen() {
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), new ChessLog(), space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory);
        Log<Coordinate, Piece> log = new ChessLog();
        Game game = new ChessGame(board, log);

        game.updateGame(new Action(Colour.WHITE, new Point("b2"), new Point("b4")));
        game.updateGame(new Action(Colour.BLACK, new Point("h7"), new Point("h6")));
        game.updateGame(new Action(Colour.WHITE, new Point("b4"), new Point("b5")));
        game.updateGame(new Action(Colour.BLACK, new Point("h6"), new Point("h5")));
        game.updateGame(new Action(Colour.WHITE, new Point("b5"), new Point("b6")));
        game.updateGame(new Action(Colour.BLACK, new Point("h5"), new Point("h4")));
        game.updateGame(new Action(Colour.WHITE, new Point("b6"), new Point("a7")));
        game.updateGame(new Action(Colour.BLACK, new Point("h4"), new Point("h3")));

        // When
        Point start = new Point("a7");
        Point end = new Point("b8");
        game.updateGame(new Action(Colour.WHITE, start, end));

        // Then
        assertNotNull(board.get(end));
        assertNotNull(log.peek().getPromotion());
        assertEquals("Q", board.get(end).getCode());

        // Testing Undo and Redo as well
        game.undoUpdate();
        assertNotNull(board.get(start));
        assertEquals("P", board.get(start).getCode());

        game.redoUpdate();
        assertNotNull(board.get(end));
        assertEquals("Q", board.get(end).getCode());
    }

    // region Piece Movement
    @Test
    void executeAction_noPieceAtCoordinate_throwsIllegalActionException() {
        // Given
        int x = 2;
        int y = 2;
        int nextX = 4;
        int nextY = 3;
        Point start = new Point(x, y); // Nothing at location
        Point next = new Point(nextX, nextY);
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        Action action = new Action(Colour.WHITE, start, next);

        // When
        GameStatus status = game.updateGame(action);
        assertEquals(GameStatus.NO_CHANGE, status);

        // Then
        assertNull(board.get(start));
        assertEquals(32, board.count());
    }

    @Test
    void executeAction_toSameCoordinate_throwsIllegalActionException() {
        // Given
        int x = 1;
        int y = 0;
        int nextX = 1;
        int nextY = 0;
        Point start = new Point(x, y); // White Knight
        Point next = new Point(nextX, nextY);
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, start, next);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertEquals(Colour.WHITE, board.get(start).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void executeAction_toInvalidCoordinate_throwsIndexOutOfBoundsException() {
        // Given
        int x = 1;
        int y = 0;
        int nextX = 0;
        int nextY = -2;
        Point start = new Point(x, y); // White Knight
        Point invalid = new Point(nextX, nextY);
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, start, invalid);
        GameStatus status = game.updateGame(action);
        assertEquals(GameStatus.NO_CHANGE, status);

        // Then
        assertEquals(Colour.WHITE, board.get(start).getColour());
        assertEquals(32, board.count());
    }

    @Test
    void executeAction_toValidSameColourOccupiedCoordinate_throwsIllegalActionException() {
        // Given
        int x = 1;
        int y = 0;
        int nextX = 2;
        int nextY = 2;
        Point source = new Point(x, y); // White Knight
        Point target = new Point(nextX, nextY); // White Pawn
        Space space = new Plane(8, 8);
        Board<Coordinate> board = new ChessBoard(space, new StandardPieceFactory());
        Log<Coordinate, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board, log, space);

        ChessGame game = new ChessGame(board, log);
        game.movePiece(new Point(nextX, 1), new Point(nextX, nextY), log, threatMap); // Filler
        game.movePiece(new Point(0, 6), new Point(0, 5), log, threatMap); // Filler

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.get(source));
        assertNotNull(board.get(target));
        assertEquals(Colour.WHITE, board.get(source).getColour());
        assertEquals(Colour.WHITE, board.get(target).getColour());
        assertEquals(32, board.count());
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
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.get(source));
        assertNotNull(board.get(target));
        assertEquals(Colour.WHITE, board.get(source).getColour());
        assertEquals(Colour.BLACK, board.get(target).getColour());
        assertEquals(32, board.count());
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
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());
        board.add(new Point(0, 1), null); // Can be sufficient for path checks

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.get(source));
        assertNotNull(board.get(target));
        assertEquals(Colour.WHITE, board.get(target).getColour());
        assertEquals(30, board.count()); // Two fewer pieces due to forced removal and capture
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
        Board<Coordinate> board = new ChessBoard();

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

        // Then
        assertNotNull(board.get(source));
        assertEquals(Colour.WHITE, board.get(source).getColour());
        assertNull(board.get(target));
        assertEquals(32, board.count());
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
        Board<Coordinate> board = new ChessBoard();
        board.add(new Point(3, 1), null); // Clearing the path for a Bishop's move

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.get(source));
        assertNotNull(board.get(target));
        assertEquals(Colour.WHITE, board.get(target).getColour());
        assertEquals(31, board.count()); // One fewer piece from forced removal
    }

    @Test
    void executeAction_castleKingSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Point source = new Point(4, 0);
        Point target = new Point(6, 0);
        Board<Coordinate> board = new ChessBoard();
        board.add(new Point(5, 0), null);
        board.add(new Point(6, 0), null);

        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.get(source));
        assertNull(board.get(target.shift(Colour.WHITE, Direction.RIGHT)));
        assertNotNull(board.get(target));
        assertNotNull(board.get(target.shift(Colour.WHITE, Direction.LEFT)));
    }


    @Test
    void executeAction_castleQueenSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Point source = new Point(4, 0);
        Point target = new Point(2, 0);
        Board<Coordinate> board = new ChessBoard();
        board.add(new Point(1, 0), null);
        board.add(new Point(2, 0), null);
        board.add(new Point(3, 0), null);


        ChessGame game = new ChessGame(board, new ChessLog());

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.updateGame(action);

        // Then
        assertNull(board.get(source));
        assertNull(board.get(target.shift(Colour.WHITE, Direction.LEFT).shift(Colour.WHITE, Direction.LEFT)));
        assertNotNull(board.get(target));
        assertNotNull(board.get(target.shift(Colour.WHITE, Direction.RIGHT)));
    }

    @Test
    void executeAction_pawnEnPassantRightAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Point source = new Point(4, 6);
        Point target = new Point(4, 4);
        Space space = new Plane(8, 8);
        Board<Coordinate> board = new ChessBoard(space, new StandardPieceFactory());
        Log<Coordinate, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board, log, space);

        ChessGame game = new ChessGame(board, log);
        // White move
        game.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        game.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        LogEntry<Coordinate, Piece> entry1 = new ChessLogEntry(new Point(3, 3), new Point(3, 4),
                board.get(new Point(3, 3)));
        game.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        log.push(entry1);
        // Black move (with log updated for piece to check)
        LogEntry<Coordinate, Piece> entry2 = new ChessLogEntry(source, target, board.get(source));
        game.movePiece(source, target, log, threatMap);
        log.push(entry2);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(4, 5));

        game.updateGame(action); // En Passant

        // Then
        assertNull(board.get(target.shift(Colour.WHITE, Direction.LEFT)));
        assertNotNull(board.get(target.shift(Colour.WHITE, Direction.FRONT)));
        assertNull(board.get(target));
    }

    @Test
    void executeAction_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Point source = new Point(2, 6);
        Point target = new Point(2, 4);
        Space space = new Plane(8, 8);
        Board<Coordinate> board = new ChessBoard(space, new StandardPieceFactory());
        Log<Coordinate, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board, log, space);

        ChessGame game = new ChessGame(board, log);
        // White move
        game.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        game.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        LogEntry<Coordinate, Piece> entry1 = new ChessLogEntry(new Point(3, 3), new Point(3, 4),
                board.get(new Point(3, 3)));
        game.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        log.push(entry1);
        // Black move
        LogEntry<Coordinate, Piece> entry2 = new ChessLogEntry(source, target, board.get(source));
        game.movePiece(source, target, log, threatMap);
        log.push(entry2);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(2, 5));
        game.updateGame(action); // En Passant

        // Then
        assertNull(board.get(target.shift(Colour.WHITE, Direction.LEFT)));
        assertNotNull(board.get(target.shift(Colour.WHITE, Direction.FRONT)));
        assertNull(board.get(target));
    }

    // endregion
    // region In Progress Game
    @Test
    void executeAction_kingH8PieceCanMove_gameIsInProgress() {
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.inProgressPieceCanMove);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.inProgressPieceCanCapture);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.inProgressNotOnlyKings);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.stalematePieceCannotMove);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.stalematePieceCannotCapture);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.stalemateOnlyKings);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(new Plane(8, 8), factory, BoardTestCases.checkPieceCanCapture);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory, BoardTestCases.checkPieceCanBlock);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory, BoardTestCases.checkKingCanMove);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory, BoardTestCases.checkmatePieceCannotCapture);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory, BoardTestCases.checkmatePieceCannotBlock);
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
        Log<Coordinate, Piece> log = new ChessLog();
        Space space = new Plane(8, 8);
        PieceFactory factory = new CustomPieceFactory(Map.of(), log, space);
        Board<Coordinate> board = new ChessBoard(space, factory, BoardTestCases.checkmateKingCannotMove);
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
