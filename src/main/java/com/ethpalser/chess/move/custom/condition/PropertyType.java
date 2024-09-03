package com.ethpalser.chess.move.custom.condition;

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
