package com.ethpalser.chess.piece.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.reference.Reference;
import java.util.List;

public class ReferenceCondition implements Conditional {

    private final Reference target;
    private final Comparator comparator;
    private final Reference expected;

    public ReferenceCondition(Reference target, Comparator comparator, Reference expected) {
        if (expected == null && !Comparator.canReferenceSelf(comparator)) {
            throw new IllegalArgumentException("Cannot use a Comparator that requires an expected value.");
        }
        this.target = target;
        this.comparator = comparator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Board board, Action action) {
        List<CustomPiece> customPieces = this.target.getPieces(board, action);
        switch (this.comparator) {
            case FALSE, DOES_NOT_EXIST -> {
                return customPieces == null || customPieces.isEmpty();
            }
            case TRUE, EXIST -> {
                return customPieces != null;
            }
            case EQUAL -> {
                List<CustomPiece> expectedCustomPieces = this.expected.getPieces(board, action);
                for (CustomPiece customPiece : customPieces) {
                    if (!expectedCustomPieces.contains(customPiece))
                        return false;
                }
                return true;
            }
            case NOT_EQUAL -> {
                List<CustomPiece> expectedCustomPieces = this.expected.getPieces(board, action);
                for (CustomPiece customPiece : customPieces) {
                    if (!expectedCustomPieces.contains(customPiece))
                        return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "ReferenceCondition{" +
                "target=" + target +
                ", comparator=" + comparator +
                ", expected=" + expected +
                '}';
    }
}
