package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Point;

public class StandardPieceFactory implements PieceFactory {
    @Override
    public Piece create(Colour colour, String code) {
        return switch (PieceType.fromCode(code)) {
            case KING -> new King(colour, Point.ORIGIN);
            case QUEEN -> new Queen(colour, Point.ORIGIN);
            case BISHOP -> new Bishop(colour, Point.ORIGIN);
            case KNIGHT -> new Knight(colour, Point.ORIGIN);
            case ROOK -> new Rook(colour, Point.ORIGIN);
            case PAWN -> new Pawn(colour, Point.ORIGIN);
            default -> null;
        };
    }
}
