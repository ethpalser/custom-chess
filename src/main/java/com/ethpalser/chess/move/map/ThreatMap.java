package com.ethpalser.chess.move.map;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.Heuristics;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThreatMap {

    private final Colour colour;
    private final Map<Point, Set<Piece>> map;
    private final int width;
    private final int length;

    public ThreatMap(ThreatMap original) {
        this.colour = original.colour;
        this.map = new HashMap<>(original.map);
        // Todo: remove these from this class, as drawing should only be done by the game or wherever the space is known
        this.width = original.width;
        this.length = original.length;
    }

    public ThreatMap(Colour colour, Board<Coordinate> board, Log<Coordinate, Piece> log, Space space) {
        if (colour == null || space == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (space.getDimension() < 2) {
            throw new IllegalArgumentException("Space must be have 2 dimensions or greater");
        }
        this.colour = colour;
        // setup threat map
        Map<Point, Set<Piece>> piecesThreateningPoint = new HashMap<>();
        for (Piece piece : board) {
            if (piece != null && Pieces.isAllied(colour, piece)) {
                MoveSet moveSet = piece.getMoves(board, log, null, true, true);
                for (Point point : moveSet.getPoints()) {
                    piecesThreateningPoint.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        this.map = piecesThreateningPoint;
        this.width = space.length(1);
        this.length = space.length(2);
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

    public void refreshThreats(Board<Coordinate> board, Log<Coordinate, Piece> log, Point point) {
        if (board == null || log == null || point == null) {
            String str = "one or more arguments are null" +
                    " board: " + (board == null) +
                    ", log: " + (log == null) +
                    ", point: " + (point == null);
            throw new NullPointerException(str);
        }
        Piece change = board.get(point);
        List<Pair<Piece, Path>> pairList = new ArrayList<>();
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
                    pairList.add(new Pair<>(piece, moveWithPoint.getPath()));
                }
            }
        }

        // Clear these paths
        for (Pair<Piece, Path> pair : pairList) {
            for (Point p : pair.getSecond()) {
                this.clearMoves(pair.getFirst(), p);
            }
        }
        // Add the piece back, so we can reapply threats with this piece present
        if (change != null) {
            board.add(point, change);
        }

        boolean changeIsPresent = board.get(point) != null;
        for (Pair<Piece, Path> pair : pairList) {
            // The only change from before and after are the paths that contain the impacted point
            boolean seenChange = false;
            for (Point p : pair.getSecond()) {
                if (seenChange && changeIsPresent)
                    break;
                if (p.equals(point))
                    seenChange = true;
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(pair.getFirst());
            }
        }
        if (change != null && this.colour.equals(change.getColour())) {
            MoveSet moves = change.getMoves(board, log, this, true, true);
            for (Point p : moves.getPoints()) {
                this.map.computeIfAbsent(p, k -> new HashSet<>()).add(change);
            }
        }
    }

    public Integer evaluate(Board<Coordinate> board) {
        int direction = Colour.WHITE.equals(this.colour) ? 1 : -1;

        List<Piece> pawns = new ArrayList<>();
        List<Point> pawnThreats = new ArrayList<>();
        for (Piece p : board) {
            if (PieceType.PAWN.getCode().equals(p.getCode()) && this.colour.equals(p.getColour())) {
                pawns.add(p);

                Point left = Point.validOrNull(board, (Point) p.getCoordinate(), this.colour, -1, direction, true);
                if (left != null) {
                    pawnThreats.add(left);
                }

                Point right = Point.validOrNull(board, (Point) p.getCoordinate(), this.colour, 1, direction, true);
                if (right != null) {
                    pawnThreats.add(right);
                }
            }
        }

        return Heuristics.pawnWall(pawnThreats, pawns)
                + Heuristics.pawnCenterControl(pawnThreats, this.width / 2, this.length / 2)
                + Heuristics.doubleFilePawns(pawns);
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

}
