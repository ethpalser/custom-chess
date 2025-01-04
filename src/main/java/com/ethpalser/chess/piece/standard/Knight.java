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

public class Knight implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Knight(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Knight(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "N";
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
        MoveSpec rSpec = new MoveSpec(new Path(new Point(1, 2)), true, true);
        MoveSpec uSpec = new MoveSpec(new Path(new Point(2, 1)), true, true);

        List<MoveReport> results = new ArrayList<>(8);
        results.addAll(rSpec.toMoveList(context, this.point, this.colour));
        results.addAll(uSpec.toMoveList(context, this.point, this.colour));
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
