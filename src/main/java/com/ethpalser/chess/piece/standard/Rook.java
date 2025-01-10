package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.PathOptions;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    private static final String CODE = PieceType.ROOK.toCode();
    private static final List<MoveSpec> MOVE_SPECS = List.of(
            new MoveSpec(new PathOptions(PathOptions.Type.VERTICAL), true, false),
            new MoveSpec(new PathOptions(PathOptions.Type.HORIZONTAL), false, true)
    );

    public Rook(Colour colour, Coordinate point) {
        super(Rook.CODE, colour, point, false);
    }

    public Rook(Colour colour, Coordinate point, boolean hasMoved) {
        super(Rook.CODE, colour, point, hasMoved);
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        List<MoveReport> results = new ArrayList<>(16);
        for (MoveSpec spec : Rook.MOVE_SPECS) {
            results.addAll(spec.toMoveList(context, this.getCoordinate(), this.getColour()));
        }
        return new MoveSet(results);
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        return false;
    }

    @Override
    public List<String> getPromotions() {
        return List.of();
    }
}
