package com.chess.api.game;

import com.chess.api.game.exception.IllegalActionException;
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

    public void executeAction(@NonNull Action action) {
        Colour player = action.colour();
        Vector2D start = action.start();
        Vector2D end = action.end();
        if (!this.turn.equals(player)) {
            throw new IllegalActionException("Acting player is not the turn player!");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Action does not have both a start and end.");
        }
        if (!start.isValid() || !end.isValid()) {
            throw new IndexOutOfBoundsException("Vector arguments out of board bounds.");
        }

        Piece toMove = this.getPieceToMove(player, start);
        this.verifyDestination(toMove, end);
        Movement movement = this.getMovement(toMove, end);
        this.performMovement(player, movement, start, end);
        if ((this.turn.equals(Colour.WHITE) && this.board.getKingCheck(Colour.BLACK))
                || (this.turn.equals(Colour.BLACK) && this.board.getKingCheck(Colour.WHITE))) {
            // Todo: Determine if checkmate
        }
        this.turn = turn.equals(Colour.BLACK) ? Colour.WHITE : Colour.BLACK;
    }

    private Piece getPieceToMove(@NonNull Colour player, @NonNull Vector2D start) {
        Piece piece = this.board.getPiece(start);
        if (piece == null) {
            throw new IllegalActionException("The piece at " + start + " does not exist.");
        } else if (!player.equals(piece.getColour())) {
            throw new IllegalActionException("The piece at " + start + " is not the current player's piece!");
        }
        return piece;
    }

    private void verifyDestination(@NonNull Piece selected, @NonNull Vector2D end) {
        Piece destination = this.board.getPiece(end);
        if (selected.getPosition().equals(end)) {
            throw new IllegalActionException("The destination " + end + " is the selected piece's current location.");
        } else if (destination != null && selected.getColour().equals(destination.getColour())) {
            throw new IllegalActionException("The destination " + end + " is occupied by a piece of the same colour.");
        }
    }

    private Movement getMovement(@NonNull Piece selected, @NonNull Vector2D end) {
        Movement movement = selected.getMovement(this.board, end);
        if (movement == null) {
            throw new IllegalActionException("The selected piece does not have a movement to " + end);
        }
        return movement;
    }

    private void performMovement(@NonNull Colour player, @NonNull Movement movement, @NonNull Vector2D start,
            @NonNull Vector2D end) {
        this.board.movePiece(start, end);

        // If the movement has an extra action, perform it
        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction()
                    .getAction(this.board, new Action(player, start, end));
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
