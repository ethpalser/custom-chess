package com.chess.api.game.condition;

public enum Comparator {
    FALSE,
    TRUE,
    EQUAL,
    NOT_EQUAL,
    EXIST,
    DOES_NOT_EXIST;

    public static boolean canReferenceSelf(Comparator comparator) {
        return switch (comparator) {
            case FALSE, TRUE, EXIST, DOES_NOT_EXIST -> true;
            default -> false;
        };
    }
}
