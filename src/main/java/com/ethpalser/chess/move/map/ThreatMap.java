package com.ethpalser.chess.move.map;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThreatMap {

    private final Colour colour;
    private final Map<Coordinate, Set<Coordinate>> map;
    private final int width;
    private final int length;

    public ThreatMap(ThreatMap original) {
        this.colour = original.colour;
        // Note: The map's values are object references (to a Set), a change to these will change the original
        this.map = new HashMap<>(original.map);
        this.width = original.width;
        this.length = original.length;
    }

    public ThreatMap(Colour colour, Space space, Board<Coordinate> board, ChessLog log) {
        if (space == null || board == null || log == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        Map<Coordinate, Set<Coordinate>> piecesThreateningPoint = new HashMap<>();
        // Create a snapshot of the GameContext without any threats, so this must be refreshed after for pieces affected
        GameContext.Record ctxRecord = new GameContext.Record(board, log, null, null);
        for (Piece piece : board) {
            if (colour.equals(piece.getColour())) {
                MoveSet set = piece.getMoves(ctxRecord);
                for (Coordinate threatened : set.attacks()) {
                    piecesThreateningPoint.computeIfAbsent(threatened, k -> new HashSet<>()).add(piece.getCoordinate());
                }
                for (Coordinate defended : set.defends()) {
                    piecesThreateningPoint.computeIfAbsent(defended, k -> new HashSet<>()).add(piece.getCoordinate());
                }
            }
        }
        this.colour = colour;
        this.map = piecesThreateningPoint;
        this.width = space.length(1);
        this.length = space.length(2);
    }

    public boolean hasNoThreats(Coordinate point) {
        return this.getThreats(point).isEmpty();
    }

    public void addThreat(Coordinate attacker, Coordinate threatened) {
        this.map.computeIfAbsent(threatened, k -> new HashSet<>()).add(attacker);
    }

    public void addThreats(Coordinate attacker, MoveSet moveSet) {
        for (Coordinate threatened : moveSet.attacks()) {
            this.addThreat(attacker, threatened);
        }
        for (Coordinate defended : moveSet.defends()) {
            this.addThreat(attacker, defended);
        }
    }

    public Set<Coordinate> getThreats(Coordinate point) {
        if (point == null) {
            return Set.of();
        }
        Set<Coordinate> threatenedPoints = this.map.get(point);
        if (threatenedPoints == null) {
            return Set.of();
        }
        return threatenedPoints;
    }

    public void removeThreats(Coordinate attacker) {
        for (Coordinate threatened : this.map.keySet()) {
            this.removeThreats(attacker, threatened);
        }
    }

    public void removeThreats(Coordinate attacker, Coordinate threatened) {
        Set<Coordinate> set = this.map.get(threatened);
        if (set != null) {
            // All attackers are located in a set for each coordinate
            set.remove(attacker);
        }
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
