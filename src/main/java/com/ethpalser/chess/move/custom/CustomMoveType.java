package com.ethpalser.chess.move.custom;

public enum CustomMoveType {
    JUMP("jump"),
    ADVANCE("advance"),
    CHARGE("charge");

    private final String display;

    CustomMoveType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return this.display;
    }

    public static CustomMoveType fromString(String string) {
        for (CustomMoveType e : CustomMoveType.values()) {
            if (e.display.equalsIgnoreCase(string)) {
                return e;
            }
        }
        return ADVANCE;
    }

}
