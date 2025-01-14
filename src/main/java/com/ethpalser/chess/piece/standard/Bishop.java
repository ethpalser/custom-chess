package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.config.MoveSpec;
import com.ethpalser.chess.move.config.PathOptions;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import java.util.ArrayList;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 3,
        minorVersion = 1,
        lastModified = "2025-01-13"
)
public class Bishop extends Piece {

    private static final String CODE = PieceType.BISHOP.toCode();
    private static final List<MoveSpec> MOVE_SPECS = List.of(
            new MoveSpec(new PathOptions(PathOptions.Type.DIAGONAL), true, true)
    );

    public Bishop(Colour colour, Coordinate point) {
        super(Bishop.CODE, colour, point, false);
    }

    public Bishop(Colour colour, Coordinate point, boolean hasMoved) {
        super(Bishop.CODE, colour, point, hasMoved);
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        List<MoveReport> results = new ArrayList<>(8);
        for (MoveSpec spec : Bishop.MOVE_SPECS) {
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
