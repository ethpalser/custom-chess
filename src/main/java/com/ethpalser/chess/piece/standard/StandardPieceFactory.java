package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

public class StandardPieceFactory implements PieceFactory {
    @Override
    public Piece create(String code, Colour colour, Coordinate coordinate) {
        return switch (PieceType.fromCode(code)) {
            case KING -> new King(colour, coordinate);
            case QUEEN -> new Queen(colour, coordinate);
            case BISHOP -> new Bishop(colour, coordinate);
            case KNIGHT -> new Knight(colour, coordinate);
            case ROOK -> new Rook(colour, coordinate);
            case PAWN -> new Pawn(colour, coordinate);
            default -> null;
        };
    }
}
