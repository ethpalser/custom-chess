package com.ethpalser.chess.game.event;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.PieceStringTokenizer;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.Map;

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
        Log<Coordinate, Piece> log = contextRecord.getLog();

        if (board.rejects(this.source) || board.rejects(this.target)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (board.get(this.source) == null) {
            throw new IllegalActionException("piece to move from " + this.source + " to " + this.target + " is null");
        }

        Piece piece = board.get(this.source);
        // The turn player should always match the acting piece, and this piece should always be from the first change
        Colour turnPlayer = piece.getColour();
        MoveSet moveSet = piece.getMoves(contextRecord);
        Move move = moveSet.getMove(this.target);
        if (!piece.canMove(this.target, contextRecord)) {
            throw new IllegalActionException("piece (" + piece + ") cannot move to " + target);
        }

        Move.FollowUp followUp = move.followUp();
        // Todo: depreciate using LogEntry for followUp
        LogEntry<Coordinate, Piece> followUpLog;
        if (followUp != null) {
            Path followUpPath = followUp.path();
            // When the path is null, remove the piece at the
            Coordinate logStart = followUp.reference().coordinates(contextRecord, piece.getCoordinate()).get(0);
            Coordinate logEnd = followUpPath == null || followUpPath.isEmpty() ? null
                    : followUpPath.getPoint(followUpPath.length() - 1);
            Piece logStartPiece = board.get(logStart);

            followUpLog = new ChessLogEntry((Point) logStart, (Point) logEnd, logStartPiece);
        } else {
            followUpLog = null;
        }
        ChessLogEntry logEntry = new ChessLogEntry((Point) this.source, (Point) this.target, piece,
                board.get(this.target),
                followUpLog);
        // Update the board reference with all movements, which should not modify the context yet
        board.remove(this.target);
        board.remove(this.source);
        piece.move(this.target); // Apparently this change is not being retained by updated context
        board.add(this.target, piece);

        if (followUpLog != null) {
            Piece toForcePush = followUpLog.getStartObject();
            board.remove(followUpLog.getStart());
            if (followUpLog.getEnd() != null) {
                board.add(followUpLog.getEnd(), toForcePush);
            }
        }
        board.remove(null);

        log.push(logEntry);
        if (piece.canPromote(board)) {
            boolean promoted = this.promotePiece(piece, board, log);
            if (!promoted) {
                context.raisePrompt(new GamePrompt(EventType.PROMOTE, this.target, piece.getPromotions()));
            }
        }
        // Commit this change to the game
        context.update(turnPlayer, board, log);
    }

    private boolean promotePiece(Piece piece, Board<Coordinate> board, Log<Coordinate, Piece> log) {
        List<String> promoteOptions = piece.getPromotions();
        // Temporary, always have pawns promote to queen to simplify running simulations
        String pieceStr;
        if (promoteOptions.size() == 1 || PieceType.PAWN.toCode().equals(piece.getCode())) {
            pieceStr = Pieces.asString(piece, promoteOptions.get(0));
        } else {
            return false;
        }
        // Manually modify piece's string then convert it into a piece
        Piece replacement;
        if (PieceType.fromCode(promoteOptions.get(0)) == PieceType.CUSTOM) {
            // CustomPieceFactory should load custom piece specifications to determine how to make the custom piece
            PieceFactory factory = new CustomPieceFactory(Map.of());
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(pieceStr);
            // colour then code
            replacement = factory.create(tokenizer.nextToken(), Colour.fromCode(tokenizer.nextToken()), Point.ORIGIN);
        } else {
            // Build a standard piece using information from the piece string
            replacement = Pieces.fromString(pieceStr);
        }
        board.add(this.target, replacement);
        log.peek().setPromotion(replacement);
        return true;
    }

    @Override
    public void unExecute(GameContext context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        GameContext.Record contextRecord = context.toRecord(); // Copies information
        if (contextRecord.getBoard().rejects(this.source) || contextRecord.getBoard().rejects(this.target)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (contextRecord.getBoard().get(this.target) == null) {
            throw new IllegalActionException("The piece to move does not exist");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        Board<Coordinate> board = contextRecord.getBoard();
        Log<Coordinate, Piece> log = contextRecord.getLog();

        Piece piece = board.get(this.source);
        // The turn player should always match the acting piece, and this piece should always be from the first change
        Colour turnPlayer = piece.getColour();

        // Reverse the followup first
        Move move = piece.getMoves(contextRecord).getMove(this.target);

        Move.FollowUp followUp = move.followUp();
        if (followUp != null) {
            Path followUpPath = followUp.path();
            // Todo: depreciate using LogEntry for followUp
            LogEntry<Coordinate, Piece> followUpLog = new ChessLogEntry((Point) followUpPath.getPoint(0),
                    (Point) followUpPath.getPoint(followUpPath.length() - 1),
                    board.get(followUpPath.getPoint(followUpPath.length() - 1)));
            Piece toForcePush = followUpLog.getEndObject();
            board.remove(followUpLog.getEnd());
            board.add(followUpLog.getStart(), toForcePush);
        }
        board.remove(null);

        // Update the board reference with all movements, which should not modify the context yet
        board.remove(this.target);
        board.remove(this.source);
        board.add(this.source, piece);
        piece.move(this.source);
        log.pop();
        // Commit this change to the game
        context.undo(turnPlayer, board, log);
    }
}
