package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.board.StandardBoard;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ReferenceTest {

    @Test
    void pieceRef_getReference_givenAtLocationAndNotMoved_thenIsItself() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece piece = board.getPiece(4, 1); // e1 pawn
        // When
        PieceReference ref = new PieceReference(piece, Direction.AT);
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(piece));
    }

    @Test
    void pieceRef_getReference_givenAtLocationAndMoved_thenIsItself() {
        StandardBoard board = new StandardBoard();
        // Given
        Piece piece = board.getPiece(4, 1); // e1 pawn
        // When
        PieceReference ref = new PieceReference(piece, Direction.AT);
        board.movePiece(new Point(4, 1), new Point(4, 2));
        // Then
        assertTrue(ref.getReferences(board.getPieces()).contains(piece));
    }

    void pieceRef_getReference_givenBackOfLocationAndMovedUpOne_thenIsNothing() {

    }

    void pieceRef_getReference_givenRightOfLocationAndPawnToRight_thenIsPawn() {

    }

    void pieceRef_getReference_givenLeftThreeOfLocationAndRookToLeftThree_thenIsRook() {

    }

}
