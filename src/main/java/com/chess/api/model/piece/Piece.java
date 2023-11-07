package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;

public interface Piece {

    Colour getColour();

    Coordinate getCoordinate();

    boolean isMoved();

}
