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

public class Knight implements ChessPiece {

    private final Colour colour;
    private Vector2D point;

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
    public Set<Vector2D> getMoves(Space2D<Piece> board) {
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
    public boolean canMove(Space2D<Piece> board, Collection<ActionRecord> log, Vector2D destination) {
        return this.getMoves(board).contains(destination);
    }

    @Override
    public void move(Vector2D destination) {
        this.point = destination;
    }
}
