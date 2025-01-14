package com.ethpalser.chess.move.config;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-08",
        lastModified = "2025-01-13"
)
public record PathOptions(Type type, Reference start, Reference end) {

    public PathOptions {
        if (type == null) {
            type = Type.CUSTOM;
        }
    }

    public PathOptions(Type type) {
        this(type, null, null);
    }

    public PathOptions(Reference reference) {
        this(Type.CUSTOM, reference, null);
    }

    public PathOptions(Coordinate... path) {
        this(Type.CUSTOM, new Reference(Reference.Location.PATH, Direction.AT, 1, path), null);
    }


    public enum Type {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        CUSTOM;
    }
}
