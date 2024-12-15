package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.List;

public class Pawn implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Pawn(Colour colour, Point point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Pawn(Colour colour, Point point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
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
    public Coordinate getCoordinate() {
        return this.point;
    }

    @Override
    public void setCoordinate(Coordinate point) {
        this.point = point;
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board) {
        System.err.println("unsupported method used by pawn: getMoves(Plane<Piece> board)");
        return this.getMoves(board, null, null, false, false);
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        // Threats are not needed
        return this.getMoves(board, log, null, false, false);
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap threats,
            boolean onlyAttacks, boolean includeDefends) {
        int yOffset = this.colour == Colour.WHITE ? 1 : -1;
        if (onlyAttacks) {
            // As we want only attacks, include an attack even if there is no capture. Intended to use for threats.
            return new MoveSet(
                    Point.validOrNull(board, (Point) this.point, this.colour, -1, yOffset, includeDefends),
                    Point.validOrNull(board, (Point) this.point, this.colour, 1, yOffset, includeDefends)
            );
        }
        MoveSet moveSet = new MoveSet(
                Point.notCaptureOrNull(board, (Point) this.point, 0, yOffset),
                Point.captureOrNull(board, (Point) this.point, this.colour, -1, yOffset, includeDefends),
                Point.captureOrNull(board, (Point) this.point, this.colour, 1, yOffset, includeDefends)
        );

        // pawns can move forward two if it is their first move
        if (!this.hasMoved) {
            moveSet.addMove(new Move(new Path(
                    Point.notCaptureOrNull(board, (Point) this.point, 0, yOffset),
                    Point.notCaptureOrNull(board, (Point) this.point, 0, yOffset * 2)
            )));
        }

        // en passant (there must be at least one move)
        if (log != null && !log.isEmpty()) {
            LogEntry<Coordinate, Piece> lastMove = log.peek();
            Point peekStart = (Point) lastMove.getStart();
            Point peekEnd = (Point) lastMove.getEnd();
            // a pawn moved forward two
            if (lastMove.isFirstOccurrence() && board.get(peekEnd) != null && "P".equals(board.get(peekEnd).getCode())
                    && ((lastMove.getStartObject().getColour() == Colour.WHITE && peekStart.getY() + 2 == peekEnd.getY())
                    || (lastMove.getStartObject().getColour() == Colour.BLACK && peekStart.getY() - 2 == peekEnd.getY()))
            ) {
                // that pawn is to the left of this pawn
                Point left = Point.validOrNull(board, (Point) this.point, this.colour, -1, 0, false);
                if (left != null && left.equals(peekEnd)) {
                    Point enPassPoint = Point.validOrNull(board, (Point) this.point, this.colour, -1, yOffset, false);
                    moveSet.addMove(new Move(enPassPoint, new ChessLogEntry(left, null, board.get(left))));
                }
                // that pawn is to the right of this pawn
                Point right = Point.validOrNull(board, (Point) this.point, this.colour, 1, 0, false);
                if (right != null && right.equals(peekEnd)) {
                    Point enPassPoint = Point.validOrNull(board, (Point) this.point, this.colour, 1, yOffset, false);
                    moveSet.addMove(new Move(enPassPoint, new ChessLogEntry(right, null, board.get(right))));
                }
            }
        }
        return moveSet;
    }

    @Override
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        // Assuming standard board, todo: fix this
        return Colour.WHITE.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == 7
                || Colour.BLACK.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == 0;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of(PieceType.QUEEN.getCode(), PieceType.KNIGHT.getCode(), PieceType.ROOK.getCode(),
                PieceType.BISHOP.getCode());
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
