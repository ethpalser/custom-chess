package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.BoardTestCases;
import com.ethpalser.chess.board.Point;
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
        Game game = new Game();

        // When
        Action action = new Action(Colour.WHITE, pieceC, nextC);
        assertThrows(IllegalActionException.class, () -> game.executeAction(action));

        // Then
        Board board = game.getBoard();
        assertNull(board.getPiece(pieceX, pieceY));
        assertEquals(32, board.count());
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
        Game game = new Game();

        // When
        Action action = new Action(Colour.WHITE, pieceC, nextC);
        assertThrows(IllegalActionException.class, () -> game.executeAction(action));

        // Then
        Board board = game.getBoard();
        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.count());
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
        Game game = new Game();

        // When
        Action action = new Action(Colour.WHITE, pieceC, invalid);
        assertThrows(IndexOutOfBoundsException.class, () -> game.executeAction(action));

        // Then
        Board board = game.getBoard();
        assertEquals(Colour.WHITE, board.getPiece(pieceX, pieceY).getColour());
        assertEquals(32, board.count());
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
        Game game = new Game();
        Board board = game.getBoard();
        board.movePiece(new Point(nextX, 1), new Point(nextX, nextY)); // Filler
        board.movePiece(new Point(0, 6), new Point(0, 5)); // Filler

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.executeAction(action));

        // Then
        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
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
        Game game = new Game();

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.executeAction(action));

        // Then
        Board board = game.getBoard();
        assertNotNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertEquals(Colour.BLACK, board.getPiece(target).getColour());
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
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Point(0, 1), null); // Can be sufficient for path checks

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.executeAction(action);

        // Then
        assertNull(board.getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
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
        Game game = new Game();

        // When
        Action action = new Action(Colour.WHITE, source, target);
        assertThrows(IllegalActionException.class, () -> game.executeAction(action));

        // Then
        Board board = game.getBoard();
        assertNotNull(board.getPiece(source));
        assertEquals(Colour.WHITE, board.getPiece(source).getColour());
        assertNull(board.getPiece(target));
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
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Point(3, 1), null); // Clearing the path for a Bishop's move

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.executeAction(action);

        // Then
        assertNull(game.getBoard().getPiece(source));
        assertNotNull(board.getPiece(target));
        assertEquals(Colour.WHITE, board.getPiece(target).getColour());
        assertEquals(31, board.count()); // One fewer piece from forced removal
    }

    @Test
    void executeAction_castleKingSideAndValid_kingAndRookMovedAndNoFewerPieces() {
        // Given
        Point source = new Point(4, 0);
        Point target = new Point(6, 0);
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Point(5, 0), null);
        board.setPiece(new Point(6, 0), null);

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.executeAction(action);

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
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Point(1, 0), null);
        board.setPiece(new Point(2, 0), null);
        board.setPiece(new Point(3, 0), null);

        // When
        Action action = new Action(Colour.WHITE, source, target);
        game.executeAction(action);

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
        Game game = new Game();
        Board board = game.getBoard();
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3));
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5));
        // White move
        board.movePiece(new Point(3, 3), new Point(3, 4));
        // Black move
        board.movePiece(source, target);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(4, 5));
        game.executeAction(action); // En Passant

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
        Game game = new Game();
        Board board = game.getBoard();
        // White move
        board.movePiece(new Point(3, 1), new Point(3, 3));
        // Black move (filler)
        board.movePiece(new Point(1, 6), new Point(1, 5));
        // White move
        board.movePiece(new Point(3, 3), new Point(3, 4));
        // Black move
        board.movePiece(source, target);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Point(3, 4), new Point(2, 5));
        game.executeAction(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNull(board.getPiece(2, 4));
        assertNotNull(board.getPiece(2, 5));
    }

    // region In Progress Game
    @Test
    void executeAction_kingH8PieceCanMove_gameIsInProgress() {
        Board board = new Board(BoardTestCases.inProgressPieceCanMove);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
    }

    @Test
    void executeAction_kingF6PieceCanCapture_gameIsInProgress() {
        Board board = new Board(BoardTestCases.inProgressPieceCanCapture);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
    }

    @Test
    void executeAction_onlyKingsAndAdditionalPiece_gameIsInProgress() {
        Board board = new Board(BoardTestCases.inProgressNotOnlyKings);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '2'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
    }

    // endregion
    // region Stalemate Game
    @Test
    void executeAction_kingH8PieceCannotMove_gameIsStalemate() {
        // Given
        Board board = new Board(BoardTestCases.stalematePieceCannotMove);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('g', '4'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertTrue(game.isComplete());
    }

    @Test
    void executeAction_kingF6PieceCannotMove_gameIsStalemate() {
        Board board = new Board(BoardTestCases.stalematePieceCannotCapture);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertTrue(game.isComplete());
    }

    @Test
    void executeAction_onlyKings_gameIsStalemate() {
        Board board = new Board(BoardTestCases.stalemateOnlyKings);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('e', '1'), new Point('e', '2'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertTrue(game.isComplete());
    }

    // endregion
    // region Check in Game
    @Test
    void executeAction_kingD8PieceCanCapture_gameHasCheck() {
        Board board = new Board(BoardTestCases.checkPieceCanCapture);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }

    @Test
    void executeAction_kingG8PieceCanBlock_gameHasCheck() {
        Board board = new Board(BoardTestCases.checkPieceCanBlock);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }

    @Test
    void executeAction_kingG7KingCanMove_gameHasCheck() {
        Board board = new Board(BoardTestCases.checkKingCanMove);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertNull(game.getWinner());
        assertFalse(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }

    // endregion
    // region Checkmate in Game
    @Test
    void executeAction_kingD8PieceCannotCapture_gameHasCheckmate() {
        Board board = new Board(BoardTestCases.checkmatePieceCannotCapture);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertEquals(Colour.WHITE, game.getWinner());
        assertTrue(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }

    @Test
    void executeAction_kingG8PieceCannotBlock_gameHasCheckmate() {
        Board board = new Board(BoardTestCases.checkmatePieceCannotBlock);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '8'));
        game.executeAction(action);
        // Then
        assertEquals(Colour.WHITE, game.getWinner());
        assertTrue(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }

    @Test
    void executeAction_kingG7KingCannotMove_gameHasCheckmate() {
        Board board = new Board(BoardTestCases.checkmateKingCannotMove);
        Game game = new Game(board, Colour.WHITE);
        // When
        Action action = new Action(Colour.WHITE, new Point('d', '1'), new Point('d', '7'));
        game.executeAction(action);
        // Then
        assertEquals(Colour.WHITE, game.getWinner());
        assertTrue(game.isComplete());
        assertTrue(game.getBoard().getKingCheck(Colour.BLACK));
    }
    // endregion

}
