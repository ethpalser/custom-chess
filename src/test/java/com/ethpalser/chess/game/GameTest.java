package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.custom.CustomBoard;
import com.ethpalser.chess.board.BoardTestCases;
import com.ethpalser.chess.board.StandardBoard;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.Colour;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void executeAction_noPieceAtCoordinate_throwsIllegalActionException() {
        // Given
        int pieceX = 2;
        int pieceY = 2;
        int nextX = 4;
        int nextY = 3;
        Point pieceC = new Point(pieceX, pieceY); // Nothing at location
        Point nextC = new Point(nextX, nextY);
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);

        Action action = new Action(Colour.WHITE, pieceC, nextC);

        // When
        assertThrows(IllegalActionException.class, () -> game.updateGame(action));

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
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);

        // When
        Action action = new Action(Colour.WHITE, pieceC, invalid);
        assertThrows(IndexOutOfBoundsException.class, () -> game.updateGame(action));

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
        Board board = new StandardBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        board.movePiece(new Point(nextX, 1), new Point(nextX, nextY), log, threatMap); // Filler
        board.movePiece(new Point(0, 6), new Point(0, 5), log, threatMap); // Filler

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);
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
        Board board = new StandardBoard();

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();
        board.addPiece(new Point(3, 1), null); // Clearing the path for a Bishop's move

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();
        board.addPiece(new Point(5, 0), null);
        board.addPiece(new Point(6, 0), null);

        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();
        board.addPiece(new Point(1, 0), null);
        board.addPiece(new Point(2, 0), null);
        board.addPiece(new Point(3, 0), null);


        ChessGame game = new ChessGame(board);

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
        Board board = new StandardBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        board.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        // Black move
        board.movePiece(source, target, log, threatMap);

        ChessGame game = new ChessGame(board);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(4, 5));
        game.updateGame(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNull(board.getPiece(4, 4));
        assertNotNull(board.getPiece(4, 5));
    }

    @Test
    void executeAction_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Point source = new Point(2, 6);
        Point target = new Point(2, 4);
        Board board = new StandardBoard();
        Log<Point, Piece> log = new ChessLog();
        ThreatMap threatMap = new ThreatMap(Colour.BLACK, board.getPieces(), log);
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3), log, threatMap);
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5), log, threatMap);
        // White move
        board.movePiece(new Point(3, 3), new Point(3, 4), log, threatMap);
        // Black move
        board.movePiece(source, target, log, threatMap);

        ChessGame game = new ChessGame(board);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(2, 5));
        game.updateGame(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNull(board.getPiece(2, 4));
        assertNotNull(board.getPiece(2, 5));
    }

    // region In Progress Game
    @Test
    void executeAction_kingH8PieceCanMove_gameIsInProgress() {
        CustomBoard board = new CustomBoard(BoardTestCases.inProgressPieceCanMove);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCanCapture_gameIsInProgress() {
        CustomBoard board = new CustomBoard(BoardTestCases.inProgressPieceCanCapture);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.ONGOING, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKingsAndAdditionalPiece_gameIsInProgress() {
        CustomBoard board = new CustomBoard(BoardTestCases.inProgressNotOnlyKings);
        ChessGame game = new ChessGame(new StandardBoard());
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
        CustomBoard board = new CustomBoard(BoardTestCases.stalematePieceCannotMove);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingF6PieceCannotMove_gameIsStalemate() {
        CustomBoard board = new CustomBoard(BoardTestCases.stalematePieceCannotCapture);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.STALEMATE, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_onlyKings_gameIsStalemate() {
        CustomBoard board = new CustomBoard(BoardTestCases.stalemateOnlyKings);
        ChessGame game = new ChessGame(new StandardBoard());
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
        CustomBoard board = new CustomBoard(BoardTestCases.checkPieceCanCapture);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCanBlock_gameHasCheck() {
        CustomBoard board = new CustomBoard(BoardTestCases.checkPieceCanBlock);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.BLACK_IN_CHECK, status);
        assertFalse(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCanMove_gameHasCheck() {
        CustomBoard board = new CustomBoard(BoardTestCases.checkKingCanMove);
        ChessGame game = new ChessGame(new StandardBoard());
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
        CustomBoard board = new CustomBoard(BoardTestCases.checkmatePieceCannotCapture);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG8PieceCannotBlock_gameHasCheckmate() {
        CustomBoard board = new CustomBoard(BoardTestCases.checkmatePieceCannotBlock);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }

    @Test
    void executeAction_kingG7KingCannotMove_gameHasCheckmate() {
        CustomBoard board = new CustomBoard(BoardTestCases.checkmateKingCannotMove);
        ChessGame game = new ChessGame(new StandardBoard());
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        GameStatus status = game.updateGame(action);
        // Then
        assertEquals(GameStatus.WHITE_WIN, status);
        assertTrue(GameStatus.isCompletedGameStatus(status));
    }
    // endregion

}
