package com.ethpalser.chess.move;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.move.config.Reference;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-13",
        majorVersion = 3,
        lastModified = "2025-01-13"
)
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
