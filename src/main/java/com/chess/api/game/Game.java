package com.chess.api.game;

import com.chess.api.game.exception.IllegalActionException;
import com.chess.api.game.movement.Action;
import com.chess.api.game.movement.Movement;
import com.chess.api.game.movement.Path;
import com.chess.api.game.piece.Piece;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Game {

    private final Board board;
    private Colour turn;
    private Colour winner;

    public Game() {
        this.board = new Board();
        this.turn = Colour.WHITE;
        this.winner = null;
    }

    public Colour getTurnColour() {
        return this.turn;
    }

    public Colour getTurnOppColour() {
        return Colour.WHITE.equals(this.turn) ? Colour.WHITE : Colour.BLACK;
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
        if (this.isCheckmate()) {
            this.winner = this.turn;
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

    private boolean isCheckmate() {
        Piece king = this.board.getKing(this.getTurnOppColour());
        Set<Vector2D> kingMoves = king.getMovementSet(king.getPosition(), this.getBoard());
        if (!kingMoves.isEmpty()) {
            return false;
        }

        Vector2D kingPosition = king.getPosition();
        for (Piece p : this.board.getPiecesCausingCheck(this.getTurnColour())) {
            List<Piece> attackers = this.board.getLocationThreats(p.getPosition());
            for (Piece a : attackers) {
                // An opponent piece can move to prevent checkmate by attacking this threatening piece
                if (a != null && !a.getColour().equals(this.getTurnColour())){
                    return false;
                }
            }

            Movement pMove = this.getMovement(p, kingPosition);
            Path pPath = pMove.getPath(this.getTurnColour(), p.getPosition(), kingPosition);
            for (Vector2D v : pPath) {
                List<Piece> blockers = this.board.getLocationThreats(v);
                for (Piece b : blockers) {
                    // An opponent piece can move to prevent checkmate by blocking
                    if (!this.turn.equals(b.getColour())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
