package com.chess.api.model.movement.condition;

import com.chess.api.model.Coordinate;

public record Reference(Location location, Coordinate coordinate) {

    public Reference() {
        this(Location.AT_CURRENT);
    }

    public Reference(Location location) {
        this(location, null);
    }

    public Reference(Location location, Coordinate coordinate) {
        this.location = location;
        if (location == Location.AT_COORDINATE) {
            this.coordinate = coordinate;
        } else {
            this.coordinate = null;
        }
    }
}
