package com.ethpalser.chess.move.custom.condition;

public enum Comparator {
    FALSE,
    TRUE,
    EQUAL,
    NOT_EQUAL;

    @Override
    public String toString() {
        return switch (this) {
            case FALSE -> "false";
            case TRUE -> "true";
            case EQUAL -> "equal";
            case NOT_EQUAL -> "not equal";
        };
    }

    public static Comparator fromString(String string) {
        for (Comparator c : Comparator.values()) {
            if (c.toString().equalsIgnoreCase(string)) {
                return c;
            }
        }
        return EQUAL;
    }
}
