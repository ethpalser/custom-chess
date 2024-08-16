package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MoveSet {

    private final Set<Movement> set;

    public MoveSet(Set<Movement> moves) {
        this.set = moves;
    }

    public MoveSet(Point... points) {
        Set<Movement> moves = new HashSet<>();
        for (Point p : points) {
            if (p != null) {
                moves.add(new Move(p));
            }
        }
        moves.remove(null);
        this.set = moves;
    }

    public MoveSet(Path... paths) {
        Set<Movement> moves = new HashSet<>();
        for (Path path : paths) {
            if (!path.toSet().isEmpty()) {
                moves.add(new Move(path));
            }
        }
        moves.remove(null);
        this.set = moves;
    }

    public MoveSet(Movement... moves) {
        this.set = new HashSet<>(Arrays.asList(moves));
    }

    public Set<Movement> toSet() {
        return this.set;
    }

    public Movement getMove(Point point) {
        return this.set.stream().filter(m -> m.getPath().hasPoint(point)).findFirst().orElse(null);
    }

    public void addMove(Movement move) {
        this.set.add(move);
    }

    public Set<Point> getPoints() {
        Set<Point> points = new HashSet<>();
        for (Movement m : this.set) {
            points.addAll(m.getPath().toSet());
        }
        return points;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoveSet: [");
        Iterator<Movement> iterator = this.set.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
