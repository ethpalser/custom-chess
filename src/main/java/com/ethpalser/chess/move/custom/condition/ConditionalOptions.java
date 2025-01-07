package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.Reference;

public record ConditionalOptions(ConditionalOptions.Type type, Reference primary, PropertyType property,
                                 Operator operator, Object expected, Reference optional) {
    public enum Type {PIECE, BOARD, LOG}

    public ConditionalOptions {
        if (type == null || primary == null || operator == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "ConditionalSpec{" +
                "type=" + type +
                ", primaryReference=" + primary +
                ", propertyType=" + property +
                ", comparator=" + operator +
                ", expected=" + expected +
                ", optionalReference=" + optional +
                '}';
    }

    // Named "Constructors" that map to Conditional implementations and expected to be created by the ConditionalFactory

    public static ConditionalOptions gameHistory(PropertyType property, Operator operator, Object expected) {
        return new ConditionalOptions(Type.LOG, new Reference(), property, operator, expected, null);
    }

    public static ConditionalOptions pieceCompare(Reference primary, Operator operator, Reference reference) {
        return new ConditionalOptions(Type.PIECE, primary, null, operator, null, reference);
    }

    public static ConditionalOptions pieceState(Reference primary, PropertyType property, Operator operator,
            Object expected) {
        return new ConditionalOptions(Type.PIECE, primary, property, operator, expected, null);
    }
}
