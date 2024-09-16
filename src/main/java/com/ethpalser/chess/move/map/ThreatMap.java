package com.ethpalser.chess.move.map;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.util.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThreatMap {

    private final Colour colour;
    private final Map<Point, Set<Piece>> map;
    private final int length;
    private final int width;

    public ThreatMap(Colour colour, Plane<Piece> board, Log<Point, Piece> log) {
        this.colour = colour;
        this.map = this.setup(colour, board, log);
        this.length = board.length();
        this.width = board.width();
    }

    public boolean hasNoThreats(Point point) {
        return this.getPieces(point).isEmpty();
    }

    public Colour getColour() {
        return this.colour;
    }

    public Set<Piece> getPieces(Point point) {
        if (point == null) {
            return Set.of();
        }
        Set<Piece> piecesThreateningPoint = this.map.get(point);
        if (piecesThreateningPoint == null) {
            return Set.of();
        }
        return piecesThreateningPoint;
    }

    private void clearMoves(Piece piece) {
        for (Point p : this.map.keySet()) {
            this.clearMoves(piece, p);
        }
    }

    private void clearMoves(Piece piece, Point point) {
        Set<Piece> set = this.map.get(point);
        if (set != null) {
            set.remove(piece);
        }
    }

    public void refreshThreats(Plane<Piece> board, Log<Point, Piece> log, Point point) {
        if (board == null || log == null || point == null) {
            String str = "one or more arguments are null" +
                    " board: " + (board == null) +
                    ", log: " + (log == null) +
                    ", point: " + (point == null);
            throw new NullPointerException(str);
        }
        Piece change = board.get(point);
        List<Tuple<Piece, Path>> tupleList = new ArrayList<>();
        // Remove the impacting piece temporarily
        board.remove(point);
        if (change != null && this.colour.equals(change.getColour())) {
            this.clearMoves(change);
        }

        // Get all paths that are along this point
        for (Piece piece : this.getPieces(point)) {
            if (!piece.equals(change)) {
                MoveSet moves = piece.getMoves(board, log, this, true, true);
                Movement moveWithPoint = moves.getMove(point);
                if (moveWithPoint != null) {
                    tupleList.add(new Tuple<>(piece, moveWithPoint.getPath()));
                }
            }
        }

        // Clear these paths
        for (Tuple<Piece, Path> tuple : tupleList) {
            for (Point p : tuple.getSecond()) {
                this.clearMoves(tuple.getFirst(), p);
            }
        }
        // Add the piece back, so we can reapply threats with this piece present
        if (change != null) {
            board.put(point, change);
        }

        boolean changeIsPresent = board.get(point) != null;
        for (Tuple<Piece, Path> tuple : tupleList) {
            // The only change from before and after are the paths that contain the impacted point
            boolean seenChange = false;
            for (Point p : tuple.getSecond()) {
                if (seenChange && changeIsPresent)
                    break;
                if (p.equals(point))
                    seenChange = true;
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(tuple.getFirst());
            }
        }
        if (change != null && this.colour.equals(change.getColour())) {
            MoveSet moves = change.getMoves(board, log, this, true, true);
            for (Point p : moves.getPoints()) {
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(change);
            }
        }
    }

    public Integer evaluate(Plane<Piece> board) {
        int direction = Colour.WHITE.equals(this.colour) ? 1 : -1;

        List<Piece> pawns = new ArrayList<>();
        List<Point> pawnThreats = new ArrayList<>();
        for (Piece p : board) {
            if (PieceType.PAWN.getCode().equals(p.getCode()) && this.colour.equals(p.getColour())) {
                pawns.add(p);

                Point left = Point.validOrNull(board, p.getPoint(), this.colour, -1, direction, true);
                if (left != null) {
                    pawnThreats.add(left);
                }

                Point right = Point.validOrNull(board, p.getPoint(), this.colour, 1, direction, true);
                if (right != null) {
                    pawnThreats.add(right);
                }
            }
        }

        return direction * (this.calculatePawnWall(pawnThreats, pawns)
                + this.calculatePawnCenterControl(pawnThreats, board.width() / 2, board.length() / 2)
                + this.calculateDoubleFilePawns(pawns));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                boolean hasThreat = map.get(new Point(x, y)) != null && !map.get(new Point(x, y)).isEmpty();
                if (!hasThreat) {
                    sb.append("|   ");
                } else {
                    sb.append("| x ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    // PRIVATE METHODS

    private Map<Point, Set<Piece>> setup(Colour colour, Plane<Piece> board, Log<Point, Piece> log) {
        Map<Point, Set<Piece>> piecesThreateningPoint = new HashMap<>();
        for (Piece piece : board) {
            if (piece != null && Pieces.isAllied(colour, piece)) {
                MoveSet moveSet = piece.getMoves(board, log, null, true, true);
                for (Point point : moveSet.getPoints()) {
                    piecesThreateningPoint.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        return piecesThreateningPoint;
    }

    private int calculatePawnWall(List<Point> pawnThreats, List<Piece> pawns) {
        int sum = 0;
        // Pawn defends
        for (Piece piece : pawns) {
            for (Point p : pawnThreats) {
                // This is a pawn that is defended by at least one other pawn. Doubled-up defends count for one each.
                if (PieceType.PAWN.getCode().equals(piece.getCode()) && piece.getPoint().equals(p)) {
                    sum++; // Currently, an arbitrarily set amount
                }
            }
        }
        return sum;
    }

    private int calculatePawnCenterControl(List<Point> pawnThreats, int midX, int midY) {
        int midX2;
        int midY2;
        if (midX % 2 == 0) {
            midX2 = midX - 1;
        } else {
            midX2 = midX;
        }
        if (midY % 2 == 0) {
            midY2 = midY - 1;
        } else {
            midY2 = midY;
        }

        Point midPoint1 = new Point(midX, midY);
        Point midPoint2 = new Point(midX, midY2);
        Point midPoint3 = new Point(midX2, midY);
        Point midPoint4 = new Point(midX2, midY2);
        int sum = 0;
        for (Point p : pawnThreats) {
            // A pawn has threat over a centre position on the board, which is often valuable
            if (p.equals(midPoint1) || p.equals(midPoint2) || p.equals(midPoint3) || p.equals(midPoint4)) {
                sum++;  // Currently, an arbitrarily set amount
            }
        }
        return sum;
    }

    private int calculateDoubleFilePawns(List<Piece> pawns) {
        Set<Integer> seen = new HashSet<>();
        int sum = 0;
        for (Piece p : pawns) {
            if (seen.contains(p.getPoint().getX())) {
                sum -= 1;
            }
            seen.add(p.getPoint().getX());
        }
        return sum;
    }
}
