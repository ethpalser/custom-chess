package com.ethpalser.chess.game.event;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

public class MoveEvent implements GameEvent {

    private final Coordinate source;
    private final Coordinate target;

    public MoveEvent(Coordinate source, Coordinate target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("One or more constructor arguments are null. None can be null.");
        }
        this.source = source;
        this.target = target;
    }

    @Override
    public EventType type() {
        return EventType.MOVE;
    }

    @Override
    public void execute(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        if (context.getBoard().rejects(this.source) || context.getBoard().rejects(this.target)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (context.getBoard().get(this.source) == null) {
            throw new IllegalActionException("The piece to move does not exist");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog();

        Piece piece = board.get(this.source);
        // The turn player should always match the acting piece, and this piece should always be from the first change
        Colour turnPlayer = piece.getColour();
        ThreatMap oppThreats = Colour.WHITE.equals(turnPlayer) ? context.getThreats(Colour.BLACK) :
                context.getThreats(Colour.WHITE);

        // Todo: simplify getting moves and change followUp to be another Movement
        Movement move = piece.getMoves(board, log, oppThreats).getMove((Point) this.target);
        if (!piece.canMove(board, log, oppThreats, (Point) this.target)) {
            throw new IllegalActionException("piece (" + piece + ") cannot move to " + target);
        }
        ChessLogEntry logEntry = new ChessLogEntry((Point) this.source, (Point) this.target, piece,
                board.get(this.target),
                move.getFollowUpMove());
        // Update the board reference with all movements, which should not modify the context yet
        board.remove(this.target);
        board.remove(this.source);
        board.add(this.target, piece);
        piece.move(this.target);

        LogEntry<Coordinate, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getStartObject();
            board.remove(followUp.getStart());
            if (followUp.getEnd() != null) {
                board.add(followUp.getEnd(), toForcePush);
            }
        }
        board.remove(null);

        log.push(logEntry);
        // Commit this change to the game
        context.update(turnPlayer, board, log);
    }

    @Override
    public void unExecute(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        if (context.getBoard().rejects(this.source) || context.getBoard().rejects(this.target)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (context.getBoard().get(this.target) == null) {
            throw new IllegalActionException("The piece to move does not exist");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog();

        Piece piece = board.get(this.source);
        // The turn player should always match the acting piece, and this piece should always be from the first change
        Colour turnPlayer = piece.getColour();
        ThreatMap oppThreats = Colour.WHITE.equals(turnPlayer) ? context.getThreats(Colour.BLACK) :
                context.getThreats(Colour.WHITE);

        // Reverse the followup first
        Movement move = piece.getMoves(board, log, oppThreats).getMove((Point) this.target);
        LogEntry<Coordinate, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getEndObject();
            board.remove(followUp.getEnd());
            board.add(followUp.getStart(), toForcePush);
        }
        board.remove(null);

        // Update the board reference with all movements, which should not modify the context yet
        board.remove(this.target);
        board.remove(this.source);
        board.add(this.source, piece);
        piece.move(this.source);
        log.pop();
        // Commit this change to the game
        context.update(turnPlayer, board, log);
    }
}
