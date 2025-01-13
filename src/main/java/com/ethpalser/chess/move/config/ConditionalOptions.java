package com.ethpalser.chess.move.config;

import com.ethpalser.chess.condition.Operator;
import com.ethpalser.chess.condition.reflection.PropertyType;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Direction;

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

    public static ConditionalOptions pathState(Reference start, Reference end, Operator operator, Object expected) {
        return new ConditionalOptions(Type.BOARD, start, null, operator, expected, end);
    }

    // Specific Conditional Options

    public static ConditionalOptions refNotMoved(Reference ref) {
        return ConditionalOptions.pieceState(ref, PropertyType.HAS_MOVED, Operator.FALSE, false);
    }

    public static ConditionalOptions refIsType(Reference ref, PieceType pieceType) {
        return ConditionalOptions.pieceState(ref, PropertyType.CODE, Operator.EQUAL, pieceType.toCode());
    }

    public static ConditionalOptions refAtDirection(Reference ref, Direction direction, int distance) {
        return ConditionalOptions.pieceCompare(ref, Operator.EQUAL,
                new Reference(Reference.Location.POINT, direction, distance));
    }

    public static ConditionalOptions pathIsEmpty(Reference start, Reference end) {
        return ConditionalOptions.pathState(start, end, Operator.FALSE, null);
    }

    public static ConditionalOptions lastMovedDistance(int distance) {
        return ConditionalOptions.gameHistory(PropertyType.DISTANCE_MOVED, Operator.EQUAL, distance);
    }

}
