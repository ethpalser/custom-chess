package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.board.PointUtil;
import com.ethpalser.chess.game.ActionRecord;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Move;
import com.ethpalser.chess.piece.MoveRecord;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class King implements ChessPiece {

    private final Colour colour;
    private Point point;
    private boolean hasMoved;

    public King(Colour colour, Point point) {
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
        Set<Point> set = new HashSet<>();
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 0)); // left
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 1)); // top left
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 0, 1)); // top
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 1)); // top right
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, 0)); // right
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 1, -1)); // bottom right
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, 0, -1)); // bottom
        set.add(PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, -1)); // bottom left
        set.remove(null); // remove any case of null
        MoveSet moveSet = new MoveSet(set);

        // castling
        Colour opposite = this.colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        // not moved and not threatened
        if (!this.hasMoved && !board.hasThreats(this.point, opposite)) {
            int startRank = this.colour == Colour.WHITE ? board.getPieces().getMinY() : board.getPieces().getMaxY();
            // king side
            ChessPiece kingSideRook = board.getPiece(new Point(board.getPieces().getMinX(), startRank));
            if (kingSideRook != null && !kingSideRook.hasMoved()
                    && isEmptyAndSafe(board, this.point.getX() - 1, this.point.getY(), this.colour)
                    && isEmptyAndSafe(board, this.point.getX() - 2, this.point.getY(), this.colour)
            ) {
                MoveRecord kingSideRookMove = new ActionRecord(
                        new Point(0, startRank),
                        new Point(this.point.getX() - 1, this.point.getY()),
                        kingSideRook
                );
                moveSet.addMove(new Move(new Point(this.point.getX() - 2, this.point.getY()), kingSideRookMove));
            }
            // queen side
            ChessPiece queenSideRook = board.getPiece(new Point(board.getPieces().getMaxX(), startRank));
            if (queenSideRook != null && !queenSideRook.hasMoved()
                    && isEmptyAndSafe(board, this.point.getX() + 1, this.point.getY(), this.colour)
                    && isEmptyAndSafe(board, this.point.getX() + 2, this.point.getY(), this.colour)
            ) {
                MoveRecord queenSideRookMove = new ActionRecord(
                        new Point(0, startRank),
                        new Point(this.point.getX() + 1, this.point.getY()),
                        queenSideRook
                );
                moveSet.addMove(new Move(new Point(this.point.getX() - 2, this.point.getY()), queenSideRookMove));
            }
        }
        return moveSet;
    }

    @Override
    public Point getPoint() {
        return this.point;
    }

    private boolean isEmptyAndSafe(ChessBoard board, int x, int y, Colour colour) {
        Point p = new Point(x, y);
        return board.getPiece(p) == null && !board.hasThreats(p, colour);
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
