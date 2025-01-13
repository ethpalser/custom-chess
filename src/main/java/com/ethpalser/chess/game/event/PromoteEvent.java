package com.ethpalser.chess.game.event;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.Map;

public class PromoteEvent implements GameEvent {

    private final Colour player;
    private final Coordinate source;
    private final String promoteCode;

    public PromoteEvent(Colour player, Coordinate source, String pieceCode) {
        if (player == null || source == null || pieceCode == null) {
            throw new IllegalArgumentException();
        }
        this.player = player;
        this.source = source;
        this.promoteCode = pieceCode;
    }

    @Override
    public Colour player() {
        return this.player;
    }

    @Override
    public String choice() {
        return this.promoteCode;
    }

    @Override
    public EventType type() {
        return EventType.PROMOTE;
    }

    @Override
    public void execute(GameContext context) {
        this.verifyPieceExists(context, this.source);
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record ctxRecord = context.toRecord();
        // Update the board
        Board<Coordinate> board = ctxRecord.getBoard();
        Piece replacement = this.updatePieceType(this.promoteCode, board);
        board.add(this.source, replacement);
        // Update the log
        ChessLog log = ctxRecord.getLog();
        ChessRecord.Builder rb = new ChessRecord.Builder()
                .original(log.peek().notation().toRecord())
                .promoteCode(this.promoteCode);
        log.push(new ChessLog.Entry(new ChessNotation(rb.build()), this));
        // Commit this change to the game
        context.update(replacement.getColour(), board, log);
    }

    @Override
    public void unExecute(GameContext context) {
        this.verifyPieceExists(context, this.source);
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record ctxRecord = context.toRecord();
        Board<Coordinate> board = ctxRecord.getBoard();
        ChessLog log = ctxRecord.getLog();
        // Undo change to board
        String oldCode = log.peek().notation().toRecord().sourceCode();
        Piece replacement = this.updatePieceType(oldCode, board);
        board.add(this.source, replacement);
        // Undo change to log
        log.pop();
        // Commit this change to the game
        context.undo(replacement.getColour(), board, log);
    }

    private void verifyPieceExists(GameContext context, Coordinate coordinate)
            throws IllegalActionException, IndexOutOfBoundsException {
        if (context.getBoard().rejects(coordinate)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (context.getBoard().get(coordinate) == null) {
            throw new IllegalActionException("The piece to move does not exist");
        }
    }

    private Piece updatePieceType(String code, Board<Coordinate> board) {
        // Manually modify piece's string then convert it into a piece
        String pieceStr = Pieces.asString(board.get(this.source), code);
        Piece replacement;
        if (PieceType.fromCode(code) == PieceType.CUSTOM) {
            // CustomPieceFactory should load custom piece specifications to determine how to make the custom piece
            PieceFactory factory = new CustomPieceFactory(Map.of());
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(pieceStr);
            // colour then code
            replacement = factory.create(tokenizer.nextToken(), Colour.fromCode(tokenizer.nextToken()), Point.ORIGIN);
        } else {
            // Build a standard piece using information from the piece string
            replacement = Pieces.fromString(pieceStr);
        }
        return replacement;
    }
}
