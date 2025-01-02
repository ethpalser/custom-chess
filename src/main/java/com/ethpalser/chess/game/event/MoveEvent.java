package com.ethpalser.chess.game.event;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.map.ThreatMap;
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
        if (context.getBoard().rejects(this.source) || context.getBoard().rejects(this.target)) {
            throw new IndexOutOfBoundsException("One or more coordinates are out of bounds");
        }
        if (context.getBoard().get(this.source) == null) {
            throw new IllegalActionException("piece to move from " + this.source + " to " + this.target + " is null");
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
        Move move = piece.getMoves(board, log, oppThreats).getMove((Point) this.target);
        if (!piece.canMove(board, log, oppThreats, (Point) this.target)) {
            throw new IllegalActionException("piece (" + piece + ") cannot move to " + target);
        }

        Move.FollowUp followUp = move.followUp();
        // Todo: depreciate using LogEntry for followUp
        LogEntry<Coordinate, Piece> followUpLog;
        if (followUp != null) {
            Path followUpPath = followUp.path();
            followUpLog = new ChessLogEntry(followUpPath.getPoint(0),
                    followUpPath.getPoint(followUpPath.length() - 1),
                    board.get(followUpPath.getPoint(followUpPath.length() - 1)));
        } else {
            followUpLog = null;
        }
        ChessLogEntry logEntry = new ChessLogEntry((Point) this.source, (Point) this.target, piece,
                board.get(this.target),
                followUpLog);
        // Update the board reference with all movements, which should not modify the context yet
        board.remove(this.target);
        board.remove(this.source);
        board.add(this.target, piece);
        piece.move(this.target);

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
                context.raisePrompt(new GamePrompt(EventType.PROMOTE, this.target, piece.promoteOptions()));
            }
        }
        // Commit this change to the game
        context.update(turnPlayer, board, log);
    }

    private boolean promotePiece(Piece piece, Board<Coordinate> board, Log<Coordinate, Piece> log) {
        List<String> promoteOptions = piece.promoteOptions();
        // Temporary, always have pawns promote to queen to simplify running simulations
        String pieceStr;
        if (promoteOptions.size() == 1 || PieceType.PAWN.getCode().equals(piece.getCode())) {
            pieceStr = Pieces.asString(piece, promoteOptions.get(0));
        } else {
            return false;
        }
        // Manually modify piece's string then convert it into a piece
        Piece replacement;
        if (PieceType.fromCode(promoteOptions.get(0)) == PieceType.CUSTOM) {
            // CustomPieceFactory should load custom piece specifications to determine how to make the custom piece
            PieceFactory factory = new CustomPieceFactory(Map.of(), log, board.space());
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(pieceStr);
            // colour then code
            replacement = factory.create(Colour.fromCode(tokenizer.nextToken()), tokenizer.nextToken());
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
        Move move = piece.getMoves(board, log, oppThreats).getMove((Point) this.target);

        Move.FollowUp followUp = move.followUp();
        if (followUp != null) {
            Path followUpPath = followUp.path();
            // Todo: depreciate using LogEntry for followUp
            LogEntry<Coordinate, Piece> followUpLog = new ChessLogEntry(followUpPath.getPoint(0),
                    followUpPath.getPoint(followUpPath.length() - 1),
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
