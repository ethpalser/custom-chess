package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public class Bishop implements Piece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Bishop(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
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
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        return new MoveSet(
                new Move(Path.diagonal(board, this.point, this.colour, false, false, onlyAttacks, includeDefends)),
                new Move(Path.diagonal(board, this.point, this.colour, false, true, onlyAttacks, includeDefends)),
                new Move(Path.diagonal(board, this.point, this.colour, true, false, onlyAttacks, includeDefends)),
                new Move(Path.diagonal(board, this.point, this.colour, true, true, onlyAttacks, includeDefends))
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
