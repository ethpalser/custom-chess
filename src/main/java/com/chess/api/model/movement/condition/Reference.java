package com.chess.api.model.movement.condition;

import com.chess.api.model.Vector2D;

public record Reference(Location location, Vector2D vector) {

    public Reference() {
        this(Location.AT_START);
    }

    public Reference(Location location) {
        this(location, null);
    }

    public Reference(Location location, Vector2D vector) {
        this.location = location;
        if (location == Location.AT_COORDINATE) {
            this.vector = vector;
        } else {
            this.vector = null;
        }
    }
}
