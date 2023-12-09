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

        Piece piece = board.getPiece(start);
        if (piece == null || !turn.equals(piece.getColour())) {
            return;
        }

        Piece atStart = board.getPiece(start);
        Piece atEnd = board.getPiece(end);
        if (atStart == null || atStart.equals(atEnd) || (atEnd != null && atStart.getColour().equals(atEnd.getColour()))) {
            return;
        }
        Movement movement = atStart.getMovement(this.board, end);
        if (movement == null) {
            return;
        }
        this.board.setPiece(end, atStart);

        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction().getAction(this.board, new Action(atStart.getColour(), start, end));
            Piece toForceMove = board.getPiece(action.start());
            if (toForceMove != null) {
                if (action.end() != null) {
                    this.board.setPiece(action.end(), toForceMove);
                }
                // If the piece had not moved the intent is to remove it
                this.board.setPiece(action.start(), null);
            }
        }

        this.turn = turn.equals(Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

}
