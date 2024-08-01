package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.Vector2D;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.custom.movement.Movement;
import com.ethpalser.chess.piece.custom.movement.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {

    private final Board board;
    private Colour turn;
    private Colour winner;
    private boolean isComplete;

    public Game() {
        this.board = new Board();
        this.turn = Colour.WHITE;
        this.winner = null;
        this.isComplete = false;
    }

    public Game(Board board, Colour turn) {
        this.board = board;
        this.turn = turn;
    }

    public Board getBoard() {
        return this.board;
    }

    public Colour getTurnColour() {
        return this.turn;
    }

    public Colour getTurnOppColour() {
        return Colour.WHITE.equals(this.turn) ? Colour.BLACK : Colour.WHITE;
    }

    public Colour getWinner() {
        return this.winner;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void executeAction(Action action) {
        if (action == null) {
            throw new NullPointerException();
        }
        if (this.isComplete) {
            throw new IllegalActionException("Game has ended. No further moves are allowed.");
        }

        Colour player = action.getColour();
        Vector2D start = action.getStart();
        Vector2D end = action.getEnd();
        if (!this.turn.equals(player)) {
            throw new IllegalActionException("Acting player is not the turn player!");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Action does not have both a start and end.");
        }
        if (!board.isValidLocation(start) || !board.isValidLocation(end)) {
            throw new IndexOutOfBoundsException("Vector arguments out of board bounds.");
        }

        Piece toMove = this.getPieceToMove(player, start);
        this.verifyDestination(toMove, end);
        Movement movement = this.getMovement(toMove, end);
        this.performMovement(player, movement, start, end);
        if (this.isCheckmate()) {
            this.winner = this.turn;
            this.isComplete = true;
        } else if (this.isStalemate()) {
            this.isComplete = true;
        }
        this.turn = turn.equals(Colour.BLACK) ? Colour.WHITE : Colour.BLACK;
    }

    private Piece getPieceToMove(Colour player, Vector2D start) {
        if (player == null || start == null) {
            throw new NullPointerException();
        }
        Piece piece = this.board.getPiece(start);
        if (piece == null) {
            throw new IllegalActionException("The piece at " + start + " does not exist.");
        } else if (!player.equals(piece.getColour())) {
            throw new IllegalActionException("The piece at " + start + " is not the current player's piece!");
        }
        return piece;
    }

    private void verifyDestination(Piece selected, Vector2D end) {
        if (selected == null || end == null) {
            throw new NullPointerException();
        }
        Piece destination = this.board.getPiece(end);
        if (selected.getPosition().equals(end)) {
            throw new IllegalActionException("The destination " + end + " is the selected piece's current location.");
        } else if (destination != null && selected.getColour().equals(destination.getColour())) {
            throw new IllegalActionException("The destination " + end + " is occupied by a piece of the same colour.");
        }
    }

    private Movement getMovement(Piece selected, Vector2D end) {
        if (selected == null || end == null) {
            throw new NullPointerException();
        }
        Movement movement = selected.getMovement(this.board, end);
        if (movement == null) {
            throw new IllegalActionException("The selected piece does not have a movement to " + end);
        }
        return movement;
    }

    private void performMovement(Colour player, Movement movement, Vector2D start, Vector2D end) {
        if (player == null || movement == null || start == null || end == null) {
            throw new NullPointerException();
        }
        this.board.movePiece(start, end);

        // If the movement has an extra action, perform it
        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction().getAction(this.board, new Action(player, start, end));
            Piece toForceMove = this.board.getPiece(action.getStart());
            if (toForceMove != null) {
                if (action.getEnd() != null) {
                    this.board.setPiece(action.getEnd(), toForceMove);
                }
                // Remove this piece from its original location. If it did not move the intent is to capture it.
                this.board.setPiece(action.getStart(), null);
            }
        }
    }

    private boolean isCheckmate() {
        if (!this.board.getKingCheck(this.getTurnOppColour())) {
            return false;
        }
        Piece king = this.board.getKing(this.getTurnOppColour());
        Set<Vector2D> kingMoves = king.getMovementSet(king.getPosition(), this.getBoard());
        if (!kingMoves.isEmpty()) {
            for (Vector2D v : kingMoves) {
                if (this.board.getLocationThreats(v, this.getTurnColour()).isEmpty()) {
                    // King can move to a location that is not threatened by an opponent's piece
                    return false;
                }
            }
        }

        Vector2D kingPosition = king.getPosition();
        for (Piece p : this.board.getPiecesCausingCheck(this.getTurnOppColour())) {
            List<Piece> attackers = this.board.getLocationThreats(p.getPosition(), this.getTurnOppColour());
            for (Piece a : attackers) {
                // An opponent piece can move to prevent checkmate by attacking this threatening piece
                if (a != null && !a.getColour().equals(this.getTurnColour())
                        && (!PieceType.KING.equals(a.getType()) || PieceType.KING.equals(a.getType())
                        && this.board.getLocationThreats(p.getPosition(), this.getTurnColour()).isEmpty())) {
                    return false;
                }
            }

            Movement pMove = this.getMovement(p, kingPosition);
            Path pPath = pMove.getPath(this.getTurnColour(), p.getPosition(), kingPosition, this.board);
            for (Vector2D v : pPath) {
                List<Piece> blockers =
                        this.board.getLocationThreats(v, this.getTurnOppColour()).stream().filter(piece -> !piece.getType().equals(PieceType.KING))
                                .collect(Collectors.toList());
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

    private boolean isStalemate() {
        List<Piece> allPieces = this.board.getPieces();
        if (allPieces.size() <= 2) {
            return true;
        }

        Piece king = this.board.getKing(this.getTurnOppColour());
        Set<Vector2D> kingMoves = king.getMovementSet(king.getPosition(), this.getBoard());
        if (!kingMoves.isEmpty()) {
            for (Vector2D v : kingMoves) {
                if (this.board.getLocationThreats(v, this.getTurnColour()).isEmpty()) {
                    // King can move to a location that is not threatened by an opponent's piece
                    return false;
                }
            }
        }

        List<Piece> playerPieces = allPieces.stream().filter(p -> this.getTurnOppColour().equals(p.getColour())
                && !p.getType().equals(PieceType.KING)).collect(Collectors.toList());
        for (Piece p : playerPieces) {
            Set<Vector2D> moves = p.getMovementSet(p.getPosition(), board);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
