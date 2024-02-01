package com.chess.api.game;

import com.chess.api.game.exception.IllegalActionException;
import com.chess.api.game.movement.Action;
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
        Vector2D pieceC = new Vector2D(pieceX, pieceY); // Nothing at location
        Vector2D nextC = new Vector2D(nextX, nextY);
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
        Vector2D pieceC = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D nextC = new Vector2D(nextX, nextY);
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
        Vector2D pieceC = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D invalid = new Vector2D(nextX, nextY);
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
        Vector2D source = new Vector2D(pieceX, pieceY); // White Knight
        Vector2D target = new Vector2D(nextX, nextY); // White Pawn
        Game game = new Game();
        Board board = game.getBoard();
        board.movePiece(new Vector2D(nextX, 1), new Vector2D(nextX, nextY)); // Filler
        board.movePiece(new Vector2D(0, 6), new Vector2D(0, 5)); // Filler

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
        Vector2D source = new Vector2D(pieceX, pieceY); // White Rook
        Vector2D target = new Vector2D(nextX, nextY); // Black Pawn
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
        Vector2D source = new Vector2D(pieceX, pieceY); // White Rook
        Vector2D target = new Vector2D(nextX, nextY); // Black Pawn
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Vector2D(0, 1), null); // Can be sufficient for path checks

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
        Vector2D source = new Vector2D(pieceX, pieceY); // White Bishop
        Vector2D target = new Vector2D(nextX, nextY); // Empty
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
        Vector2D source = new Vector2D(pieceX, pieceY); // White Bishop
        Vector2D target = new Vector2D(nextX, nextY); // Empty
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Vector2D(3, 1), null); // Clearing the path for a Bishop's move

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
        Vector2D source = new Vector2D(4, 0);
        Vector2D target = new Vector2D(6, 0);
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Vector2D(5, 0), null);
        board.setPiece(new Vector2D(6, 0), null);

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
        Vector2D source = new Vector2D(4, 0);
        Vector2D target = new Vector2D(2, 0);
        Game game = new Game();
        Board board = game.getBoard();
        board.setPiece(new Vector2D(1, 0), null);
        board.setPiece(new Vector2D(2, 0), null);
        board.setPiece(new Vector2D(3, 0), null);

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
        Vector2D source = new Vector2D(4, 6);
        Vector2D target = new Vector2D(4, 4);
        Game game = new Game();
        Board board = game.getBoard();
        // White move
        board.movePiece(new Vector2D(3, 1), new Vector2D(3, 3));
        // Black move (filler)
        board.movePiece(new Vector2D(1, 6), new Vector2D(1, 5));
        // White move
        board.movePiece(new Vector2D(3, 3), new Vector2D(3, 4));
        // Black move
        board.movePiece(source, target);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Vector2D(3, 4), new Vector2D(4, 5));
        game.executeAction(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNull(board.getPiece(4, 4));
        assertNotNull(board.getPiece(4, 5));
    }

    @Test
    void executeAction_pawnEnPassantLeftAndValid_pawnMovedAndOtherRemoved() {
        // Given
        Vector2D source = new Vector2D(2, 6);
        Vector2D target = new Vector2D(2, 4);
        Game game = new Game();
        Board board = game.getBoard();
        // White move
        board.movePiece(new Vector2D(3, 1), new Vector2D(3, 3));
        // Black move (filler)
        board.movePiece(new Vector2D(1, 6), new Vector2D(1, 5));
        // White move
        board.movePiece(new Vector2D(3, 3), new Vector2D(3, 4));
        // Black move
        board.movePiece(source, target);

        // When (White move)
        Action action = new Action(Colour.WHITE, new Vector2D(3, 4), new Vector2D(2, 5));
        game.executeAction(action); // En Passant

        // Then
        assertNull(board.getPiece(3, 4));
        assertNull(board.getPiece(2, 4));
        assertNotNull(board.getPiece(2, 5));
    }


}
