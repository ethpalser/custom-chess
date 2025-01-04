package com.ethpalser.chess.game.event;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.Map;

public class PromoteEvent implements GameEvent {

    private final Coordinate source;
    private final String promoteCode;

    public PromoteEvent(Coordinate source, String pieceCode) {
        if (source == null || pieceCode == null) {
            throw new IllegalArgumentException();
        }
        this.source = source;
        this.promoteCode = pieceCode;
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
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog();

        Piece replacement = this.updatePieceType(this.promoteCode, board, log);
        // Update the board and latest log with this promotion
        board.add(this.source, replacement);
        log.peek().setPromotion(replacement);
        // Commit this change to the game
        context.update(replacement.getColour(), board, log);
    }

    @Override
    public void unExecute(GameContext context) {
        this.verifyPieceExists(context, this.source);
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog();

        String code;
        if (log.peek().getEndObject() != null) {
            code = log.peek().getEndObject().getCode();
        } else if (log.peek().getStartObject() != null) {
            code = log.peek().getStartObject().getCode();
        } else {
            throw new IllegalStateException("Log does not have a piece to demote.");
        }

        Piece replacement = this.updatePieceType(code, board, log);
        board.add(this.source, replacement);
        log.peek().setPromotion(null);
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

    private Piece updatePieceType(String code, Board<Coordinate> board, Log<Coordinate, Piece> log) {
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
