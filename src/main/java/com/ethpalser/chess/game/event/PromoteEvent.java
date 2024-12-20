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
    public EventType type() {
        return EventType.PROMOTE;
    }

    @Override
    public void execute(GameContext context) {
        if (context.getBoard().rejects(this.source)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (context.getBoard().get(this.source) == null) {
            throw new IllegalActionException("The piece to move does not exist");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        Board<Coordinate> board = context.getBoard();
        Log<Coordinate, Piece> log = context.getLog();

        // Manually modify piece's string then convert it into a piece
        String pieceStr = Pieces.asString(board.get(this.source), this.promoteCode);
        Piece replacement;
        if (PieceType.fromCode(this.promoteCode) == PieceType.CUSTOM) {
            // CustomPieceFactory should load custom piece specifications to determine how to make the custom piece
            PieceFactory factory = new CustomPieceFactory(Map.of(), log, board.space());
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(pieceStr);
            // colour then code
            replacement = factory.create(Colour.fromCode(tokenizer.nextToken()), tokenizer.nextToken());
        } else {
            // Build a standard piece using information from the piece string
            replacement = Pieces.fromString(pieceStr);
        }
        // Update the board and latest log with this promotion
        board.add(this.source, replacement);
        log.peek().setPromotion(replacement);
        // Commit this change to the game
        context.update(replacement.getColour(), board, log);
    }

    @Override
    public void unExecute(GameContext context) {

    }
}
