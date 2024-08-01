package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Vector2D;
import com.ethpalser.chess.board.Vector2DUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class Rook implements ChessPiece {

    private final Colour colour;
    private Vector2D point;
    private boolean hasMoved;

    public Rook(Colour colour, Vector2D point) {
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
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        Set<Vector2D> set = new HashSet<>();
        set.addAll(Vector2DUtil.generateHorizontalMoves(board, this.point, this.colour, false)); // left
        set.addAll(Vector2DUtil.generateHorizontalMoves(board, this.point, this.colour, true)); // right
        set.addAll(Vector2DUtil.generateVerticalMoves(board, this.point, this.colour, true)); // up
        set.addAll(Vector2DUtil.generateVerticalMoves(board, this.point, this.colour, false)); // down
        return new MoveSet(set);
    }

    @Override
    public boolean canMove(ChessBoard board, ChessLog log, Vector2D destination) {
        return this.getMoves(board, log).getMoves().stream().anyMatch(m -> m.getPoint().equals(destination));
    }

    @Override
    public void move(Vector2D destination) {
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
