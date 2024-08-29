package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;

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
    public Point getPoint() {
        return this.point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        System.err.println("generally unsupported method for king used: getMoves(Plane<Piece> board)");
        return this.getMoves(board, null, null);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap opponentThreats) {
        MoveSet moveSet = new MoveSet(
                this.generateSafePointOrNull(board, opponentThreats, -1, 0), // left
                this.generateSafePointOrNull(board, opponentThreats, -1, 1), // top left
                this.generateSafePointOrNull(board, opponentThreats, 0, 1), // top
                this.generateSafePointOrNull(board, opponentThreats, 1, 1), // top right
                this.generateSafePointOrNull(board, opponentThreats, 1, 0), // right
                this.generateSafePointOrNull(board, opponentThreats, 1, -1), // bottom right
                this.generateSafePointOrNull(board, opponentThreats, 0, -1), // bottom
                this.generateSafePointOrNull(board, opponentThreats, -1, -1) // bottom left
        );

        // castling
        // not moved and not threatened (need to use the correct threat map)
        if (!this.hasMoved && opponentThreats != null && opponentThreats.getPieces(this.point).isEmpty()) {
            int startRank = this.colour == Colour.WHITE ? board.getMinY() : board.getMaxY();

            // queen side (towards the left)
            Piece queenSideRook = board.get(new Point(board.getMinX(), startRank));
            if (queenSideRook != null && !queenSideRook.hasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() - 1, this.point.getY())
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() - 2, this.point.getY())
            ) {
                LogEntry<Point, Piece> queenSideRookMove = new ChessLogEntry(
                        new Point(0, startRank),
                        new Point(this.point.getX() - 1, this.point.getY()),
                        queenSideRook
                );
                moveSet.addMove(new Move(new Path(
                        new Point(this.point.getX() - 1, this.point.getY()),
                        new Point(this.point.getX() - 2, this.point.getY())
                ), queenSideRookMove));
            }

            // king side (towards the right)
            Piece kingSideRook = board.get(new Point(board.getMaxX(), startRank));
            if (kingSideRook != null && !kingSideRook.hasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() + 1, this.point.getY())
                    && isEmptyAndSafe(board, opponentThreats, this.point.getX() + 2, this.point.getY())
            ) {
                LogEntry<Point, Piece> kingSideRookMove = new ChessLogEntry(
                        new Point(board.getMaxX(), startRank),
                        new Point(this.point.getX() + 1, this.point.getY()),
                        kingSideRook
                );
                moveSet.addMove(new Move(new Path(
                        new Point(this.point.getX() + 1, this.point.getY()),
                        new Point(this.point.getX() + 2, this.point.getY())
                ), kingSideRookMove));
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

    // PRIVATE METHODS

    private boolean isSafe(ThreatMap threatMap, Point point) {
        if (threatMap == null || point == null) {
            return false;
        }
        return threatMap.getPieces(point).isEmpty();
    }

    private boolean isEmptyAndSafe(Plane<Piece> board, ThreatMap threatMap, int x, int y) {
        Point p = new Point(x, y);
        return board.get(p) == null && isSafe(threatMap, p);
    }

    private Point generateSafePointOrNull(Plane<Piece> board, ThreatMap threatMap, int xOffset, int yOffset) {
        Point p = new Point(this.point.getX() + xOffset, this.point.getY() + yOffset);
        if (isSafe(threatMap, p)) {
            return Point.validOrNull(board, this.point, this.colour, -1, 0);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getCode() + this.getPoint().toString();
    }
}
