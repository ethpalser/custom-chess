package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Objects;

public class Move implements Movement {

    private final Path path;
    private final LogEntry<Point, Piece> followUpMove;

    public Move(Point point) {
        this(new Path(point), null);
    }

    public Move(Point point, LogEntry<Point, Piece> followUpMove) {
        this(new Path(point), followUpMove);
    }

    public Move(Path path) {
        this(path, null);
    }

    public Move(Path path, LogEntry<Point, Piece> followUpMove) {
        this.path = path;
        this.followUpMove = followUpMove;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public LogEntry<Point, Piece> getFollowUpMove() {
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
