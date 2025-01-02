package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.PathReport;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MoveSet {

    private final Set<Move> set;
    private final Collection<Coordinate> attacks;
    private final Collection<Coordinate> defends;

    public MoveSet(Set<Move> moves) {
        this.set = moves;
        // Legacy code handles these differently and is not supported by this constructor
        this.attacks = List.of();
        this.defends = List.of();
    }

    public MoveSet(Point... points) {
        Set<Move> moves = new HashSet<>();
        for (Point p : points) {
            if (p != null) {
                moves.add(new Move(new Path(p), (Move.FollowUp) null));
            }
        }
        moves.remove(null);
        this.set = moves;
        // Legacy code handles these differently and is not supported by this constructor
        this.attacks = List.of();
        this.defends = List.of();
    }

    public MoveSet(Path... paths) {
        Set<Move> moves = new HashSet<>();
        for (Path path : paths) {
            if (!path.toSet().isEmpty()) {
                moves.add(new Move(path, (Move.FollowUp) null));
            }
        }
        moves.remove(null);
        this.set = moves;
        // Legacy code handles these differently and is not supported by this constructor
        this.attacks = List.of();
        this.defends = List.of();
    }

    public MoveSet(Move... moves) {
        this.set = new HashSet<>(Arrays.asList(moves));
        // Legacy code handles these differently and is not supported by this constructor
        this.attacks = List.of();
        this.defends = List.of();
    }

    public MoveSet(PathReport... pathReports) {
        Set<Move> movements = new HashSet<>();
        Collection<Coordinate> attackList = new ArrayList<>();
        Collection<Coordinate> defendList = new ArrayList<>();
        for (PathReport report : pathReports) {
            if (!report.path().isEmpty()) {
                movements.add(new Move(report.path(), (Move.FollowUp) null));
            }
            if (report.status().equals(PathReport.Status.BLOCKED_BY_OPPONENT)) {
                attackList.add(report.lastChecked());
            } else if (report.status().equals(PathReport.Status.BLOCKED_BY_ALLY)) {
                defendList.add(report.lastChecked());
            }
        }
        this.set = movements;
        this.attacks = attackList;
        this.defends = defendList;
    }

    public Set<Move> toSet() {
        return this.set;
    }

    public Move getMove(Point point) {
        return this.set.stream().filter(m -> m.path().hasPoint(point)).findFirst().orElse(null);
    }

    public void addMove(Move move) {
        this.set.add(move);
    }

    public Set<Point> getPoints() {
        Set<Point> points = new HashSet<>();
        for (Move m : this.set) {
            points.addAll(m.path().toSet());
        }
        return points;
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoveSet: [");
        Iterator<Move> iterator = this.set.iterator();
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
