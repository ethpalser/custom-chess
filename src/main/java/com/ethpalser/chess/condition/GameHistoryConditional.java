package com.ethpalser.chess.condition;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Space;

public class GameHistoryConditional implements Conditional {

    private final Operator operator;
    private final PropertyType propType;
    private final Object expected;

    public GameHistoryConditional(Operator operator, PropertyType propType, Object expected) {
        if (propType == null || operator == null) {
            throw new IllegalArgumentException("at least one argument is null of: property type or operator");
        }
        this.operator = operator;
        this.propType = propType;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
        // Note: appliedTo is ignored, as this is only checking the context's log
        if (context == null || context.getLog() == null || context.getLog().peek() == null) {
            return false;
        }
        ChessLog log = context.getLog();
        if (this.propType == PropertyType.DISTANCE_MOVED) {
            if (log.peek() == null) {
                return false;
            }
            ChessRecord rec = log.peek().notation().toRecord();
            Coordinate start = rec.source();
            Coordinate end = rec.target();
            int diff;
            if (start == null || end == null) {
                diff = 0;
            } else {
                diff = Math.abs(start.getValue(Space.AXIS.X) - end.getValue(Space.AXIS.X)) +
                        Math.abs(start.getValue(Space.AXIS.Y) - end.getValue(Space.AXIS.Y));
            }

            return switch (this.operator) {
                case EQUAL -> expected.equals(diff);
                case NOT_EQUAL -> !expected.equals(diff);
                default -> false;
            };
        }
        return false;
    }
}
