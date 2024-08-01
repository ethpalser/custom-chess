package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.board.PointUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class Queen implements ChessPiece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Queen(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
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
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        Set<Point> set = new HashSet<>();
        set.addAll(PointUtil.generateHorizontalMoves(board, this.point, this.colour, false)); // left
        set.addAll(PointUtil.generateHorizontalMoves(board, this.point, this.colour, true)); // right
        set.addAll(PointUtil.generateVerticalMoves(board, this.point, this.colour, true)); // up
        set.addAll(PointUtil.generateVerticalMoves(board, this.point, this.colour, false)); // down
        set.addAll(PointUtil.generateDiagonalMoves(board, this.point, this.colour, false, false)); // bottom left
        set.addAll(PointUtil.generateDiagonalMoves(board, this.point, this.colour, false, true)); // top left
        set.addAll(PointUtil.generateDiagonalMoves(board, this.point, this.colour, true, false)); // bottom right
        set.addAll(PointUtil.generateDiagonalMoves(board, this.point, this.colour, true, true)); // top right
        return new MoveSet(set);
    }

    @Override
    public boolean canMove(ChessBoard board, ChessLog log, Point destination) {
        return this.getMoves(board, log).getMoves().stream().anyMatch(m -> m.getPoint().equals(destination));
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
