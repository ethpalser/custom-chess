package com.ethpalser.chess.space;

public enum Location {
    LAST_MOVED("last moved"),
    PATH("path"),
    POINT("point");

    private final String displayName;

    Location(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static Location fromString(String string) {
        for (Location location : Location.values()) {
            if (location.displayName.equals(string)) {
                return location;
            }
        }
        return POINT; // Default
    }
}
