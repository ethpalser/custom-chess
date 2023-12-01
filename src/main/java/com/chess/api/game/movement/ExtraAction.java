package com.chess.api.game.movement;

import com.chess.api.game.Board;
import com.chess.api.game.Vector2D;
import com.chess.api.game.piece.Piece;
import com.chess.api.game.reference.Reference;
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
