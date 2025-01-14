package com.ethpalser.chess.condition;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-22",
        majorVersion = 1,
        minorVersion = 3,
        lastModified = "2025-01-13"
)
public enum Operator {
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

    public static Operator fromString(String string) {
        for (Operator c : Operator.values()) {
            if (c.toString().equalsIgnoreCase(string)) {
                return c;
            }
        }
        return EQUAL;
    }
}
