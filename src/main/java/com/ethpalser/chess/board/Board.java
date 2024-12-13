package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public interface Board {

    Plane<Piece> getPieces();

    Piece getPiece(Point point);

    void addPiece(Point point, Piece piece);

    boolean isInBounds(Point point);

}
