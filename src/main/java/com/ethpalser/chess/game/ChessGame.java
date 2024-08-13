package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame {

    private final Board board;
    private final ChessLog log;
    private final ThreatMap whiteThreats;
    private final ThreatMap blackThreats;

    private GameStatus status;
    private Colour turn;
    private Point whiteKing;
    private Point blackKing;

    public ChessGame(Board board) {
        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }
        this.board = board;
        this.log = new ChessLog();
        this.status = GameStatus.PENDING;
        this.turn = Colour.WHITE;
        // Assuming standard starting positions, and if this assumption changes it should be provided by the board
        this.whiteKing = new Point('e', '1');
        this.blackKing = new Point('e', '8');
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.board, this.log);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.board, this.log);
    }

    public GameStatus updateGame(Action action) throws IllegalActionException {
        if (action == null) {
            throw new IllegalActionException("action cannot be null");
        }
        return this.updateGame(action.getStart(), action.getEnd(), action.getColour());
    }

    public GameStatus updateGame(Point start, Point end, Colour player) throws IllegalActionException {
        if (GameStatus.isCompletedGameStatus(this.status)) {
            throw new IllegalActionException("cannot update completed game");
        }
        if (isNotPlayerAction(player)) {
            throw new IllegalActionException("not the acting player's turn (actor: " + player
                    + ", turn: " + this.turn + ")");
        }
        if (isNotInBoardBounds(start, end)) {
            throw new IllegalActionException("cannot perform move as one of the start or end are not on the board");
        }

        Piece movingPiece = this.board.getPiece(start);
        if (movingPiece == null) {
            throw new IllegalActionException("cannot perform move as there is no piece at " + start);
        }
        if (isNotAllowedToMove(movingPiece)) {
            throw new IllegalActionException("not the acting player's piece (actor: " + player
                    + ", piece: " + movingPiece.getColour() + ")");
        }

        this.board.movePiece(start, end);
        // Todo: update movePiece to return a LogEntry
        this.log.push(new ChessLogEntry(start, end, movingPiece));
        // Is the moving piece pinned? (a pinned piece cannot move, as it will cause the king to be in check)
        if (this.isKingInCheck(this.turn)) {
            this.undoUpdate(1, false);
            throw new IllegalActionException("cannot perform move as player's king will be in check");
        }

        Piece expectingEmpty = this.board.getPiece(start);
        if (expectingEmpty != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + end);
        }

        this.updateKingPosition(movingPiece, end);
        this.status = this.checkGameStatus();
        this.turn = Colour.opposite(this.turn);
        return this.status;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameStatus undoUpdate() {
        return this.undoUpdate(1, true);
    }

    public GameStatus undoUpdate(int beforeCurrent, boolean saveUndone) {
        for (int i = 0; i < beforeCurrent; i++) {
            LogEntry<Point, Piece> logEntry;
            if (saveUndone) {
                logEntry = this.log.undo();
            } else {
                logEntry = this.log.pop();
            }
            if (logEntry == null) {
                break;
            }
            // FollowUp moves are applied first while undoing, as they are the most recent board change
            if (logEntry.getSubLogEntry() != null) {
                this.applyLog(logEntry.getSubLogEntry());
            }
            this.applyLog(logEntry);
        }
        return this.checkGameStatus();
    }

    public GameStatus redoUpdate() {
        return this.redoUpdate(1);
    }

    public GameStatus redoUpdate(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry<Point, Piece> logEntry = this.log.redo();
            if (logEntry == null) {
                break;
            }
            this.applyLog(logEntry);
            // FollowUp moves are applied last while redoing, as they are the most recent board change
            if (logEntry.getSubLogEntry() != null) {
                this.applyLog(logEntry.getSubLogEntry());
            }
        }
        return this.checkGameStatus();
    }

    // PRIVATE METHODS

    private boolean isNotPlayerAction(Colour colour) {
        return !this.turn.equals(colour);
    }

    private boolean isNotInBoardBounds(Point start, Point end) {
        return start == null || end == null || !this.board.isInBounds(start) || !this.board.isInBounds(end);
    }

    private boolean isNotAllowedToMove(Piece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        return !this.turn.equals(piece.getColour());
    }

    private boolean isKingPiece(Piece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        return "K".equals(piece.getCode());
    }

    private boolean isKingInCheck(Colour playerColour) {
        if (playerColour == null) {
            throw new NullPointerException();
        }
        return this.hasThreats(playerColour, this.getOpponentKingPosition(this.turn));
    }

    private void updateKingPosition(Piece piece, Point update) {
        if (isKingPiece(piece)) {
            if (Colour.WHITE.equals(piece.getColour())) {
                this.whiteKing = update;
            } else {
                this.blackKing = update;
            }
        }
    }

    private Point getOpponentKingPosition(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return this.blackKing;
        } else {
            return this.whiteKing;
        }
    }

    private ThreatMap getThreatMap(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return whiteThreats;
        } else {
            return blackThreats;
        }
    }

    private boolean hasThreats(Colour colour, Point point) {
        return !this.getThreatMap(colour).getPieces(point).isEmpty();
    }

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.turn);
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        if (isKingInCheck(opponent)) {
            if (this.isCheckmate()) {
                nextStatus = GameStatus.colourWinStatus(this.turn);
            } else {
                nextStatus = GameStatus.colourInCheckStatus(opponent);
            }
        } else {
            if (this.isStalemate()) {
                nextStatus = GameStatus.STALEMATE;
            } else {
                nextStatus = GameStatus.ONGOING;
            }
        }
        return nextStatus;
    }

    private boolean isCheckmate() {
        Colour oppColour = Colour.opposite(this.turn);
        Point oppKingPoint = this.getOpponentKingPosition(this.turn);
        Piece oppKing = this.board.getPiece(oppKingPoint);
        // Assuming King is in check
        Set<Point> oppKingMoveSet = oppKing.getMoves(this.board, this.log).getPoints();
        if (!oppKingMoveSet.isEmpty()) {
            for (Point p : oppKingMoveSet) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (!hasThreats(this.turn, p)) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Piece> sourcesOfCheck = this.getThreatMap(this.turn).getPieces(oppKingPoint);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return true;
        }
        // There is only one threatening check
        for (Piece p : sourcesOfCheck) {
            // Can this piece be captured by the opponent?
            Set<Piece> defenders = this.getThreatMap(oppColour).getPieces(p.getPoint());
            if (!defenders.isEmpty()) {
                // Yes, as this piece can be captured by a non-king, the opponent has a legal move to prevent check
                return false;
            }
            // Can a piece block its path?
            Move causingCheck = p.getMoves(this.board, this.log).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            for (Point c : causingCheck.getPath()) {
                // Yes, there is at least one piece that can move to a point along the path causing check
                if (!this.getThreatMap(oppColour).getPieces(c).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isStalemate() {
        // Only kings remain, which is a stalemate
        if (this.board.getPieces().size() <= 2) {
            return true;
        }
        Colour oppColour = Colour.opposite(this.turn);
        Point oppKingPoint = this.getOpponentKingPosition(this.turn);
        Piece oppKing = this.board.getPiece(oppKingPoint);
        // Can the opponent's king move, including captures that are not defended?
        Set<Point> oppKingMoves = oppKing.getMoves(this.board, this.log).getPoints();
        if (!oppKingMoves.isEmpty()) {
            return false;
        }
        // Are there any non-king opponent pieces that can move?
        List<Piece> opponentPieces = this.board.getPieces().values().stream()
                .filter(p -> oppColour.equals(p.getColour()) && !isKingPiece(p)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            if (!p.getMoves(this.board, this.log).toSet().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void applyLog(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            throw new NullPointerException();
        }

        if (logEntry.getEndObject() != null) {
            this.board.addPiece(logEntry.getEndObject().getPoint(), logEntry.getEndObject());
        }
        this.board.addPiece(logEntry.getStart(), logEntry.getStartObject());

        this.whiteThreats.clearMoves(logEntry.getStartObject());
        this.whiteThreats.updateMoves(this.board, this.log, logEntry.getEnd());
        this.whiteThreats.updateMoves(this.board, this.log, logEntry.getStart());
        this.blackThreats.clearMoves(logEntry.getStartObject());
        this.blackThreats.updateMoves(this.board, this.log, logEntry.getEnd());
        this.blackThreats.updateMoves(this.board, this.log, logEntry.getStart());
    }
}
