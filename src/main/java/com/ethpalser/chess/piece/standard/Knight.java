package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.PointUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class Knight implements ChessPiece {

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
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        Set<Point> set = new HashSet<>();
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -2, 1)); // left 2 up
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 2)); // up 2 left
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 2)); // up 2 right
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 2, 1)); // right 2 up
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 2, -1)); // right 2 down
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, -2)); // down 2 right
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, -2)); // down 2 left
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -2, -1)); // left 2 down
        set.remove(null);
        return new MoveSet(set);
    }

    @Override
    public Point getPoint() {
        return this.point;
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
