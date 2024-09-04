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

    public King(Colour colour, Point point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
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
        System.err.println("unsupported method for king used: getMoves(Plane<Piece> board)");
        return this.getMoves(board, null, null);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log) {
        System.err.println("unsupported method for king used: getMoves(Plane<Piece> board, Log<Point, Piece> log)");
        return this.getMoves(board, log, null);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats) {
        return this.getMoves(board, log, threats, false, false);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap opponentThreats,
            boolean onlyAttacks, boolean includeDefends) {
        MoveSet moveSet = new MoveSet(
                this.generateSafePointOrNull(board, opponentThreats, -1, 0, includeDefends), // left
                this.generateSafePointOrNull(board, opponentThreats, -1, 1, includeDefends), // top left
                this.generateSafePointOrNull(board, opponentThreats, 0, 1, includeDefends), // top
                this.generateSafePointOrNull(board, opponentThreats, 1, 1, includeDefends), // top right
                this.generateSafePointOrNull(board, opponentThreats, 1, 0, includeDefends), // right
                this.generateSafePointOrNull(board, opponentThreats, 1, -1, includeDefends), // bot right
                this.generateSafePointOrNull(board, opponentThreats, 0, -1, includeDefends), // bottom
                this.generateSafePointOrNull(board, opponentThreats, -1, -1, includeDefends) // bot left
        );

        // castling
        // not moved and not threatened (need to use the correct threat map)
        if (!this.hasMoved && opponentThreats != null && opponentThreats.hasNoThreats(this.point)) {
            int startRank = this.colour == Colour.WHITE ? board.getMinY() : board.getMaxY();

            // queen side (towards the left)
            Piece queenSideRook = board.get(new Point(board.getMinX(), startRank));
            if (queenSideRook != null && !queenSideRook.getHasMoved()
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
            if (kingSideRook != null && !kingSideRook.getHasMoved()
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
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    // PRIVATE METHODS

    private boolean isEmptyAndSafe(Plane<Piece> board, ThreatMap threatMap, int x, int y) {
        Point p = new Point(x, y);
        return board.get(p) == null && threatMap != null && threatMap.hasNoThreats(p);
    }

    private Point generateSafePointOrNull(Plane<Piece> board, ThreatMap threatMap, int xOffset, int yOffset,
            boolean includeDefends) {
        Point p = new Point(this.point.getX() + xOffset, this.point.getY() + yOffset);
        if (threatMap != null && threatMap.hasNoThreats(p)) {
            return Point.validOrNull(board, this.point, this.colour, -1, 0, includeDefends);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
