package com.chess.api.game;

import com.chess.api.game.exception.IllegalActionException;
import com.chess.api.game.movement.Action;
import com.chess.api.game.movement.Movement;
import com.chess.api.game.piece.Piece;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Game {

    private final Board board;
    private final Map<Vector2D, Set<Piece>> threats;
    private Colour turn;
    private boolean whiteInCheck;
    private boolean blackInCheck;

    public Game() {
        this.board = new Board();
        this.threats = new HashMap<>();
        this.turn = Colour.WHITE;
        this.whiteInCheck = false;
        this.blackInCheck = false;
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
        Piece pStart = this.getPieceToMove(start);
        this.verifyDestination(pStart, end);

        Movement movement = this.getMovement(pStart, end);
        this.performMovement(pStart, movement, start, end);

        this.board.setLastMoved(pStart);
        this.turn = turn.equals(Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    private Piece getPieceToMove(@NonNull Vector2D start) {
        Piece piece = this.board.getPiece(start);
        if (piece == null) {
            throw new IllegalActionException("The piece at " + start + " does not exist.");
        }
        return piece;
    }

    private void verifyDestination(@NonNull Piece selected, @NonNull Vector2D dest) {
        Piece destination = this.board.getPiece(dest);
        if (selected.equals(destination)) {
            throw new IllegalActionException("The destination " + dest + " is the selected piece's current location.");
        } else if (destination != null && selected.getColour().equals(destination.getColour())) {
            throw new IllegalActionException("The destination " + dest + " is occupied by a piece of the same colour.");
        }
    }

    private Movement getMovement(@NonNull Piece selected, @NonNull Vector2D dest) {
        Movement movement = selected.getMovement(this.board, dest);
        if (movement == null) {
            throw new IllegalActionException("The selected piece does not have a movement to " + dest);
        }
        return movement;
    }

    private void performMovement(@NonNull Piece selected, @NonNull Movement movement, @NonNull Vector2D start,
            @NonNull Vector2D dest) {
        this.board.setPiece(dest, selected);

        // If the movement has an extra action, perform it
        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction()
                    .getAction(this.board, new Action(selected.getColour(), start, dest));
            Piece toForceMove = this.board.getPiece(action.start());
            if (toForceMove != null) {
                if (action.end() != null) {
                    this.board.setPiece(action.end(), toForceMove);
                }
                // Remove this piece from its original location. If it did not move the intent is to capture it.
                this.board.setPiece(action.start(), null);
            }
        }
    }

}
