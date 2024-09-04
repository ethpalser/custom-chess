package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.view.GameView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame implements Game {

    private final Board board;
    private final Log<Point, Piece> log;
    private final ThreatMap whiteThreats;
    private final ThreatMap blackThreats;

    private GameStatus status;
    private Colour player;
    private Point whiteKing;
    private Point blackKing;
    private int turn;

    public ChessGame(Board board, Log<Point, Piece> log) {
        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }
        this.board = board;
        this.log = log;
        this.status = GameStatus.PENDING;
        for (Piece p : this.board.getPieces()) {
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = p.getPoint();
                } else {
                    this.blackKing = p.getPoint();
                }
            }
        }
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.board.getPieces(), log);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.board.getPieces(), log);
        this.turn = log.size();
        this.player = this.turn % 2 == 0 ? Colour.WHITE : Colour.BLACK;
    }

    public ChessGame(GameView view) {
        this.turn = view.getTurn();
        this.player = this.turn % 2 == 0 ? Colour.WHITE : Colour.BLACK;
        this.log = new ChessLog();
        this.board = new ChessBoard(this.log, view.getBoard(), view.getPieceSpecs());
        for (Piece p : this.board.getPieces()) {
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = p.getPoint();
                } else {
                    this.blackKing = p.getPoint();
                }
            }
        }
        this.log.addAll(this.board.getPieces(), view.getLog());
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.board.getPieces(), this.log);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.board.getPieces(), this.log);
        this.status = checkGameStatus();
    }

    @Override
    public Board getBoard() {
        return this.board;
    }

    @Override
    public Log<Point, Piece> getLog() {
        return this.log;
    }

    @Override
    public GameStatus getStatus() {
        return status;
    }

    @Override
    public int getTurn() {
        return this.turn;
    }

    @Override
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
                    + ", turn: " + this.player + ")");
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

        LogEntry<Point, Piece> entry = this.board.movePiece(start, end, this.log,
                this.getThreatMap(Colour.opposite(this.player)));
        this.log.push(entry);

        // Update opponent's threats with the move performed
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board.getPieces(), this.log, start);
        // Does moving this piece put turn player in check? (opponent's updated threats now include turn player's king)
        if (this.isKingInCheck(this.player)) {
            this.undoUpdate(1, false);
            throw new IllegalActionException("cannot perform move as player's king will be in check");
        }

        // Update remaining threats
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board.getPieces(), this.log, end);
        this.getThreatMap(this.player).refreshThreats(this.board.getPieces(), this.log, start);
        this.getThreatMap(this.player).refreshThreats(this.board.getPieces(), this.log, end);
        if (entry.getSubLogEntry() != null) {
            this.applyLogEntryToThreats(entry.getSubLogEntry());
        }

        Piece expectingEmpty = this.board.getPiece(start);
        if (expectingEmpty != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + end);
        }

        this.updateKingPosition(movingPiece, end);
        this.status = this.checkGameStatus();
        this.player = Colour.opposite(this.player);
        this.turn++;
        return this.status;
    }

    @Override
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
                this.applyLogEntryToBoard(logEntry.getSubLogEntry());
                this.applyLogEntryToThreats(logEntry.getSubLogEntry());
            }
            this.applyLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
        }
        return this.checkGameStatus();
    }

    @Override
    public GameStatus redoUpdate(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry<Point, Piece> logEntry = this.log.redo();
            if (logEntry == null) {
                break;
            }
            this.applyLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
            // FollowUp moves are applied last while redoing, as they are the most recent board change
            if (logEntry.getSubLogEntry() != null) {
                this.applyLogEntryToBoard(logEntry.getSubLogEntry());
                this.applyLogEntryToThreats(logEntry.getSubLogEntry());
            }
        }
        return this.checkGameStatus();
    }

    @Override
    public Iterable<Action> potentialUpdates() {
        List<Action> potentialCaptures = new ArrayList<>(64);
        List<Action> quietActions = new ArrayList<>(128);
        for (Piece piece : this.board.getPieces()) {
            MoveSet moves = piece.getMoves(this.board.getPieces(), this.log,
                    this.getThreatMap(Colour.opposite(piece.getColour())));
            for (Movement m : moves.toSet()) {
                Path path = m.getPath();
                if (path != null && path.length() > 0) {
                    // The last point in a path is a potential capture
                    potentialCaptures.add(new Action(piece.getColour(), piece.getPoint(),
                            path.getPoint(path.length() - 1)));
                    // Remaining points are quiet actions (no captures)
                    for (int i = 0; i < path.length() - 1; i++) {
                        quietActions.add(new Action(piece.getColour(), piece.getPoint(), path.getPoint(i)));
                    }
                }
            }
        }
        // Potential captures initially have priority for evaluating board state (can change by game tree)
        potentialCaptures.addAll(quietActions);
        return potentialCaptures;
    }

    @Override
    public int evaluateState() {
        return this.evaluateBoardState()
                + this.whiteThreats.evaluate(this.board.getPieces())
                - this.blackThreats.evaluate(this.board.getPieces());
    }

    public String toJson() {
        GameView info = new GameView(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(info);
    }

    // PRIVATE METHODS

    private int evaluateBoardState() {
        int whiteSum = 0;
        int blackSum = 0;
        for (Piece p : board.getPieces()) {
            if (Colour.WHITE.equals(p.getColour())) {
                whiteSum += this.getPieceValue(p);
            } else {
                blackSum += this.getPieceValue(p);
            }
        }
        return whiteSum - blackSum;
    }

    private int getPieceValue(Piece p) {
        if (p == null) {
            return 0;
        }
        int value;
        switch (PieceType.fromCode(p.getCode())) {
            case PAWN -> value = 1;
            case BISHOP, KNIGHT -> value = 3;
            case ROOK -> value = 5;
            case QUEEN -> value = 9;
            case CUSTOM -> {
                // Currently, this uses MoveSet, but this would be more accurate to use its blueprint
                MoveSet moveSet = p.getMoves(this.board.getPieces(), this.log,
                        this.getThreatMap(Colour.opposite(p.getColour())));
                int numMoves = moveSet.getPoints().size();
                int base = (int) Math.ceil(numMoves / 3.0);
                value = base + base / 3;
            }
            default -> value = 0;
        }
        return value;
    }

    private boolean isNotPlayerAction(Colour colour) {
        return !this.player.equals(colour);
    }

    private boolean isNotInBoardBounds(Point start, Point end) {
        return start == null || end == null || !this.board.isInBounds(start) || !this.board.isInBounds(end);
    }

    private boolean isNotAllowedToMove(Piece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        return !this.player.equals(piece.getColour());
    }

    private boolean isKingPiece(Piece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        return "K".equals(piece.getCode());
    }

    private boolean isKingInCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        // Does the opponent have a threat on the current king's position
        return !this.getThreatMap(Colour.opposite(kingColour)).hasNoThreats(getKingPosition(kingColour));
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

    private Point getKingPosition(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return this.whiteKing;
        } else {
            return this.blackKing;
        }
    }

    private ThreatMap getThreatMap(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return whiteThreats;
        } else {
            return blackThreats;
        }
    }

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.player);
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        if (isKingInCheck(opponent)) {
            if (this.isCheckmate()) {
                nextStatus = GameStatus.colourWinStatus(this.player);
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
        Colour oppColour = Colour.opposite(this.player);
        Point oppKingPoint = this.getKingPosition(Colour.opposite(this.player));

        // Assuming King is in check
        MoveSet oppKingMoveSet = this.board.getPiece(oppKingPoint)
                .getMoves(this.board.getPieces(), this.log, this.getThreatMap(this.player));
        if (oppKingMoveSet != null && !oppKingMoveSet.isEmpty()) {
            for (Point p : oppKingMoveSet.getPoints()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (this.getThreatMap(this.player).hasNoThreats(p)) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Piece> sourcesOfCheck = this.getThreatMap(this.player).getPieces(oppKingPoint);
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
            Movement causingCheck = p.getMoves(this.board.getPieces(), this.log).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            for (Point c : causingCheck.getPath()) {
                // Yes, there is at least one non-king piece that can move to a point along the path causing check
                if (this.getThreatMap(oppColour).hasNoThreats(c, true)) {
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
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = this.board.getPieces().values().stream()
                .filter(p -> Colour.opposite(this.player).equals(p.getColour()))
                .collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            if (!p.getMoves(this.board.getPieces(), this.log, this.getThreatMap(this.player)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void applyLogEntryToBoard(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            System.err.println("cannot apply null log entry to board");
            return;
        }
        if (logEntry.getEndObject() != null) {
            this.board.addPiece(logEntry.getEndObject().getPoint(), logEntry.getEndObject());
        }
        this.board.addPiece(logEntry.getStart(), logEntry.getStartObject());
    }

    private void applyLogEntryToThreats(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            System.err.println("cannot apply null log entry to threats");
            return;
        }
        this.whiteThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getStart());
        this.blackThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getStart());
        // End can be null when removing a piece
        if (logEntry.getEnd() != null) {
            this.whiteThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getEnd());
            this.blackThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getEnd());
        }
    }
}
