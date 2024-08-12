package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Colour;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame {

    private final Board board;
    private final Log log;
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
        this.log = new StandardLog();
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
        // Is the moving piece pinned? (a pinned piece cannot move, as it will cause the king to be in check)
        if (this.isKingInCheck(this.turn)) {
            this.undoAction(1, false);
            throw new IllegalActionException("cannot perform move as player's king will be in check");
        }

        Piece expectingEmpty = this.board.getPiece(start);
        if (expectingEmpty != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + end);
        }

        this.log.push(new ActionRecord(start, end, movingPiece));
        this.updateKingPosition(movingPiece, end);
        this.status = this.checkGameStatus();
        this.turn = Colour.opposite(this.turn);
        return this.status;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameStatus undoAction() {
        return this.undoAction(1, true);
    }

    public GameStatus undoAction(int beforeCurrent, boolean saveUndone) {
        for (int i = 0; i < beforeCurrent; i++) {
            LogEntry logEntry;
            if (saveUndone) {
                logEntry = this.log.undo();
            } else {
                logEntry = this.log.pop();
            }
            if (logEntry == null) {
                break;
            }

            this.board.addPiece(logEntry.getEnd(), logEntry.getCapturedPiece());
            this.board.addPiece(logEntry.getStart(), logEntry.getMovingPiece());

            this.whiteThreats.clearMoves(logEntry.getMovingPiece());
            this.whiteThreats.updateMoves(this.board, this.log, logEntry.getEnd());
            this.whiteThreats.updateMoves(this.board, this.log, logEntry.getStart());
            this.blackThreats.clearMoves(logEntry.getMovingPiece());
            this.blackThreats.updateMoves(this.board, this.log, logEntry.getEnd());
            this.blackThreats.updateMoves(this.board, this.log, logEntry.getStart());
        }
        return this.checkGameStatus();
    }

    public GameStatus redoAction() {
        return this.redoAction(1);
    }

    public GameStatus redoAction(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry logEntry = this.log.redo();
            if (logEntry == null) {
                break;
            }
            this.board.addPiece(logEntry.getEnd(), logEntry.getCapturedPiece());
            this.board.addPiece(logEntry.getStart(), logEntry.getMovingPiece());

            this.whiteThreats.clearMoves(logEntry.getMovingPiece());
            this.whiteThreats.updateMoves(this.board, this.log, logEntry.getEnd());
            this.whiteThreats.updateMoves(this.board, this.log, logEntry.getStart());
            this.blackThreats.clearMoves(logEntry.getMovingPiece());
            this.blackThreats.updateMoves(this.board, this.log, logEntry.getEnd());
            this.blackThreats.updateMoves(this.board, this.log, logEntry.getStart());
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
            // Todo: how do I get the path? Note: CustomPiece Movement uses Paths, but I don't want to use all of it
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


}
