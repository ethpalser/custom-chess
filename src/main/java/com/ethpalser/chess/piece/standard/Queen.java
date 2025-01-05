package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.List;

public class Queen implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Queen(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Queen(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "Q";
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
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        MoveSpec vSpec = new MoveSpec(new Path(context.getBoard().space(), new Point(0, 1), new int[]{0, 1}), true, false);
        MoveSpec hSpec = new MoveSpec(new Path(context.getBoard().space(), new Point(1, 0), new int[]{1, 0}), false, true);
        MoveSpec dSpec = new MoveSpec(new Path(context.getBoard().space(), new Point(1, 1), new int[]{1, 1}), true, true);

        List<MoveReport> results = new ArrayList<>(24);
        results.addAll(vSpec.toMoveList(context, this.point, this.colour));
        results.addAll(hSpec.toMoveList(context, this.point, this.colour));
        results.addAll(dSpec.toMoveList(context, this.point, this.colour));
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
