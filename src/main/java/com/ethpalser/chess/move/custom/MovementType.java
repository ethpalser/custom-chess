package com.ethpalser.chess.move.custom;

public enum MovementType {
    JUMP("jump"),
    ADVANCE("advance"),
    CHARGE("charge");

    private final String display;

    MovementType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return this.display;
    }

    public static MovementType fromString(String string) {
        for (MovementType e : MovementType.values()) {
            if (e.display.equalsIgnoreCase(string)) {
                return e;
            }
        }
        return ADVANCE;
    }

}
