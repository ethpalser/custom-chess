package com.ethpalser.chess.game;

import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame {

    private final ChessBoard board;
    private final ChessLog log;
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
    }

    public GameStatus updateGame(Action action) throws IllegalActionException {
        if (GameStatus.isCompletedGameStatus(this.status)) {
            throw new IllegalActionException("cannot update completed game");
        }
        if (isNotPlayerAction(action)) {
            throw new IllegalActionException("not the acting player's turn (actor: " + action.getColour()
                    + ", turn: " + this.turn + ")");
        }

        ChessPiece movingPiece = this.board.getPiece(action.getStart());
        if (movingPiece == null) {
            throw new IllegalActionException("cannot perform move as there is no piece at " + action.getStart());
        }
        if (isNotAllowedToMove(movingPiece)) {
            throw new IllegalActionException("not the acting player's piece (actor: " + action.getColour()
                    + ", piece: " + movingPiece.getColour());
        }

        this.board.movePiece(action.getStart(), action.getEnd());

        ChessPiece expectingEmpty = this.board.getPiece(action.getStart());
        if (expectingEmpty != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + action.getEnd());
        }

        this.updateKingPosition(movingPiece, action.getEnd());
        this.status = this.checkGameStatus();
        this.turn = Colour.opposite(this.turn);
        return this.status;
    }

    public GameStatus getStatus() {
        return status;
    }

    // PRIVATE METHODS

    private boolean isNotPlayerAction(Action action) {
        return !this.turn.equals(action.getColour());
    }

    private boolean isNotAllowedToMove(ChessPiece piece) {
        return !this.turn.equals(piece.getColour());
    }

    private boolean isKingPiece(ChessPiece piece) {
        return "K".equals(piece.getCode());
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

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.turn);
        Point toCheck = this.getOpponentKingPosition(this.turn);
        Set<ChessPiece> threats = this.board.getThreats(toCheck, opponent);
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        if (!threats.isEmpty()) {
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
                if (this.board.getThreats(p, this.turn).isEmpty()) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<ChessPiece> sourcesOfCheck = this.board.getThreats(oppKingPoint, this.turn);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return true;
        }
        // There is only one threatening check
        for (ChessPiece p : sourcesOfCheck) {
            // Can this piece be captured by the opponent?
            Set<ChessPiece> defenders = this.board.getThreats(p.getPoint(), oppColour);
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
        if (this.board.getPieces().size() <= 2) {
            return true;
        }
        Colour oppColour = Colour.opposite(this.turn);
        Point oppKingPoint = this.getOpponentKingPosition(this.turn);
        ChessPiece oppKing = this.board.getPiece(oppKingPoint);
        // Assume not in check or checkmate
        Set<Point> oppKingMoves = oppKing.getMoves(this.board, this.log).getPoints();
        if (!oppKingMoves.isEmpty()) {
            return false;
        }

        List<ChessPiece> opponentPieces = this.board.getPieces().values().stream()
                .filter(p -> oppColour.equals(p.getColour()) && !isKingPiece(p)).collect(Collectors.toList());
        for (ChessPiece p : opponentPieces) {
            // Any non-king opponent piece can move
            if (!p.getMoves(this.board, this.log).toSet().isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
