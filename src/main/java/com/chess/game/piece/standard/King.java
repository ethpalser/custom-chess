package com.chess.game.piece.standard;

import com.chess.game.ChessBoard;
import com.chess.game.ChessLog;
import com.chess.game.Colour;
import com.chess.game.Vector2D;
import com.chess.game.Vector2DUtil;
import com.chess.game.piece.ChessPiece;
import java.util.HashSet;
import java.util.Set;

public class King implements ChessPiece {

    private final Colour colour;
    private Vector2D point;
    private boolean hasMoved;

    public King(Colour colour, Vector2D point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    @Override
    public String getCode() {
        return "K";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Set<Vector2D> getMoves(ChessBoard board, ChessLog log) {
        Set<Vector2D> set = new HashSet<>();
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 0)); // left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 1)); // top left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, 1)); // top
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 1)); // top right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 0)); // right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, -1)); // bottom right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, -1)); // bottom
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, -1)); // bottom left
        if (!hasMoved) {
            // todo: Add castling
            // todo: Need to use board for threats. Refactor is required
        }
        set.remove(null); // remove any case of null
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
