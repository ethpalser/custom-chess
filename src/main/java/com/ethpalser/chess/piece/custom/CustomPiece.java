package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomPiece implements Piece {

    private final PieceType type;
    private final String code;
    private final Colour colour;
    private final List<MoveSpec> moveSpecifications;
    private Coordinate position;
    private boolean hasMoved;

    public CustomPiece(PieceType pieceType, Colour colour, Coordinate coordinate) {
        this(pieceType.toCode(), colour, coordinate, false, (MoveSpec) null);
    }

    public CustomPiece(String code, Colour colour, Coordinate coordinate, boolean hasMoved, MoveSpec... moveSpecs) {
        this(code, colour, coordinate, hasMoved, List.of(moveSpecs));
    }

    public CustomPiece(String code, Colour colour, Coordinate coordinate, boolean hasMoved, List<MoveSpec> moveSpecs) {
        this.type = PieceType.fromCode(code);
        this.code = code;
        this.colour = colour;
        this.position = coordinate;
        this.hasMoved = hasMoved;
        this.moveSpecifications = moveSpecs;
    }

    @Override
    public String getCode() {
        if (this.type != PieceType.CUSTOM) {
            return type.toCode();
        } else {
            return code;
        }
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Coordinate getCoordinate() {
        return this.position;
    }

    @Override
    public void setCoordinate(Coordinate point) {
        this.position = point;
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        Set<MoveReport> movements = new HashSet<>();
        for (MoveSpec spec : this.moveSpecifications) {
            movements.addAll(spec.toMoveList(context, this.position, this.colour));
        }
        return new MoveSet(movements);
    }

    public List<MoveSpec> getMoveSpecs() {
        return this.moveSpecifications;
    }

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        // Temporary work-around. This should be defined on construction by a configuration object/string
        if (PieceType.PAWN.toCode().equals(this.code)) {
            // temp promote condition
            return Colour.WHITE.equals(this.colour) && this.getCoordinate().getValue(2) == 7
                    || Colour.BLACK.equals(this.colour) && this.getCoordinate().getValue(2) == 0;
        } else {
            return false;
        }
    }

    @Override
    public List<String> promoteOptions() {
        // Temporary work-around. This should be defined on construction
        if (PieceType.PAWN.toCode().equals(this.code)) {
            return List.of(PieceType.QUEEN.toCode(), PieceType.KNIGHT.toCode(), PieceType.ROOK.toCode(),
                    PieceType.BISHOP.toCode());
        } else {
            return List.of();
        }
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.position.toString() + (this.hasMoved ? "" : "*");
    }

}
