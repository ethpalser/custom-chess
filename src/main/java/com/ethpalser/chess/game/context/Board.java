package com.ethpalser.chess.game.context;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Space;
import java.util.Collection;

/**
 * An interface for a Data Structure containing Pieces stored at Coordinates.
 */
@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 3,
        minorVersion = 2,
        lastModified = "2025-01-13"
)
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
