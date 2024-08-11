package com.ethpalser.chess.move;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Point;
import java.util.Set;

public interface MoveMap extends Quantifiable<Integer> {

    Colour getColour();

    Set<ChessPiece> getPieces(Point point);

    default boolean isEmpty(Point point) {
        return this.getPieces(point).isEmpty();
    }

    /**
     * Remove the given piece from all sets it exists in, so it cannot be retrieved from any point.
     *
     * @param piece
     */
    void clearMoves(ChessPiece piece);

    /**
     * Remove the given piece for only the set at this point, so it cannot be retrieved from this point.
     *
     * @param piece
     * @param point
     */
    void clearMoves(ChessPiece piece, Point point);

    /**
     * Update all possible moves pieces can make given the start and end points, which were performed by a piece.
     * This piece may have already moved, and in this case the start is null and end is this piece. Additionally,
     * this case does not make the captured piece known, and must be cleared separately. Otherwise, this assumes
     * that the start moving to the end and this will clear both and update all pieces involved.
     *
     * @param board
     * @param log
     * @param point
     */
    void updateMoves(ChessBoard board, ChessLog log, Point point);

}
