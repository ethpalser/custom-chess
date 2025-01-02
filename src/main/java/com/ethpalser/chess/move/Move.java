package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.reference.AbsoluteReference;
import com.ethpalser.chess.space.custom.reference.Reference;

public record Move(Path path, FollowUp followUp) {

    public Move(Coordinate point) {
        this(new Path((Point) point), (FollowUp) null);
    }

    public Move(Path path) {
        this(path, (FollowUp) null);
    }

    @Deprecated(since = "2025-01-01")
    public Move(Path path, LogEntry<Coordinate, Piece> followUpMove) {
        this(path, followUpMove == null ? null : new FollowUp(
                new AbsoluteReference<>(followUpMove.getStart()),
                new Path((Point) followUpMove.getEnd())
        ));
    }

    public record FollowUp(Reference<Piece> reference, Path path) {
    }
}
