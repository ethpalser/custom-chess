package com.ethpalser.chess.move;

import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoveMap {

    private final Map<Coordinate, Set<Piece>> map;
    private final int width;
    private final int length;

    public MoveMap(Colour colour, GameContext.Record ctxRecord) {
        Map<Coordinate, Set<Piece>> moves = new HashMap<>();
        for (Piece piece : ctxRecord.getBoard()) {
            if (piece != null && Pieces.isAllied(colour, piece)) {
                MoveSet moveSet = piece.getMoves(ctxRecord);
                for (Coordinate point : moveSet.coordinates()) {
                    moves.computeIfAbsent(point, k -> new HashSet<>()).add(piece);
                }
            }
        }
        this.map = moves;
        Space space = ctxRecord.getBoard().space();
        this.width = space.length(Space.AXIS.X);
        this.length = space.length(Space.AXIS.Y);
    }

    public Set<Coordinate> getPoints() {
        return this.map.keySet();
    }

    public Set<Piece> getPieces(Coordinate point) {
        if (point == null) {
            return Set.of();
        }
        Set<Piece> piecesThreateningPoint = this.map.get(point);
        if (piecesThreateningPoint == null) {
            return Set.of();
        }
        return piecesThreateningPoint;
    }

    public boolean hasNoMove(Coordinate point, boolean ignoreKing) {
        Set<Piece> set = this.getPieces(point);
        for (Piece p : set) {
            if (!ignoreKing || !Pieces.isKing(p)) {
                // Any piece (if the king is not ignored) or non-king piece can move to this point
                return false;
            }
        }
        return true;
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
                    sb.append("| + ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

}
