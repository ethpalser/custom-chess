package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MoveSet {

    private final Set<Move> set;

    @Deprecated(since = "2024-08-13")
    public MoveSet(Set<Point> points) {
        Set<Move> moves = new HashSet<>();
        for (Point p : points) {
            moves.add(new Move(p));
        }
        this.set = moves;
    }

    public MoveSet(Path... paths) {
        Set<Move> moves = new HashSet<>();
        for (Path path : paths) {
            moves.add(new Move(path));
        }
        this.set = moves;
    }

    public MoveSet(Move... moves) {
        this.set = new HashSet<>(Arrays.asList(moves));
    }

    public Set<Move> toSet() {
        return this.set;
    }

    public Move getMove(Point point) {
        return this.set.stream().filter(m -> m.getPath().toSet().contains(point)).findFirst().orElse(null);
    }

    public void addMove(Move move) {
        this.set.add(move);
    }

    public Set<Point> getPoints() {
        Set<Point> points = new HashSet<>();
        for (Move m : this.set) {
            points.addAll(m.getPath().toSet());
        }
        return points;
    }

}
