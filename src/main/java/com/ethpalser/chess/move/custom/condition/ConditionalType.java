package com.ethpalser.chess.move.custom.condition;

public enum ConditionalType {

    PIECE("piece"),
    FIELD("field"),
    LOG("log");

    private final String display;

    ConditionalType(String string) {
        this.display = string;
    }

    @Override
    public String toString() {
        return this.display;
    }

    public static ConditionalType fromString(String string) {
        for (ConditionalType e : ConditionalType.values()) {
            if (e.display.equals(string)) {
                return e;
            }
        }
        return null;
    }

}
