package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Vector2D;
import com.ethpalser.chess.board.Vector2DUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.game.LogRecord;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
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
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        Set<Vector2D> set = new HashSet<>();
        int y = this.colour == Colour.WHITE ? 1 : -1;
        set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, y));
        set.add(Vector2DUtil.generateCapturePointOrNull(board, this.point, this.colour, -1, y));
        set.add(Vector2DUtil.generateCapturePointOrNull(board, this.point, this.colour, 1, y));
        if (!this.hasMoved) {
            set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 0, y * 2));
        }
        // en passant (there must be at least one move)
        if (log != null && log.size() > 1) {
            LogRecord lastMove = log.peek();
            Vector2D start = lastMove.getStart();
            Vector2D destination = lastMove.getEnd();
            // a pawn moved forward two
            if (lastMove.isFirstMove() && "P".equals(board.getPiece(destination).getCode())
                    && ((lastMove.getMovingPiece().getColour() == Colour.WHITE && start.getX() + 2 == start.getY())
                    || (lastMove.getMovingPiece().getColour() == Colour.BLACK && start.getX() - 2 == start.getY()))
            ) {
                // that pawn is beside this pawn
                if (destination.getX() == this.point.getX() - 1) {
                    set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, -1, y));
                }
                if (destination.getX() == this.point.getX() + 1) {
                    set.add(Vector2DUtil.generateValidPointOrNull(board, this.point, this.colour, 1, y));
                }
            }
        }
        set.remove(null); // remove any case of null
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
