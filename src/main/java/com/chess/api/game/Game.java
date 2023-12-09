package com.chess.api.game;

import com.chess.api.game.movement.Action;
import com.chess.api.game.movement.Movement;
import com.chess.api.game.piece.Piece;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Game {

    private final Board board;
    private Colour turn;

    public Game() {
        this.board = new Board();
        this.turn = Colour.WHITE;
    }

    /**
     * Move a piece to a new coordinate within the board.
     * <p>For a piece to move the following must be valid:</p>
     * <ol>
     * <li>The piece to move exists</li>
     * <li>The end location is not occupied by a piece with the same colour</li>
     * <li>The piece can end on that location by moving or capturing</li>
     * <li>The piece can move to that location after restrictions apply by moving or capturing</li>
     * </ol>
     *
     * @param start {@link Vector2D} location of the Piece that will be moved
     * @param end   {@link Vector2D} location that the Piece is requested to end on, if possible
     */
    public void movePiece(@NonNull Vector2D start, @NonNull Vector2D end) {
        if (!start.isValid() || !end.isValid()) {
            throw new IndexOutOfBoundsException("Vector arguments out of board bounds.");
        }
        // Check if the piece exists and the destination is valid
        Piece pStart = this.board.getPiece(start);
        Piece pEnd = this.board.getPiece(end);
        if (pStart == null || pStart.equals(pEnd) || (pEnd != null && pStart.getColour().equals(pEnd.getColour()))) {
            return;
        }
        // Get the movement if the piece can move there
        Movement movement = pStart.getMovement(this.board, end);
        if (movement == null) {
            return;
        }
        // Move the piece on the board
        this.board.setPiece(end, pStart);
        // If the movement has an extra action, perform it
        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction().getAction(this.board, new Action(pStart.getColour(), start, end));
            Piece toForceMove = this.board.getPiece(action.start());
            if (toForceMove != null) {
                if (action.end() != null) {
                    this.board.setPiece(action.end(), toForceMove);
                }
                // If the piece had not moved the intent is to remove it
                this.board.setPiece(action.start(), null);
            }
        }
        this.board.setLastMoved(pStart);
        this.turn = turn.equals(Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

}
