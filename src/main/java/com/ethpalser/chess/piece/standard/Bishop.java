package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.List;

public class Bishop implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Bishop(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Bishop(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "B";
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
        MoveSpec spec = new MoveSpec(new Path(context.getBoard().space(), new Point(), new int[]{1, 1}), true, true);
        return new MoveSet(spec.toMoveList(context, this.point, this.colour));
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
