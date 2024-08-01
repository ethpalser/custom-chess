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

public class Pawn implements ChessPiece {

    private final Colour colour;
    private Vector2D point;
    private boolean hasMoved;

    public Pawn(Colour colour, Vector2D point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    @Override
    public String getCode() {
        return ""; // Often it is nothing or a 'P'
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Set<Vector2D> getMoves(Space2D<Piece> board) {
        Set<Vector2D> set = new HashSet<>();
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, 1));
        set.add(Vector2DUtil.generateCapturePointOrNull(board, this.point, this.colour, -1, 1));
        set.add(Vector2DUtil.generateCapturePointOrNull(board, this.point, this.colour, 1, 1));
        if (!this.hasMoved) {
            set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, 2));
        }
        // todo: add en passant
        set.remove(null); // remove any case of null
        return set;
    }

    @Override
    public boolean canMove(Space2D<Piece> board, Collection<ActionRecord> log, Vector2D destination) {
        return this.getMoves(board).contains(destination);
    }

    @Override
    public void move(Vector2D destination) {
        this.point = destination;
        this.hasMoved = true;
    }
}
