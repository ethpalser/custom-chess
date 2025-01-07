package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.List;

public class Pawn implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public Pawn(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public Pawn(Colour colour, Coordinate point, boolean hasMoved) {
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
    public MoveSet getMoves(GameContext.Record context) {
        MoveSpec moveOne = new MoveSpec(new Path(new Point(0, 1)), false, false, true);
        MoveSpec capture = (new MoveSpec.Builder(new Path(new Point(1, 1))))
                .isMove(false)
                .isMirrorXAxis(false)
                .build();

        List<MoveReport> results = new ArrayList<>();
        results.addAll(moveOne.toMoveList(context, this.point, this.colour));
        results.addAll(capture.toMoveList(context, this.point, this.colour));

        // pawns can move forward two if it is their first move
        if (!this.hasMoved) {
            MoveSpec moveTwo = new MoveSpec(new Path(new Point(0, 1), new Point(0, 2)), false, false, true);
            results.addAll(moveTwo.toMoveList(context, this.point, this.colour));
        }

        // Special move: En Passant
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog(); // Todo: replace old log with new log
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
                // Setup common en passant specifications
                MoveSpec.Builder epSpec = new MoveSpec.Builder(new Path(new Point(1, 1)))
                        .isSpecificQuadrant(true)
                        .isMirrorXAxis(false)
                        .isAttack(false);
                // that pawn is to the left of this pawn
                Coordinate enPassantLeft = this.point.translate(1, Direction.LEFT.vector());
                if (enPassantLeft.equals(peekEnd)) {
                    epSpec.isMirrorYAxis(true)
                            .followUp(new Reference(Reference.Location.POINT, Direction.AT, enPassantLeft), null);
                    results.addAll(epSpec.build().toMoveList(context, this.point, this.colour));
                }
                // that pawn is to the right of this pawn
                Coordinate enPassantRight = this.point.translate(1, Direction.RIGHT.vector(this.colour));
                if (enPassantRight.equals(peekEnd)) {
                    epSpec.followUp(new Reference(Reference.Location.POINT, Direction.AT, enPassantRight), null);
                    results.addAll(epSpec.build().toMoveList(context, this.point, this.colour));
                }
            }
        }
        return new MoveSet(results);
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
        int minY = board.space().min(Space.AXIS.Y);
        int maxY = board.space().max(Space.AXIS.Y);
        return Colour.WHITE.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == maxY
                || Colour.BLACK.equals(this.colour) && this.getCoordinate().getValue(Space.AXIS.Y) == minY;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of(PieceType.QUEEN.toCode(), PieceType.KNIGHT.toCode(), PieceType.ROOK.toCode(),
                PieceType.BISHOP.toCode());
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
