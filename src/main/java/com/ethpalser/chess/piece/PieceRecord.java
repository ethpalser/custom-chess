package com.ethpalser.chess.piece;

import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

public record PieceRecord(Colour colour, String code, Coordinate coordinate, boolean hasMoved) {

    public static PieceRecord fromString(String string) {
        // Creates five tokens with default values for missing parts, or throws exception if the string is invalid
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(string);
        // Order is defined by tokenizer
        Colour colour = Colour.fromCode(tokenizer.nextToken());
        String code = tokenizer.nextToken();
        boolean hasPiece = !PieceType.INVALID.toCode().equals(code);
        String x = tokenizer.nextToken();
        String y = tokenizer.nextToken();
        Coordinate coordinate = new Point(x + y);
        boolean hasMoved = "*".equals(tokenizer.nextToken()); // Asterisk is expected in tokenizer
        return new PieceRecord(hasPiece ? colour : null, hasPiece ? code : null, coordinate, hasMoved);
    }

}
