package com.ethpalser.chess.board;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;

public interface Board<K extends Coordinate> extends Iterable<Piece> {

    Piece get(K point);

    void add(K point, Piece piece);

    void remove(K point);

    int count();

    default boolean rejects(K point) {
        return false;
    }

}
