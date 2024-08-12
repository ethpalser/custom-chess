package com.ethpalser.chess.game;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.custom.movement.Movement;
import com.ethpalser.chess.piece.custom.movement.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomGame {

    private final CustomBoard board;
    private Colour turn;
    private Colour winner;
    private boolean isComplete;

    public CustomGame() {
        this.board = new CustomBoard();
        this.turn = Colour.WHITE;
        this.winner = null;
        this.isComplete = false;
    }

    public CustomGame(CustomBoard board, Colour turn) {
        this.board = board;
        this.turn = turn;
    }

    public CustomBoard getBoard() {
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
        Point start = action.getStart();
        Point end = action.getEnd();
        if (!this.turn.equals(player)) {
            throw new IllegalActionException("Acting player is not the turn player!");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Action does not have both a start and end.");
        }
        if (!board.isValidLocation(start) || !board.isValidLocation(end)) {
            throw new IndexOutOfBoundsException("Vector arguments out of board bounds.");
        }

        CustomPiece toMove = this.getPieceToMove(player, start);
        this.verifyDestination(toMove, end);
        Movement movement = toMove.getMovement(this.board, end);
        if (movement == null) {
            throw new IllegalActionException("The selected piece does not have a movement to " + end);
        }
        this.performMovement(player, movement, start, end);
        if (this.isCheckmate()) {
            this.winner = this.turn;
            this.isComplete = true;
        } else if (this.isStalemate()) {
            this.isComplete = true;
        }
        this.turn = turn.equals(Colour.BLACK) ? Colour.WHITE : Colour.BLACK;
    }

    private CustomPiece getPieceToMove(Colour player, Point start) {
        if (player == null || start == null) {
            throw new NullPointerException();
        }
        CustomPiece customPiece = this.board.getPiece(start);
        if (customPiece == null) {
            throw new IllegalActionException("The piece at " + start + " does not exist.");
        } else if (!player.equals(customPiece.getColour())) {
            throw new IllegalActionException("The piece at " + start + " is not the current player's piece!");
        }
        return customPiece;
    }

    private void verifyDestination(CustomPiece selected, Point end) {
        if (selected == null || end == null) {
            throw new NullPointerException();
        }
        CustomPiece destination = this.board.getPiece(end);
        if (selected.getPosition().equals(end)) {
            throw new IllegalActionException("The destination " + end + " is the selected piece's current location.");
        } else if (destination != null && selected.getColour().equals(destination.getColour())) {
            throw new IllegalActionException("The destination " + end + " is occupied by a piece of the same colour.");
        }
    }

    private void performMovement(Colour player, Movement movement, Point start, Point end) {
        if (player == null || movement == null || start == null || end == null) {
            throw new NullPointerException();
        }
        this.board.movePiece(start, end);

        // If the movement has an extra action, perform it
        if (movement.getExtraAction() != null) {
            Action action = movement.getExtraAction().getAction(this.board, new Action(player, start, end));
            CustomPiece toForceMove = this.board.getPiece(action.getStart());
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
        CustomPiece king = this.board.getKing(this.getTurnOppColour());
        Set<Point> kingMoves = king.getMovementSet(king.getPosition(), this.getBoard());
        if (!kingMoves.isEmpty()) {
            for (Point v : kingMoves) {
                if (this.board.getLocationThreats(v, this.getTurnColour()).isEmpty()) {
                    // King can move to a location that is not threatened by an opponent's piece
                    return false;
                }
            }
        }

        Point kingPosition = king.getPosition();
        for (CustomPiece p : this.board.getPiecesCausingCheck(this.getTurnOppColour())) {
            List<CustomPiece> attackers = this.board.getLocationThreats(p.getPosition(), this.getTurnOppColour());
            for (CustomPiece a : attackers) {
                // An opponent piece can move to prevent checkmate by attacking this threatening piece
                if (a != null && !a.getColour().equals(this.getTurnColour())
                        && (!PieceType.KING.equals(a.getType()) || PieceType.KING.equals(a.getType())
                        && this.board.getLocationThreats(p.getPosition(), this.getTurnColour()).isEmpty())) {
                    return false;
                }
            }

            Movement pMove = p.getMovement(this.board, kingPosition);
            Path pPath = pMove.getPath(this.getTurnColour(), p.getPosition(), kingPosition, this.board);
            for (Point v : pPath) {
                List<CustomPiece> blockers =
                        this.board.getLocationThreats(v, this.getTurnOppColour()).stream().filter(piece -> !piece.getType().equals(PieceType.KING))
                                .collect(Collectors.toList());
                for (CustomPiece b : blockers) {
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
        List<CustomPiece> allCustomPieces = this.board.getPieces();
        if (allCustomPieces.size() <= 2) {
            return true;
        }

        CustomPiece king = this.board.getKing(this.getTurnOppColour());
        Set<Point> kingMoves = king.getMovementSet(king.getPosition(), this.getBoard());
        if (!kingMoves.isEmpty()) {
            for (Point v : kingMoves) {
                if (this.board.getLocationThreats(v, this.getTurnColour()).isEmpty()) {
                    // King can move to a location that is not threatened by an opponent's piece
                    return false;
                }
            }
        }

        List<CustomPiece> playerCustomPieces =
                allCustomPieces.stream().filter(p -> this.getTurnOppColour().equals(p.getColour())
                && !p.getType().equals(PieceType.KING)).collect(Collectors.toList());
        for (CustomPiece p : playerCustomPieces) {
            Set<Point> moves = p.getMovementSet(p.getPosition(), board);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
