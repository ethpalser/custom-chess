package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.exception.MissingPieceException;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.game.context.ChessLog;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.space.Coordinate;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-12",
        majorVersion = 1,
        minorVersion = 4,
        lastModified = "2025-01-13"
)
public class MoveFollowUpEvent implements GameEvent {

    private final Colour player;
    private final Coordinate source;
    private final Coordinate target;

    public MoveFollowUpEvent(Colour player, Coordinate source, Coordinate target) {
        if (player == null || source == null) {
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
        return EventType.MOVE_FOLLOW_UP;
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

        if (board.rejects(this.source)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (board.get(this.source) == null) {
            throw new MissingPieceException("Missing piece at " + this.source + " cannot move to " + this.target);
        }

        /*
         * Notice:
         * The significant differences between a Move and a MoveFollowUp are when each are allowed and what they do.
         * A move:
         * - Always by the turn player, and only once per turn
         * - Verifies that it can happen on the board, the turn player's piece is moving, and it can move as requested
         * A follow-up:
         * - Always follows a move, and only once per move
         * - Verifies that it can happen on the board, but not whose piece it is nor if that piece can move as requested
         * - The follow-up was pre-verified by the move that preceded this
         */

        // Update board
        Piece moving = board.get(this.source);
        Piece captured = board.get(this.target); // Needed before update to create record

        // Create chess record first as we need to know if a piece did not move prior to the update
        ChessRecord chessRecord = new ChessRecord.Builder(this.source, this.target, moving, captured)
                .isFollowUp(true)
                .build();

        board.remove(this.source);
        // Followup can have a null target, which will remove the piece
        if (this.target != null) {
            board.remove(this.target);
            board.add(this.target, moving);
            moving.move(this.target);
        }

        ChessRecord previous = log.peek().notation().toRecord(); // Needed before update to check promotions
        // Update log
        ChessNotation notation = new ChessNotation(chessRecord);
        log.push(new ChessLog.Entry(notation, this));

        // Check for promotions. This followup may have prevented a promotion from the original move
        Piece previouslyMoved = board.get(previous.target());
        if (previouslyMoved.canPromote(board)) {
            context.raisePrompt(new GamePrompt(EventType.PROMOTE, previous.target(), previouslyMoved.getPromotions()));
        } else if (moving.canPromote(board)) {
            context.raisePrompt(new GamePrompt(EventType.PROMOTE, this.target, moving.getPromotions()));
        }
        // Commit this change to the game
        // This should be true in every case, as base movement is only allowed by the turn player
        Colour turnPlayer = previouslyMoved.getColour();
        context.update(turnPlayer, board, log);
    }

    @Override
    public void unExecute(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record contextRecord = context.toRecord();

        // Undo changes to log, this is first to access the move that caused this follow up
        ChessLog log = contextRecord.getLog();
        ChessLog.Entry followUpEntry = log.pop();

        // Undo changes to board
        Board<Coordinate> board = contextRecord.getBoard();

        PieceFactory factory = new CustomPieceFactory(context.getMoveSpecs());
        Piece moving = board.get(this.target);
        if (moving == null) { // This piece was removed
            ChessRecord rec = log.peek().notation().toRecord();
            moving = factory.create(rec.targetCode(), rec.targetColour(), this.source);
            moving.setHasMoved(true); // Could this piece not have moved?
        }

        Piece captured;
        ChessRecord rec = followUpEntry.notation().toRecord();
        if (rec.targetCode() != null && rec.targetColour() != null) {
            captured = factory.create(rec.targetCode(), rec.targetColour(), rec.target());
        } else {
            captured = null;
        }

        board.remove(this.source);
        board.add(this.source, moving);
        moving.move(this.source);
        // Followup can have a null target, which will remove the piece
        if (this.target != null) {
            board.remove(this.target);
            board.add(this.target, captured);
            if (captured != null) {
                captured.move(this.target);
                captured.setHasMoved(rec.targetHasMoved());
            }
        }

        // Todo: Determine if this should raise a prompt, or that its unnecessary when the preceding move is also undone
        // Commit this change to the game
        Colour turnPlayer = log.peek().notation().toRecord().sourceColour();
        context.update(turnPlayer, board, log);
    }
}
