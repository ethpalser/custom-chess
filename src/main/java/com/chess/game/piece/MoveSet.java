package com.chess.game.piece;

import com.chess.game.Vector2D;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveSet {

    private final Set<Move> moves;

    public MoveSet(Set<Vector2D> points) {
        Set<Move> moveSet = new HashSet<>();
        for (Vector2D p : points) {
            moveSet.add(new Move(p));
        }
        this.moves = moveSet;
    }

    public Set<Move> getMoves() {
        return this.moves;
    }

    public Set<Vector2D> getPoints() {
        return this.moves.stream().map(Move::getPoint).collect(Collectors.toSet());
    }

}
