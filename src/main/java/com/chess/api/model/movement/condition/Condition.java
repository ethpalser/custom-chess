package com.chess.api.model.movement.condition;

import com.chess.api.model.Board;
import com.chess.api.model.Coordinate;
import com.chess.api.model.piece.Piece;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Condition {

    private final Reference reference;
    private final Property<Piece> property;
    private final PropertyState propertyState;
    private final Object expected;

    public Condition() {
        this(new Reference(), new Property<>(null), null, null);
    }

    public Condition(Reference reference, Property<Piece> property, PropertyState propertyState, Object expected) {
        this.reference = reference;
        this.property = property;
        this.propertyState = propertyState;
        this.expected = expected;
    }

    public Boolean evaluate(Board board, Coordinate start, Coordinate end) {
        List<Piece> list = this.getReferencePiece(board, start, end);
        Iterator<Piece> iterator = list.iterator();
        boolean result = true;

        while (result && iterator.hasNext()) {
            Piece piece = iterator.next();
            if (PropertyState.DOES_NOT_EXIST.equals(this.propertyState) && piece != null) {
                return false;
            } else if (piece == null) {
                continue;
            }

            Object propVal = this.property.fetch(piece);
            switch (this.propertyState) {
                case TRUE -> result = Boolean.TRUE.equals(propVal);
                case FALSE -> result = Boolean.FALSE.equals(propVal);
                case EQUAL -> result = (this.expected == null && propVal == null) ||
                        this.expected != null && propVal != null
                                && propVal.getClass().equals(this.expected.getClass())
                                && propVal.equals(this.expected);
                case OPPOSITE -> {
                    // The start piece is implied for OPPOSITE, as the condition's value is not known until runtime
                    Piece currPiece = board.getPiece(start);
                    Object currVal = this.property.fetch(currPiece);
                    result = (currVal == null && propVal == null) ||
                            currVal != null && propVal != null
                                    && propVal.getClass().equals(currVal.getClass())
                                    && propVal.equals(currVal);
                }
                default -> {
                    return false;
                }
            }
        }
        return result;
    }

    private List<Piece> getReferencePiece(Board board, Coordinate start, Coordinate end) {
        List<Piece> pieces = new ArrayList<>();
        switch (reference.location()) {
            case LAST_MOVED -> pieces.add(board.getLastMoved());
            case AT_START -> pieces.add(board.getPiece(start));
            case AT_DESTINATION -> pieces.add(board.getPiece(end));
            case AT_COORDINATE -> pieces.add(board.getPiece(reference.coordinate()));
            case PATH_TO_DESTINATION -> pieces = board.getPieces(start, end);
            case PATH_TO_COORDINATE -> pieces = board.getPieces(start, reference.coordinate());
        }
        return pieces;
    }
}

