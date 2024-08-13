package com.ethpalser.chess.move.custom;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.reference.RelativeReference;
import com.ethpalser.chess.space.Point;

/**
 * Container of a follow-up movement with a reference to where the piece is relative to the previous action and
 * Vector2D of where it will go.
 */
public class ExtraAction {

    private final RelativeReference reference;
    private final Point destination;

    public ExtraAction() {
        this.destination = null;
        this.reference = null;
    }

    public ExtraAction(RelativeReference reference, Point destination) {
        this.reference = reference;
        this.destination = destination;
    }

    /**
     * Creates an Action using a previous action and this reference for the board to consume for another movement.
     *
     * @param board          {@link CustomBoard} needed for {@link RelativeReference} to refer to
     * @param previousAction {@link Action} that this ExtraAction is following-up on
     * @return {@link Action}
     */
    public Action getAction(CustomBoard board, Action previousAction) {
        if (board == null || previousAction == null) {
            throw new NullPointerException();
        }
        if (this.reference == null) {
            return null;
        }
        Piece customPiece = this.reference.getPieces(board, previousAction).get(0);
        return new Action(customPiece.getColour(), customPiece.getPoint(), this.destination);
    }

}
