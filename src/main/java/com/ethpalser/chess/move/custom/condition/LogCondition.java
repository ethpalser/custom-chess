package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.view.ConditionalView;

public class LogCondition<T extends Positional> implements Conditional<T> {

    private final Log<Coordinate, T> log;
    private final Comparator comparator;
    private final PropertyType propType;
    private final Object expected;

    public LogCondition(Log<Coordinate, T> log, Comparator comparator, PropertyType propType, Object expected) {
        this.log = log;
        this.comparator = comparator;
        this.propType = propType;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Board<Coordinate> plane) {
        if (this.comparator == null || this.log == null || this.log.peek() == null) {
            return false;
        }
        switch (this.propType) {
            case HAS_MOVED -> {
                return switch (this.comparator) {
                    case TRUE -> this.log.peek().isFirstOccurrence();
                    case FALSE -> !this.log.peek().isFirstOccurrence();
                    default -> false;
                };
            }
            case DISTANCE_MOVED -> {
                Point start = (Point) this.log.peek().getStart();
                Point end = (Point) this.log.peek().getEnd();
                int diff;
                if (start == null || end == null) {
                    diff = 0;
                } else {
                    diff = Math.max(
                            Math.abs(start.getX() - end.getX()),
                            Math.abs(start.getY() - end.getY())
                    );
                }

                return switch (this.comparator) {
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
