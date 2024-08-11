package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public interface ChessBoard {

    Plane<ChessPiece> getPieces();

    ChessPiece getPiece(Point point);

    void addPiece(Point point, ChessPiece piece);

    void movePiece(Point start, Point end);

    boolean isInBounds(int x, int y);

    default boolean isInBounds(Point point) {
        return this.isInBounds(point.getX(), point.getY());
    }

}
