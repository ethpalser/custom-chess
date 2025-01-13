package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.exception.CoordinateOutOfBoundsException;
import com.ethpalser.chess.exception.IllegalMoveException;
import com.ethpalser.chess.exception.MissingPieceException;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.game.context.ChessLog;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import java.util.List;

public class MoveEvent implements GameEvent {

    private final Colour player;
    private final Coordinate source;
    private final Coordinate target;

    public MoveEvent(Colour player, Coordinate source, Coordinate target) {
        if (player == null || source == null || target == null) {
            throw new IllegalArgumentException("One or more constructor arguments are null. None can be null.");
        }
        this.player = player;
        this.source = source;
        this.target = target;
    }

    @Override
    public Colour player() {
        return this.player;
    }

    @Override
    public String choice() {
        return this.target.toString();
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
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record contextRecord = context.toRecord();
        Board<Coordinate> board = contextRecord.getBoard();
        ChessLog log = contextRecord.getLog();

        if (board.rejects(this.source) || board.rejects(this.target)) {
            throw new CoordinateOutOfBoundsException();
        }
        if (board.get(this.source) == null) {
            throw new MissingPieceException("Missing piece at " + this.source + " cannot move to " + this.target);
        }

        Piece moving = board.get(this.source);
        MoveSet moveSet = moving.getMoves(contextRecord);
        Move move = moveSet.getMove(this.target);
        if (move == null) {
            throw new IllegalMoveException("Piece (" + moving + ") cannot move to " + this.target);
        }
        // Create this record before applying updates, as movement info is needed prior to the change (for un-execute)
        Piece captured = board.get(this.target);
        ChessRecord chessRecord = (new ChessRecord.Builder(this.source, this.target, moving, captured)).build();

        // Update board
        board.remove(this.source);
        board.remove(this.target);
        board.add(this.target, moving);
        moving.move(this.target);

        // Update log
        ChessNotation notation = new ChessNotation(chessRecord);
        log.push(new ChessLog.Entry(notation, this));

        // Raise a prompt for the current player to perform, which must happen before the turn changes
        Move.FollowUp followUp = move.followUp();
        if (followUp != null) {
            Coordinate refTarget = followUp.reference().coordinates(contextRecord, this.source).get(0);
            List<String> options = followUp.path().toList().stream().map(Coordinate::toString).toList();
            context.raisePrompt(new GamePrompt(EventType.MOVE_FOLLOW_UP, refTarget, options));
        } else if (moving.canPromote(board)) {
            context.raisePrompt(new GamePrompt(EventType.PROMOTE, this.target, moving.getPromotions()));
        }
        // Commit this change to the game
        // This should be true in every case, as base movement is only allowed by the turn player
        Colour turnPlayer = moving.getColour();
        context.update(turnPlayer, board, log);
    }

    @Override
    public void unExecute(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record contextRecord = context.toRecord();

        ChessLog log = contextRecord.getLog();
        ChessRecord rec = log.peek().notation().toRecord();
        PieceFactory factory = new CustomPieceFactory(context.getMoveSpecs());
        Piece captured;
        if (rec.targetCode() != null && rec.targetColour() != null) {
            captured = factory.create(rec.targetCode(), rec.targetColour(), rec.target());
            captured.setHasMoved(rec.targetHasMoved());
        } else {
            captured = null;
        }
        // Undo changes to board
        Board<Coordinate> board = contextRecord.getBoard();
        Piece moving = board.get(this.target);
        if (moving == null) {
            throw new IllegalStateException("Moving piece from undo is is not at its expected location");
        }

        board.remove(this.target);
        board.remove(this.source);
        board.add(this.source, moving);
        moving.move(this.source);
        moving.setHasMoved(rec.sourceHasMoved()); // Todo: Determine if using piece starts would be better
        if (captured != null) {
            board.add(this.target, captured);
        }

        // Undo changes to log
        log.pop();

        // Commit this change to the game
        Colour turnPlayer = moving.getColour();
        context.undo(turnPlayer, board, log);
    }
}
