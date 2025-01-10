package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.move.custom.condition.ConditionalOptions;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.PathOptions;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import java.util.ArrayList;
import java.util.List;

public class King implements Piece {

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

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public King(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public King(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "K";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Coordinate getCoordinate() {
        return this.point;
    }

    @Override
    public void setCoordinate(Coordinate point) {
        this.point = point;
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        List<MoveReport> results = new ArrayList<>(10);
        for (MoveSpec spec : King.MOVE_SPECS) {
            results.addAll(spec.toMoveList(context, this.point, this.colour));
        }
        return new MoveSet(results);
    }

    @Override
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        return false;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of();
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
