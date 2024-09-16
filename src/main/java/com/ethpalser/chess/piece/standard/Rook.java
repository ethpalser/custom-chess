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
import java.util.List;

public class Rook implements Piece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Rook(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Rook(Colour colour, Point point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
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
    public void setPoint(Point point) {
        this.point = point;
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
                new Move(Path.horizontal(board, this.point, this.colour, false, onlyAttacks, includeDefends)),
                new Move(Path.horizontal(board, this.point, this.colour, true, onlyAttacks, includeDefends)),
                new Move(Path.vertical(board, this.point, this.colour, false, onlyAttacks, includeDefends)),
                new Move(Path.vertical(board, this.point, this.colour, true, onlyAttacks, includeDefends))
        );
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
    public boolean canPromote(Plane<Piece> board) {
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
