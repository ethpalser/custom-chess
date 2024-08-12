package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.PointUtil;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.game.LogRecord;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class Pawn implements ChessPiece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public Pawn(Colour colour, Point point) {
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
    public MoveSet getMoves(Board board) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoveSet getMoves(Board board, ChessLog log) {
        Set<Point> set = new HashSet<>();
        int y = this.colour == Colour.WHITE ? 1 : -1;
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 0, y));
        set.add(PointUtil.generateCapturePointOrNull(board, this.point, this.colour, -1, y));
        set.add(PointUtil.generateCapturePointOrNull(board, this.point, this.colour, 1, y));
        if (!this.hasMoved) {
            set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 0, y * 2));
        }
        // en passant (there must be at least one move)
        if (log != null && log.size() > 1) {
            LogRecord lastMove = log.peek();
            Point start = lastMove.getStart();
            Point destination = lastMove.getEnd();
            // a pawn moved forward two
            if (lastMove.isFirstMove() && "P".equals(board.getPiece(destination).getCode())
                    && ((lastMove.getMovingPiece().getColour() == Colour.WHITE && start.getX() + 2 == start.getY())
                    || (lastMove.getMovingPiece().getColour() == Colour.BLACK && start.getX() - 2 == start.getY()))
            ) {
                // that pawn is beside this pawn
                if (destination.getX() == this.point.getX() - 1) {
                    set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, y));
                }
                if (destination.getX() == this.point.getX() + 1) {
                    set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, y));
                }
            }
        }
        set.remove(null); // remove any case of null
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
