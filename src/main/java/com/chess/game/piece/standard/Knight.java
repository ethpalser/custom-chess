package com.chess.game.piece.standard;

import com.chess.game.ChessBoard;
import com.chess.game.ChessLog;
import com.chess.game.Colour;
import com.chess.game.Vector2D;
import com.chess.game.Vector2DUtil;
import com.chess.game.piece.ChessPiece;
import java.util.HashSet;
import java.util.Set;

public class Knight implements ChessPiece {

    private final Colour colour;
    private Vector2D point;
    private boolean hasMoved;

    public Knight(Colour colour, Vector2D point) {
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
    public Set<Vector2D> getMoves(ChessBoard board, ChessLog log) {
        Set<Vector2D> set = new HashSet<>();
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -2, 1)); // left 2 up
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 2)); // up 2 left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 2)); // up 2 right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 2, 1)); // right 2 up
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 2, -1)); // right 2 down
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, -2)); // down 2 right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, -2)); // down 2 left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -2, -1)); // left 2 down
        set.remove(null);
        return set;
    }

    @Override
    public boolean canMove(ChessBoard board, ChessLog log, Vector2D destination) {
        return this.getMoves(board, log).contains(destination);
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
