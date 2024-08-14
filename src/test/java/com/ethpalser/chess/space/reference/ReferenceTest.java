package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.StandardBoard;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReferenceTest {

    @Test
    void absoluteRef_getReferences_givenLocationEmpty_thenIsEmpty() {
        // Given
        Board board = new StandardBoard();
        Reference<Piece> reference = new AbsoluteReference<>(new Point(4, 3));
        // Then
        assertTrue(reference.getReferences(board.getPieces()).isEmpty());
    }

    @Test
    void absoluteRef_getReferences_givenLocationNotEmpty_thenHasPiece() {
        // Given
        Board board = new StandardBoard();
        Reference<Piece> reference = new AbsoluteReference<>(new Point(0, 0));
        // Then
        assertFalse(reference.getReferences(board.getPieces()).isEmpty());
    }

    @Test
    void absoluteRef_getReferences_givenPieceMovedOntoLocation_thenHasPiece() {
        // Given
        Board board = new StandardBoard();
        Reference<Piece> reference = new AbsoluteReference<>(new Point(4, 3));
        board.movePiece(new Point(4, 1), new Point(4, 3));
        // Then
        assertFalse(reference.getReferences(board.getPieces()).isEmpty());
        assertTrue(reference.getReferences(board.getPieces()).contains(board.getPiece(4, 3)));
    }

    @Test
    void pieceRef_getReferences_givenAtLocationAndNotMoved_thenIsItself() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece piece = board.getPiece(4, 1); // e1 pawn
        // When
        PieceReference ref = new PieceReference(piece);
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(piece));
    }

    @Test
    void pieceRef_getReferences_givenAtLocationAndMoved_thenIsItself() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece piece = board.getPiece(4, 1); // e1 pawn
        // When
        PieceReference ref = new PieceReference(piece, Direction.AT);
        board.movePiece(new Point(4, 1), new Point(4, 2));
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(piece));
    }

    void pieceRef_getReferences_givenBackOfLocationAndMovedUpOne_thenIsEmpty() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece piece = board.getPiece(4, 1); // e1 pawn
        // When
        PieceReference ref = new PieceReference(piece, Direction.BACK);
        board.movePiece(new Point(4, 1), new Point(4, 2));
        // Then
        assertTrue(ref.getReferences(board.getPieces()).isEmpty());
    }

    @Test
    void pieceRef_getReferences_givenRightOfLocationAndPawnToRight_thenIsPawn() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece refPiece = board.getPiece(4, 1); // e1 pawn
        Piece rightPiece = board.getPiece(5, 1); // f1 pawn
        // When
        PieceReference ref = new PieceReference(refPiece, Direction.RIGHT);
        board.movePiece(new Point(4, 1), new Point(4, 2));
        board.movePiece(new Point(5, 1), new Point(5, 2));
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(rightPiece));
    }

    @Test
    void pieceRef_getReferences_givenLeftFourOfKingAtStart_thenIsRook() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece refPiece = board.getPiece(4, 0); // d0 king
        Piece expected = board.getPiece(0, 0); // a0 rook
        // When
        PieceReference ref = new PieceReference(refPiece, Direction.LEFT, 4);
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(expected));
    }

    @Test
    void pieceRef_getReferences_givenOutOfBounds_thenIsEmpty() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece refPiece = board.getPiece(4, 0); // d0 king
        // When
        PieceReference ref = new PieceReference(refPiece, Direction.BACK, 2);
        // Then
        assertTrue(ref.getReferences(board.getPieces()).isEmpty());
    }
}
