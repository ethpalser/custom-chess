package com.chess.api.model.movement;

import com.chess.api.model.Action;
import com.chess.api.model.Board;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.Piece;
import lombok.NonNull;

public class ExtraAction {

    private final Reference reference;
    private final Vector2D destination;

    public ExtraAction() {
        this.destination = null;
        this.reference = null;
    }

    public ExtraAction(Reference reference, Vector2D destination) {
        this.reference = reference;
        this.destination = destination;
    }

    public Action getAction(@NonNull Board board, @NonNull Action previousAction) {
        if (this.reference == null) {
            return null;
        }
        Piece piece = this.reference.getPieces(board, previousAction).get(0);
        return new Action(piece.getColour(), piece.getPosition(), this.destination);
    }

}
