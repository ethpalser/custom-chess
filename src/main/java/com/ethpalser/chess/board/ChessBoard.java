package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import java.util.Set;

public interface ChessBoard {

    Plane<ChessPiece> getPieces();

    ChessPiece getPiece(Point point);

    void addPiece(Point point, ChessPiece piece);

    void movePiece(Point start, Point end);

    Set<ChessPiece> getThreats(Point point, Colour colour);

    default boolean hasThreats(Point point, Colour colour) {
        if (point == null) {
            return false;
        }
        return !getThreats(point, colour).isEmpty();
    }

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Point point) {
        return this.isInBounds(point.getX(), point.getY());
    }

    /**
     * Updates the boards internal state for legal moves, threats and any other state-dependent information.
     * This method is only needed if the state is modified directly. This may already be used implicitly whenever
     * a piece is moved, or a more optimized version of this.
     */
    void refresh();

}
