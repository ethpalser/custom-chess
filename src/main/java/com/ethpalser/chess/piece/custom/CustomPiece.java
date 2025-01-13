package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.config.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CustomPiece extends Piece {

    private final List<MoveSpec> moveSpecs;

    public CustomPiece(PieceType pieceType, Colour colour, Coordinate coordinate) {
        this(pieceType.toCode(), colour, coordinate, false, List.of());
    }

    public CustomPiece(String code, Colour colour, Coordinate coordinate, boolean hasMoved, MoveSpec... moveSpecs) {
        this(code, colour, coordinate, hasMoved, List.of(moveSpecs));
    }

    public CustomPiece(String code, Colour colour, Coordinate coordinate, boolean hasMoved, List<MoveSpec> moveSpecs) {
        super(code, colour, coordinate, hasMoved);
        this.moveSpecs = moveSpecs;
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        Set<MoveReport> movements = new HashSet<>();
        for (MoveSpec spec : this.moveSpecs) {
            movements.addAll(spec.toMoveList(context, this.getCoordinate(), this.getColour()));
        }
        return new MoveSet(movements);
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        // Temporary work-around. This should be defined on construction by a configuration object/string
        if (PieceType.PAWN.toCode().equals(this.getCode())) {
            // temp promote condition
            return Colour.WHITE.equals(this.getColour()) && this.getCoordinate().getValue(2) == 7
                    || Colour.BLACK.equals(this.getColour()) && this.getCoordinate().getValue(2) == 0;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getPromotions() {
        // Temporary work-around. This should be defined on construction
        if (PieceType.PAWN.toCode().equals(this.getCode())) {
            return List.of(PieceType.QUEEN.toCode(), PieceType.KNIGHT.toCode(), PieceType.ROOK.toCode(),
                    PieceType.BISHOP.toCode());
        } else {
            return List.of();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomPiece piece = (CustomPiece) o;
        return Objects.equals(moveSpecs, piece.moveSpecs);
    }

    @Override
    public int hashCode() {
        // Ignore move specs, as it is expected that all pieces with the same code have the same specification
        return Objects.hash(super.hashCode());
    }
}
