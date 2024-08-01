package com.ethpalser.chess.piece.custom.condition;

public enum Comparator {
    FALSE,
    TRUE,
    EQUAL,
    NOT_EQUAL,
    EXIST,
    DOES_NOT_EXIST;

    /**
     * This uses the given Comparator to return if it is allowed for Conditionals that only have one Reference, or
     * that this only requires the Reference itself.
     *
     * @param comparator {@link Comparator}
     * @return boolean
     */
    public static boolean canReferenceSelf(Comparator comparator) {
        return switch (comparator) {
            case FALSE, TRUE, EXIST, DOES_NOT_EXIST -> true;
            default -> false;
        };
    }
}
