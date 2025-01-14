package com.ethpalser.chess.condition.reflection;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-22",
        lastModified = "2025-01-13"
)
public enum PropertyType {

    TYPE("type"),
    POINT("point"),
    HAS_MOVED("hasMoved"),
    DISTANCE_MOVED("lastDistanceMoved"),
    COLOUR("colour"),
    CODE("code");

    private final String display;

    PropertyType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return this.display;
    }

    public static PropertyType fromString(String string) {
        for (PropertyType e : PropertyType.values()) {
            if (e.display.equalsIgnoreCase(string)) {
                return e;
            }
        }
        return null;
    }
}
