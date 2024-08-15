package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public interface Board<T extends Piece> {

    Plane<T> getPieces();

    T getPiece(Point point);

    default T getPiece(int x, int y) {
        return getPiece(new Point(x, y));
    }

    void addPiece(Point point, T piece);

    void movePiece(Point start, Point end);

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Point point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
