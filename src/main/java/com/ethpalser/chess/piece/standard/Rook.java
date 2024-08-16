package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public class Rook implements Piece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Rook(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    @Override
    public String getCode() {
        return "R";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Point getPoint() {
        return this.point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        return new MoveSet(
                new Move(new Path(Point.generateHorizontalMoves(board, this.point, this.colour, false))),
                new Move(new Path(Point.generateHorizontalMoves(board, this.point, this.colour, true))),
                new Move(new Path(Point.generateVerticalMoves(board, this.point, this.colour, false))),
                new Move(new Path(Point.generateVerticalMoves(board, this.point, this.colour, true)))
        );
    }

    @Override
    public void move(Point destination) {
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        this.point = destination;
        this.hasMoved = true;
    }

    @Override
    public boolean hasMoved() {
        return this.hasMoved;
    }

    @Override
    public String toString() {
        return this.getCode() + this.getPoint().toString();
    }
}
