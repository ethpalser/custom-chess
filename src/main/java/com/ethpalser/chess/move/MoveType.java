package com.ethpalser.chess.move;

public enum MoveType {
    JUMP("jump"),
    ADVANCE("advance"),
    CHARGE("charge");

    private final String display;

    MoveType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return this.display;
    }

    public static MoveType fromString(String string) {
        for (MoveType e : MoveType.values()) {
            if (e.display.equalsIgnoreCase(string)) {
                return e;
            }
        }
        return ADVANCE;
    }

}
