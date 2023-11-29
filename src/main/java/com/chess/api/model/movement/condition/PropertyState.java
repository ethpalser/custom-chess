package com.chess.api.model.movement.condition;

public enum PropertyState {
    FALSE,
    TRUE,
    EQUAL,
    OPPOSITE,
    DOES_NOT_EXIST, // This means the field does not exist. Null means the field exists but has no value.
}
