package com.ethpalser.chess.piece;

import com.ethpalser.chess.space.Coordinate;

public interface PieceFactory {

    Piece create(String code, Colour colour, Coordinate coordinate);

}
