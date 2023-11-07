package com.chess.api.model.piece;

import com.chess.api.model.Coordinate;

public interface Piece {

    Coordinate getCoordinate();

    boolean isMoved();

}
