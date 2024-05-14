package com.chess.game.movement;

import com.chess.game.Board;
import com.chess.game.Vector2D;
import com.chess.game.piece.Piece;
import com.chess.game.reference.Reference;

/**
 * Container of a follow-up movement with a reference to where the piece is relative to the previous action and
 * Vector2D of where it will go.
 */
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

    /**
     * Creates an Action using a previous action and this reference for the board to consume for another movement.
     *
     * @param board          {@link Board} needed for {@link Reference} to refer to
     * @param previousAction {@link Action} that this ExtraAction is following-up on
     * @return {@link Action}
     */
    public Action getAction(Board board, Action previousAction) {
        if (board == null || previousAction == null) {
            throw new NullPointerException();
        }
        if (this.reference == null) {
            return null;
        }
        Piece piece = this.reference.getPieces(board, previousAction).get(0);
        return new Action(piece.getColour(), piece.getPosition(), this.destination);
    }

}
