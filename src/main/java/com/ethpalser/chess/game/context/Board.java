package com.ethpalser.chess.game.context;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Space;
import java.util.Collection;

public interface Board<K extends Coordinate> extends Iterable<Piece> {

    Piece get(K point);

    void add(K point, Piece piece);

    void remove(K point);

    int count();

    Space space();

    default boolean rejects(K point) {
        return false;
    }

    Collection<Coordinate> occupied();

}
