package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.config.MoveSpec;
import com.ethpalser.chess.move.config.ConditionalOptions;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.move.config.PathOptions;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.move.config.Reference;
import java.util.ArrayList;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 3,
        minorVersion = 8,
        lastModified = "2025-01-13"
)
public class King extends Piece {

    private static final String CODE = PieceType.KING.toCode();
    private static final List<MoveSpec> MOVE_SPECS = List.of(
            new MoveSpec(new PathOptions(new Point(0, 1)), true, false),
            new MoveSpec(new PathOptions(new Point(1, 0)), false, true),
            new MoveSpec(new PathOptions(new Point(1, 1)), true, true),
            // Castle Queen-side
            (new MoveSpec.Builder(new PathOptions(
                    new Reference(Reference.Location.POINT, Direction.RIGHT, 2, Point.ORIGIN) // Blueprint
            )))
                    .isAttack(false)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(true)
                    .conditions(List.of(
                            ConditionalOptions.refNotMoved(new Reference()),
                            ConditionalOptions.refNotMoved(new Reference(Reference.Location.WEST_EDGE)),
                            ConditionalOptions.refIsType(new Reference(Reference.Location.WEST_EDGE), PieceType.ROOK),
                            ConditionalOptions.pathIsEmpty(
                                    new Reference(Reference.Location.POINT, Direction.LEFT),
                                    new Reference(Reference.Location.WEST_EDGE, Direction.RIGHT)
                            )
                    ))
                    .followUp(
                            new Reference(Reference.Location.WEST_EDGE),
                            new PathOptions(new Reference(Reference.Location.POINT, Direction.LEFT))
                    )
                    .build(),
            // Castle King-side
            (new MoveSpec.Builder(new PathOptions(
                    new Reference(Reference.Location.POINT, Direction.RIGHT, 2, Point.ORIGIN) // Blueprint
            )))
                    .isAttack(false)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .conditions(List.of(
                            ConditionalOptions.refNotMoved(new Reference()),
                            ConditionalOptions.refNotMoved(new Reference(Reference.Location.EAST_EDGE)),
                            ConditionalOptions.refIsType(new Reference(Reference.Location.EAST_EDGE), PieceType.ROOK),
                            ConditionalOptions.pathIsEmpty(
                                    new Reference(Reference.Location.POINT, Direction.RIGHT),
                                    new Reference(Reference.Location.EAST_EDGE, Direction.LEFT)
                            )
                    ))
                    .followUp(
                            new Reference(Reference.Location.EAST_EDGE, Direction.AT),
                            new PathOptions(new Reference(Reference.Location.POINT, Direction.RIGHT))
                    )
                    .build()
    );

    public King(Colour colour, Coordinate point) {
        super(King.CODE, colour, point, false);
    }

    public King(Colour colour, Coordinate point, boolean hasMoved) {
        super(King.CODE, colour, point, hasMoved);
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        List<MoveReport> results = new ArrayList<>(10);
        for (MoveSpec spec : King.MOVE_SPECS) {
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
