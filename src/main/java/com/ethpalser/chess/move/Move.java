package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Objects;

public class Move implements Movement {

    private final Path path;
    private final LogEntry<Coordinate, Piece> followUpMove;

    public Move(Coordinate point) {
        this(new Path((Point) point), null);
    }

    public Move(Coordinate point, LogEntry<Coordinate, Piece> followUpMove) {
        this(new Path((Point) point), followUpMove);
    }

    public Move(Path path) {
        this(path, null);
    }

    public Move(Path path, LogEntry<Coordinate, Piece> followUpMove) {
        this.path = path;
        this.followUpMove = followUpMove;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public LogEntry<Coordinate, Piece> getFollowUpMove() {
        return this.followUpMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(this.path, move.path) && Objects.equals(this.followUpMove, move.followUpMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.followUpMove);
    }

    @Override
    public String toString() {
        return "Move{" +
                "path=" + path +
                ", followUpMove=" + followUpMove +
                '}';
    }
}
