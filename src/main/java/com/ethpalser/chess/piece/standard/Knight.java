package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
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
        this.hasMoved = false;
    }

    public Knight(Colour colour, Point point, boolean hasMoved) {
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
    public Point getPoint() {
        return this.point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        // Log and Threats are not needed
        return this.getMoves(board, null, null, false, false);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats,
            boolean onlyAttacks, boolean includeDefends) {
        return new MoveSet(
                Point.validOrNull(board, this.point, this.colour, -2, 1, includeDefends), // left 2 up
                Point.validOrNull(board, this.point, this.colour, -1, 2, includeDefends), // up 2 left
                Point.validOrNull(board, this.point, this.colour, 1, 2, includeDefends), // up 2 right
                Point.validOrNull(board, this.point, this.colour, 2, 1, includeDefends), // right 2 up
                Point.validOrNull(board, this.point, this.colour, 2, -1, includeDefends), // right 2 down
                Point.validOrNull(board, this.point, this.colour, 1, -2, includeDefends), // down 2 right
                Point.validOrNull(board, this.point, this.colour, -1, -2, includeDefends), // down 2 left
                Point.validOrNull(board, this.point, this.colour, -2, -1, includeDefends) // left 2 down
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
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
