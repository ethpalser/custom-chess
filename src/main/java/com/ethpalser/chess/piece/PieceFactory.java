package com.ethpalser.chess.piece;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.space.Coordinate;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-12-12",
        lastModified = "2025-01-13"
)
public interface PieceFactory {

    Piece create(String code, Colour colour, Coordinate coordinate);

}
