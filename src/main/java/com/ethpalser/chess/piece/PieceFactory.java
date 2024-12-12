package com.ethpalser.chess.piece;

public interface PieceFactory {

    Piece create(Colour colour, String code);

}
