package com.ethpalser.chess.move.custom.condition;

public enum PropertyType {

    TYPE("type"),
    POINT("point"),
    HAS_MOVED("hasMoved"),
    DISTANCE_MOVED("lastDistanceMoved"),
    COLOUR("colour");

    private final String label;

    PropertyType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
