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
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.List;

public class King implements Piece {

    private final Colour colour;
    private Coordinate point;
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
    public Coordinate getCoordinate() {
        return this.point;
    }

    @Override
    public void setCoordinate(Coordinate point) {
        this.point = point;
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board) {
        System.err.println("unsupported method for king used: getMoves(Plane<Piece> board)");
        return this.getMoves(board, null, null);
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        System.err.println("unsupported method for king used: getMoves(Plane<Piece> board, Log<Point, Piece> log)");
        return this.getMoves(board, log, null);
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap threats) {
        return this.getMoves(board, log, threats, false, false);
    }

    @Override
    public MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap opponentThreats,
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
        if (!this.hasMoved && opponentThreats != null && opponentThreats.hasNoThreats((Point) this.point)) {
            int startRank = this.colour == Colour.WHITE ? 0 : 7; // assuming standard board

            // queen side (towards the left)
            Piece queenSideRook = board.get(new Point(0, startRank)); // assuming standard board
            if (queenSideRook != null && !queenSideRook.getHasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getValue(Space.AXIS.X) - 1, this.point.getValue(Space.AXIS.Y))
                    && isEmptyAndSafe(board, opponentThreats, this.point.getValue(Space.AXIS.X) - 2, this.point.getValue(Space.AXIS.Y))
            ) {
                LogEntry<Coordinate, Piece> queenSideRookMove = new ChessLogEntry(
                        new Point(0, startRank), // assuming standard board
                        new Point(this.point.getValue(Space.AXIS.X) - 1, this.point.getValue(Space.AXIS.Y)),
                        queenSideRook
                );
                moveSet.addMove(new Move(new Path(
                        new Point(this.point.getValue(Space.AXIS.X) - 1, this.point.getValue(Space.AXIS.Y)),
                        new Point(this.point.getValue(Space.AXIS.X) - 2, this.point.getValue(Space.AXIS.Y))
                ), queenSideRookMove));
            }

            // king side (towards the right)
            Piece kingSideRook = board.get(new Point(7, startRank)); // assuming standard board
            if (kingSideRook != null && !kingSideRook.getHasMoved()
                    && isEmptyAndSafe(board, opponentThreats, this.point.getValue(Space.AXIS.X) + 1, this.point.getValue(Space.AXIS.Y))
                    && isEmptyAndSafe(board, opponentThreats, this.point.getValue(Space.AXIS.X) + 2, this.point.getValue(Space.AXIS.Y))
            ) {
                LogEntry<Coordinate, Piece> kingSideRookMove = new ChessLogEntry(
                        new Point(7, startRank), // assuming standard board
                        new Point(this.point.getValue(Space.AXIS.X) + 1, this.point.getValue(Space.AXIS.Y)),
                        kingSideRook
                );
                moveSet.addMove(new Move(new Path(
                        new Point(this.point.getValue(Space.AXIS.X) + 1, this.point.getValue(Space.AXIS.Y)),
                        new Point(this.point.getValue(Space.AXIS.X) + 2, this.point.getValue(Space.AXIS.Y))
                ), kingSideRookMove));
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
        return false;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of();
    }

    // PRIVATE METHODS

    private boolean isEmptyAndSafe(Board<Coordinate> board, ThreatMap threatMap, int x, int y) {
        Point p = new Point(x, y);
        return board.get(p) == null && threatMap != null && threatMap.hasNoThreats(p);
    }

    private Point generateSafePointOrNull(Board<Coordinate> board, ThreatMap threatMap, int xOffset, int yOffset,
            boolean includeDefends) {
        Point p = (Point) this.point.translate(1, xOffset, yOffset);
        if (threatMap != null && threatMap.hasNoThreats(p)) {
            return Point.validOrNull(board, (Point) this.point, this.colour, -1, 0, includeDefends);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
