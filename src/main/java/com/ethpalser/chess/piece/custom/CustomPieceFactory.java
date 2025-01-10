package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.standard.Bishop;
import com.ethpalser.chess.piece.standard.King;
import com.ethpalser.chess.piece.standard.Knight;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.piece.standard.Queen;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Space;
import java.util.List;
import java.util.Map;

public class CustomPieceFactory implements PieceFactory {

    private final Map<String, List<MoveSpec>> pieceSpecs;

    public CustomPieceFactory(Map<String, List<MoveSpec>> pieceSpecs, Space space) {
        this.pieceSpecs = pieceSpecs;
    }

    @Override
    public Piece create(String code, Colour colour, Coordinate coordinate) {
        return switch (PieceType.fromCode(code)) {
            case QUEEN -> new Queen(colour, coordinate);
            case BISHOP -> new Bishop(colour, coordinate);
            case KNIGHT -> new Knight(colour, coordinate);
            case ROOK -> new Rook(colour, coordinate);
            case KING -> new King(colour, coordinate);
            case PAWN -> new Pawn(colour, coordinate);
            default -> new CustomPiece(code, colour, coordinate, false, this.pieceSpecs.get(code));
        };
    }
}
