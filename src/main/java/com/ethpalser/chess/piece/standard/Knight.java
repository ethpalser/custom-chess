package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public class Knight implements Piece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Knight(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
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
    public Point getPoint() {
        return this.point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        return new MoveSet(
                Point.generateValidPointOrNull(board, this.point, this.colour, -2, 1), // left 2 up
                Point.generateValidPointOrNull(board, this.point, this.colour, -1, 2), // up 2 left
                Point.generateValidPointOrNull(board, this.point, this.colour, 1, 2), // up 2 right
                Point.generateValidPointOrNull(board, this.point, this.colour, 2, 1), // right 2 up
                Point.generateValidPointOrNull(board, this.point, this.colour, 2, -1), // right 2 down
                Point.generateValidPointOrNull(board, this.point, this.colour, 1, -2), // down 2 right
                Point.generateValidPointOrNull(board, this.point, this.colour, -1, -2), // down 2 left
                Point.generateValidPointOrNull(board, this.point, this.colour, -2, -1) // left 2 down
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
}
