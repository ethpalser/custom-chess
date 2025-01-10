package com.ethpalser.chess.move;

import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.move.config.Reference;

public record Move(Path path, FollowUp followUp) {

    public Move(Coordinate point) {
        this(new Path(point), null);
    }

    public Move(Path path) {
        this(path, null);
    }

    public record FollowUp(Reference reference, Path path) {

    }
}
