package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.reference.ReferenceFactory;
import com.ethpalser.chess.view.ConditionalView;
import com.ethpalser.chess.view.ReferenceView;

public class ConditionalFactory {

    private final Board board;
    private final Log<Point, Piece> log;

    public ConditionalFactory(Board board, Log<Point, Piece> log) {
        this.board = board;
        this.log = log;
    }

    public Conditional<Piece> build(ConditionalView view) {
        if (view == null) {
            return null;
        }
        ReferenceFactory refFactory = new ReferenceFactory(this.board, this.log);

        Object expected;
        if (view.getExpected() instanceof ReferenceView) {
            expected = refFactory.build((ReferenceView) view.getExpected());
        } else {
            expected = view.getExpected();
        }

        switch (view.getType()) {
            case FIELD -> {
                return new PropertyCondition<>(refFactory.build(view.getTarget()), view.getAssertion(), view.getField(),
                        expected);
            }
            case LOG -> {
                return new LogCondition<>(this.log, view.getAssertion(), view.getField(), expected);
            }
            case PIECE -> {
                if (expected instanceof ReferenceView) {
                    return new ReferenceCondition<>(refFactory.build(view.getTarget()), view.getAssertion(),
                            refFactory.build((ReferenceView) expected));
                } else {
                    return new ReferenceCondition<>(refFactory.build(view.getTarget()), view.getAssertion(), null);
                }
            }
            default -> {
                return null;
            }
        }
    }

}
