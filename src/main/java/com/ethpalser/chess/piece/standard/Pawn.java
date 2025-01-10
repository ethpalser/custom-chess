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
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.List;

public class Pawn implements Piece {

    private static final List<MoveSpec> MOVE_SPECS = List.of(
            // Move one - Move only
            new MoveSpec.Builder(new PathOptions(new Point(0, 1)))
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .build(),
            // Diagonal one - Capture only
            new MoveSpec.Builder(new PathOptions(new Point(1, 1)))
                    .isMirrorXAxis(false)
                    .isMove(false)
                    .build(),
            // Move two - Move only
            new MoveSpec.Builder(new PathOptions(new Point(0, 1), new Point(0, 2)))
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(ConditionalOptions.refNotMoved(new Reference())))
                    .build(),
            // En Passant front-right
            new MoveSpec.Builder(new PathOptions(new Point(1, 1)))
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            ConditionalOptions.refIsType(new Reference(Reference.Location.LAST_MOVED), PieceType.PAWN),
                            ConditionalOptions.refAtDirection(new Reference(Reference.Location.LAST_MOVED),
                                    Direction.RIGHT, 1),
                            ConditionalOptions.lastMovedDistance(2)
                    ))
                    // This should use the piece's relative point for reference. The captured pawn is to the right.
                    .followUp(new Reference(Reference.Location.POINT, Direction.RIGHT), null)
                    .build(),
            // En Passant front-left
            new MoveSpec.Builder(new PathOptions(new Point(1, 1)))
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            ConditionalOptions.refIsType(new Reference(Reference.Location.LAST_MOVED), PieceType.PAWN),
                            ConditionalOptions.refAtDirection(new Reference(Reference.Location.LAST_MOVED),
                                    Direction.LEFT, 1),
                            ConditionalOptions.lastMovedDistance(2)
                    ))
                    // This should use the piece's relative point for reference. The captured pawn is to the left.
                    .followUp(new Reference(Reference.Location.POINT, Direction.LEFT), null)
                    .build()
    );

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Pawn(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Pawn(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "P"; // Often it is nothing or a 'P'
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
        for (MoveSpec spec : Pawn.MOVE_SPECS) {
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
        int minY = board.space().min(Space.AXIS.Y);
        int maxY = board.space().max(Space.AXIS.Y);
        return Colour.WHITE.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == maxY
                || Colour.BLACK.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == minY;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of(PieceType.QUEEN.toCode(), PieceType.KNIGHT.toCode(), PieceType.ROOK.toCode(),
                PieceType.BISHOP.toCode());
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
