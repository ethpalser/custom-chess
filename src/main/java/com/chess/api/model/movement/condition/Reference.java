package com.chess.api.model.movement.condition;

import com.chess.api.model.Vector2D;

public record Reference(Location location, Direction direction, Vector2D vector) {

    public Reference() {
        this(Location.START);
    }

    public Reference(Location location) {
        this(location, Direction.AT, null);
    }

    public Reference(Location location, Vector2D vector) {
        this(location, Direction.AT, vector);
    }

    public Reference(Location location, Direction direction, Vector2D vector) {
        this.location = location;
        this.direction = direction;

        if (location == Location.VECTOR) {
            this.vector = vector;
        } else {
            this.vector = null;
        }
    }
}
