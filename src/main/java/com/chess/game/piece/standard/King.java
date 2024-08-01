package com.chess.game.piece.standard;

import com.chess.game.ChessBoard;
import com.chess.game.ChessLog;
import com.chess.game.Colour;
import com.chess.game.Vector2D;
import com.chess.game.Vector2DUtil;
import com.chess.game.movement.ActionRecord;
import com.chess.game.piece.ChessPiece;
import com.chess.game.piece.Move;
import com.chess.game.piece.MoveRecord;
import com.chess.game.piece.MoveSet;
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
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        Set<Vector2D> set = new HashSet<>();
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 0)); // left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 1)); // top left
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, 1)); // top
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 1)); // top right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 0)); // right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, -1)); // bottom right
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, -1)); // bottom
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, -1)); // bottom left
        set.remove(null); // remove any case of null
        MoveSet moveSet = new MoveSet(set);

        // castling
        Colour opposite = this.colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        // not moved and not threatened
        if (!this.hasMoved && !board.hasThreats(this.point, opposite)) {
            int startRank = this.colour == Colour.WHITE ? board.getPieces().getMinX() : board.getPieces().getMaxY();
            // king side
            ChessPiece kingSideRook = board.getPiece(new Vector2D(board.getPieces().getMinX(), startRank));
            if (kingSideRook != null && !kingSideRook.hasMoved()
                    && isEmptyAndSafe(board, this.point.getX() - 1, this.point.getY(), this.colour)
                    && isEmptyAndSafe(board, this.point.getX() - 2, this.point.getY(), this.colour)
            ) {
                MoveRecord kingSideRookMove = new ActionRecord(
                        new Vector2D(0, startRank),
                        new Vector2D(this.point.getX() - 1, this.point.getY()),
                        kingSideRook
                );
                moveSet.addMove(new Move(new Vector2D(this.point.getX() - 2, this.point.getY()), kingSideRookMove));
            }
            // queen side
            ChessPiece queenSideRook = board.getPiece(new Vector2D(board.getPieces().getMaxX(), startRank));
            if (queenSideRook != null && !queenSideRook.hasMoved()
                    && isEmptyAndSafe(board, this.point.getX() + 1, this.point.getY(), this.colour)
                    && isEmptyAndSafe(board, this.point.getX() + 2, this.point.getY(), this.colour)
            ) {
                MoveRecord queenSideRookMove = new ActionRecord(
                        new Vector2D(0, startRank),
                        new Vector2D(this.point.getX() + 1, this.point.getY()),
                        queenSideRook
                );
                moveSet.addMove(new Move(new Vector2D(this.point.getX() - 2, this.point.getY()), queenSideRookMove));
            }
        }
        return moveSet;
    }

    private boolean isEmptyAndSafe(ChessBoard board, int x, int y, Colour colour) {
        Vector2D p = new Vector2D(x, y);
        return board.getPiece(p) == null && !board.hasThreats(p, colour);
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
