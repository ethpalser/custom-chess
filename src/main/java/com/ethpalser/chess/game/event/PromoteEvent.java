package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.exception.CoordinateOutOfBoundsException;
import com.ethpalser.chess.exception.IllegalMoveException;
import com.ethpalser.chess.exception.MissingPieceException;
import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.ChessLog;
import com.ethpalser.chess.game.context.GameContext;
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

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-19",
        majorVersion = 1,
        minorVersion = 15,
        lastModified = "2025-01-13"
)
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
        ChessLog.Entry prevRec = log.peek();
        if (prevRec == null) {
            throw new IllegalStateException("Severe exception: Log entry before a piece promotion is null");
        }
        String oldCode = prevRec.notation().toRecord().sourceCode();
        Piece replacement = this.updatePieceType(oldCode, board);
        board.add(this.source, replacement);
        // Undo change to log
        log.pop();
        // Commit this change to the game
        context.undo(replacement.getColour(), board, log);
    }

    private void verifyPieceExists(GameContext context, Coordinate coordinate)
            throws IllegalMoveException, IndexOutOfBoundsException {
        if (context.getBoard().rejects(coordinate)) {
            throw new CoordinateOutOfBoundsException();
        }
        if (context.getBoard().get(coordinate) == null) {
            throw new MissingPieceException();
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
