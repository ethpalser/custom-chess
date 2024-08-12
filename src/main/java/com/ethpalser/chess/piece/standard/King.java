package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.PointUtil;
import com.ethpalser.chess.game.ActionRecord;
import com.ethpalser.chess.game.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Move;
import com.ethpalser.chess.piece.MoveRecord;
import com.ethpalser.chess.piece.MoveSet;
import java.util.HashSet;
import java.util.Set;

public class King implements Piece {

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
    public MoveSet getMoves(Board board) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoveSet getMoves(Board board, Log log, ThreatMap opponentThreats) {
        Set<Point> set = new HashSet<>();
        set.add(this.generateSafeOrNull(board, opponentThreats, -1, 0)); // left
        set.add(this.generateSafeOrNull(board, opponentThreats, -1, 1)); // top left
        set.add(this.generateSafeOrNull(board, opponentThreats, 0, 1)); // top
        set.add(this.generateSafeOrNull(board, opponentThreats, 1, 1)); // top right
        set.add(this.generateSafeOrNull(board, opponentThreats, 1, 0)); // right
        set.add(this.generateSafeOrNull(board, opponentThreats, 1, -1)); // bottom right
        set.add(this.generateSafeOrNull(board, opponentThreats, 0, -1)); // bottom
        set.add(this.generateSafeOrNull(board, opponentThreats, -1, -1)); // bottom left
        set.remove(null); // remove any case of null
        MoveSet moveSet = new MoveSet(set);

        // castling
        // not moved and not threatened (need to use the correct threat map)
        if (!this.hasMoved && opponentThreats.getPieces(this.point).isEmpty()) {
            int startRank = this.colour == Colour.WHITE ? board.getPieces().getMinY() : board.getPieces().getMaxY();
            // king side
            Piece kingSideRook = board.getPiece(new Point(board.getPieces().getMinX(), startRank));
            if (kingSideRook != null && !kingSideRook.hasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() - 1, this.point.getY())
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() - 2, this.point.getY())
            ) {
                MoveRecord kingSideRookMove = new ActionRecord(
                        new Point(0, startRank),
                        new Point(this.point.getX() - 1, this.point.getY()),
                        kingSideRook
                );
                moveSet.addMove(new Move(new Point(this.point.getX() - 2, this.point.getY()), kingSideRookMove));
            }
            // queen side
            Piece queenSideRook = board.getPiece(new Point(board.getPieces().getMaxX(), startRank));
            if (queenSideRook != null && !queenSideRook.hasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() + 1, this.point.getY())
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() + 2, this.point.getY())
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

    // PRIVATE METHODS

    private boolean isSafe(ThreatMap threatMap, Point point) {
        return threatMap.getPieces(point).isEmpty();
    }

    private boolean isEmptyAndSafe(Board board, ThreatMap threatMap, int x, int y) {
        Point p = new Point(x, y);
        return board.getPiece(p) == null && isSafe(threatMap, p);
    }

    private Point generateSafeOrNull(Board board, ThreatMap threatMap, int xOffset, int yOffset) {
        Point p = new Point(this.point.getX() + xOffset, this.point.getY() + yOffset);
        if (isSafe(threatMap, p)) {
            return PointUtil.generateValidPointOrNull(board, this.point, this.colour, -1, 0);
        }
        return null;
    }
}
