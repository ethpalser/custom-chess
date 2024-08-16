package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

public class Pawn implements Piece {

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
        return "P"; // Often it is nothing or a 'P'
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Point getPoint() {
        return this.point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        System.err.println("generally unsupported method used by pawn: getMoves(Plane<Piece> board)");
        return this.getMoves(board, null);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log) {
        int nextY = this.colour == Colour.WHITE ? this.point.getY() + 1 : this.point.getY() - 1;
        MoveSet moveSet = new MoveSet(
                Point.generateValidPointOrNull(board, this.point, this.colour, 0, nextY),
                Point.generateCapturePointOrNull(board, this.point, this.colour, -1, nextY),
                Point.generateCapturePointOrNull(board, this.point, this.colour, 1, nextY)
        );

        // pawns can move forward two if it is their first move
        if (!this.hasMoved) {
            moveSet.addMove(new Move(new Path(
                    Point.generateValidPointOrNull(board, this.point, this.colour, 0, nextY),
                    Point.generateValidPointOrNull(board, this.point, this.colour, 0, nextY * 2)
            )));
        }

        // en passant (there must be at least one move)
        if (log != null && log.size() > 1) {
            LogEntry<Point, Piece> lastMove = log.peek();
            Point peekStart = lastMove.getStart();
            Point peekEnd = lastMove.getEnd();

            // a pawn moved forward two
            if (lastMove.isFirstOccurrence() && "P".equals(board.get(peekEnd).getCode())
                    && ((lastMove.getStartObject().getColour() == Colour.WHITE && peekStart.getX() + 2 == peekStart.getY())
                    || (lastMove.getStartObject().getColour() == Colour.BLACK && peekStart.getX() - 2 == peekStart.getY()))
            ) {
                // that pawn is to the left of this pawn
                Point left = Point.generateValidPointOrNull(board, this.point, this.colour, -1, 0);
                if (left != null && left.equals(peekEnd)) {
                    Point enPassPoint = Point.generateValidPointOrNull(board, this.point, this.colour, -1, nextY);
                    moveSet.addMove(new Move(enPassPoint, new ChessLogEntry(left, null, board.get(left))));
                }
                // that pawn is to the right of this pawn
                Point right = Point.generateValidPointOrNull(board, this.point, this.colour, 1, 0);
                if (right != null && right.equals(peekEnd)) {
                    Point enPassPoint = Point.generateValidPointOrNull(board, this.point, this.colour, 1, nextY);
                    moveSet.addMove(new Move(enPassPoint, new ChessLogEntry(right, null, board.get(right))));
                }
            }
        }
        return moveSet;
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
