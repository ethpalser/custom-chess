package com.chess.game.piece.standard;

import com.chess.game.Colour;
import com.chess.game.Space2D;
import com.chess.game.Vector2D;
import com.chess.game.Vector2DUtil;
import com.chess.game.movement.ActionRecord;
import com.chess.game.piece.ChessPiece;
import com.chess.game.piece.Piece;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Bishop implements ChessPiece {

    private final Colour colour;
    private Vector2D point;

    public Bishop(Colour colour, Vector2D point) {
        this.colour = colour;
        this.point = point;
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
    public Set<Vector2D> getMoves(Space2D<Piece> board) {
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null");
        }
        Set<Vector2D> set = new HashSet<>();
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, false, false)); // bottom left
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, false, true)); // top left
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, true, false)); // bottom right
        set.addAll(Vector2DUtil.generateDiagonalMoves(board, this.point, this.colour, true, true)); // top right
        return set;
    }

    @Override
    public boolean canMove(Space2D<Piece> board, Collection<ActionRecord> log, Vector2D destination) {
        return this.getMoves(board).contains(destination);
    }

    @Override
    public void move(Vector2D destination) {
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        this.point = destination;
    }
}
