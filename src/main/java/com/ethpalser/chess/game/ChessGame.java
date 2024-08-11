package com.ethpalser.chess.game;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.move.ThreatMap;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame {

    private final ChessBoard board;
    private final ChessLog log;
    private final ThreatMap whiteThreats;
    private final ThreatMap blackThreats;

    private GameStatus status;
    private Colour turn;
    private Point whiteKing;
    private Point blackKing;

    public ChessGame(ChessBoard board) {
        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }
        this.board = board;
        this.log = new Log();
        this.status = GameStatus.PENDING;
        this.turn = Colour.WHITE;
        // Assuming standard starting positions, and if this assumption changes it should be provided by the board
        this.whiteKing = new Point('e', '1');
        this.blackKing = new Point('e', '8');
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.board, this.log);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.board, this.log);
    }

    public GameStatus updateGame(Action action) throws IllegalActionException {
        if (GameStatus.isCompletedGameStatus(this.status)) {
            throw new IllegalActionException("cannot update completed game");
        }
        if (isNotPlayerAction(action)) {
            throw new IllegalActionException("not the acting player's turn (actor: " + action.getColour()
                    + ", turn: " + this.turn + ")");
        }
        if (isNotInBoardBounds(action)) {
            throw new IllegalActionException("cannot perform move as one of the start or end are not on the board");
        }

        ChessPiece movingPiece = this.board.getPiece(action.getStart());
        if (movingPiece == null) {
            throw new IllegalActionException("cannot perform move as there is no piece at " + action.getStart());
        }
        if (isNotAllowedToMove(movingPiece)) {
            throw new IllegalActionException("not the acting player's piece (actor: " + action.getColour()
                    + ", piece: " + movingPiece.getColour() + ")");
        }

        this.board.movePiece(action.getStart(), action.getEnd());
        // Is the moving piece pinned? (a pinned piece cannot move, as it will cause the king to be in check)
        if (this.isKingInCheck(this.turn)) {
            this.undoAction(1, false);
            throw new IllegalActionException("cannot perform move as player's king will be in check");
        }

        ChessPiece expectingEmpty = this.board.getPiece(action.getStart());
        if (expectingEmpty != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + action.getEnd());
        }

        this.log.push(new ActionRecord(action, movingPiece));
        this.updateKingPosition(movingPiece, action.getEnd());
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
            LogRecord logRecord;
            if (saveUndone) {
                logRecord = this.log.undo();
            } else {
                logRecord = this.log.pop();
            }
            if (logRecord == null) {
                break;
            }

            this.board.addPiece(logRecord.getEnd(), logRecord.getCapturedPiece());
            this.board.addPiece(logRecord.getStart(), logRecord.getMovingPiece());

            this.whiteThreats.clearMoves(logRecord.getMovingPiece());
            this.whiteThreats.updateMoves(this.board, this.log, logRecord.getEnd());
            this.whiteThreats.updateMoves(this.board, this.log, logRecord.getStart());
            this.blackThreats.clearMoves(logRecord.getMovingPiece());
            this.blackThreats.updateMoves(this.board, this.log, logRecord.getEnd());
            this.blackThreats.updateMoves(this.board, this.log, logRecord.getStart());
        }
        return this.checkGameStatus();
    }

    public GameStatus redoAction() {
        return this.redoAction(1);
    }

    public GameStatus redoAction(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogRecord logRecord = this.log.redo();
            if (logRecord == null) {
                break;
            }
            this.board.addPiece(logRecord.getEnd(), logRecord.getCapturedPiece());
            this.board.addPiece(logRecord.getStart(), logRecord.getMovingPiece());

            this.whiteThreats.clearMoves(logRecord.getMovingPiece());
            this.whiteThreats.updateMoves(this.board, this.log, logRecord.getEnd());
            this.whiteThreats.updateMoves(this.board, this.log, logRecord.getStart());
            this.blackThreats.clearMoves(logRecord.getMovingPiece());
            this.blackThreats.updateMoves(this.board, this.log, logRecord.getEnd());
            this.blackThreats.updateMoves(this.board, this.log, logRecord.getStart());
        }
        return this.checkGameStatus();
    }

    // PRIVATE METHODS

    private boolean isNotPlayerAction(Action action) {
        if (action == null) {
            throw new NullPointerException();
        }
        return !this.turn.equals(action.getColour());
    }

    private boolean isNotInBoardBounds(Action action) {
        return action.getStart() == null || action.getEnd() == null
                || !this.board.isInBounds(action.getStart()) || !this.board.isInBounds(action.getEnd());
    }

    private boolean isNotAllowedToMove(ChessPiece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        return !this.turn.equals(piece.getColour());
    }

    private boolean isKingPiece(ChessPiece piece) {
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

    private void updateKingPosition(ChessPiece piece, Point update) {
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
        ChessPiece oppKing = this.board.getPiece(oppKingPoint);
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
        Set<ChessPiece> sourcesOfCheck = this.getThreatMap(this.turn).getPieces(oppKingPoint);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return true;
        }
        // There is only one threatening check
        for (ChessPiece p : sourcesOfCheck) {
            // Can this piece be captured by the opponent?
            Set<ChessPiece> defenders = this.getThreatMap(oppColour).getPieces(p.getPoint());
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
        ChessPiece oppKing = this.board.getPiece(oppKingPoint);
        // Can the opponent's king move, including captures that are not defended?
        Set<Point> oppKingMoves = oppKing.getMoves(this.board, this.log).getPoints();
        if (!oppKingMoves.isEmpty()) {
            return false;
        }
        // Are there any non-king opponent pieces that can move?
        List<ChessPiece> opponentPieces = this.board.getPieces().values().stream()
                .filter(p -> oppColour.equals(p.getColour()) && !isKingPiece(p)).collect(Collectors.toList());
        for (ChessPiece p : opponentPieces) {
            if (!p.getMoves(this.board, this.log).toSet().isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
