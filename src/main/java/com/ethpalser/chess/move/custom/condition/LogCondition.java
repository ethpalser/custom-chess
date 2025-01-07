package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

public class LogCondition implements Conditional {

    private final Operator operator;
    private final PropertyType propType;
    private final Object expected;

    public LogCondition(Operator operator, PropertyType propType, Object expected) {
        this.operator = operator;
        this.propType = propType;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
        // Note: appliedTo is ignored, as this is only checking the context's log
        if (this.operator == null || context == null || context.getLog() == null || context.getLog().peek() == null) {
            return false;
        }
        // Todo: replace with newer log
        Log<Coordinate, Piece> log = context.getLog();
        switch (this.propType) {
            case HAS_MOVED -> {
                return switch (this.operator) {
                    case TRUE -> log.peek().isFirstOccurrence();
                    case FALSE -> !log.peek().isFirstOccurrence();
                    default -> false;
                };
            }
            case DISTANCE_MOVED -> {
                Point start = (Point) log.peek().getStart();
                Point end = (Point) log.peek().getEnd();
                int diff;
                if (start == null || end == null) {
                    diff = 0;
                } else {
                    diff = Math.max(
                            Math.abs(start.getX() - end.getX()),
                            Math.abs(start.getY() - end.getY())
                    );
                }

                return switch (this.operator) {
                    case EQUAL -> expected.equals(diff);
                    case NOT_EQUAL -> !expected.equals(diff);
                    default -> false;
                };
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public ConditionalView toView() {
        return new ConditionalView(ConditionalType.LOG, null, this.propType, this.comparator, this.expected);
    }
}
