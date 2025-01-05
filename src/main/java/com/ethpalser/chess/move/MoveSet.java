package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
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

    public MoveSet(Move... moves) {
        this.set = new HashSet<>(Arrays.asList(moves));
        // Legacy code handles these differently and is not supported by this constructor
        this.attacks = List.of();
        this.defends = List.of();
    }

    public MoveSet(MoveReport... reports) {
        this(List.of(reports));
    }

    public MoveSet(Iterable<MoveReport> reports) {
        Set<Move> moves = new HashSet<>();
        Collection<Coordinate> attackList = new HashSet<>();
        Collection<Coordinate> defendList = new HashSet<>();
        Collection<Coordinate> checkList = new HashSet<>();
        for (MoveReport report : reports) {
            Move move = report.move();
            Path path = move.path();
            if (path == null || path.isEmpty()) {
                // There could be a move if the space was occupied (ex. pawn), and it did not fail conditions (ex. king)
                if (report.isAttack() && report.lastChecked() != null
                        && !report.status().equals(MoveReport.Status.OUT_OF_BOUNDS)
                        && !report.status().equals(MoveReport.Status.FAILED_CONDITIONS)) {
                    attackList.add(report.lastChecked());
                }
                continue;
            }
            moves.add(report.move());
            if (report.isAttack()) {
                // These are all threats that could lead to a capture
                for (Coordinate c : report.move().path()) {
                    attackList.add(c);
                }
                // These are all threats that ignore the king, which is necessary for determining checkmate
                if (report.threatsSinceKing() != null) {
                    attackList.addAll(report.threatsSinceKing());
                }
                if (MoveReport.Status.BLOCKED_BY_OPPONENT.equals(report.status())) {
                    attackList.add(report.lastChecked());
                } else if (MoveReport.Status.BLOCKED_BY_ALLY.equals(report.status())) {
                    defendList.add(report.lastChecked());
                }
            }
        }
        this.set = moves;
        this.attacks = attackList;
        this.defends = defendList;
    }

    public Set<Move> moves() {
        return this.set;
    }

    public Move getMove(Coordinate point) {
        return this.set.stream().filter(m -> m.path().hasPoint(point)).findFirst().orElse(null);
    }

    public Collection<Coordinate> coordinates() {
        Set<Coordinate> coordinates = new HashSet<>();
        for (Move m : this.set) {
            coordinates.addAll(m.path().toSet());
        }
        return coordinates;
    }

    public Collection<Coordinate> attacks() {
        return this.attacks;
    }

    public Collection<Coordinate> defends() {
        return this.defends;
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
