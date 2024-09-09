package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.view.GameView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessGame implements Game {

    private final Board board;
    private final Log<Point, Piece> log;
    private ThreatMap whiteThreats;
    private ThreatMap blackThreats;

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
        this.turn = log.size() + 1;
        this.player = this.turn % 2 != 0 ? Colour.WHITE : Colour.BLACK;
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
            return this.status;
        }
        if (isNotPlayerAction(player)) {
            // System.err.println("not the acting player's turn (actor: " + player + ", turn: " + this.player + ")");
            return GameStatus.NO_CHANGE;
        }
        if (isNotInBoardBounds(start, end)) {
            // System.err.println("cannot perform move as one of the start or end are not on the board");
            return GameStatus.NO_CHANGE;
        }

        Piece movingPiece = this.board.getPiece(start);
        if (movingPiece == null) {
            // System.err.println("cannot perform move as there is no piece at " + start);
            return GameStatus.NO_CHANGE;
        }
        if (isNotAllowedToMove(movingPiece)) {
            // System.err.println("not the acting player's piece (actor: " + player + ", piece: " + movingPiece.getColour() + ")");
            return GameStatus.NO_CHANGE;
        }
        LogEntry<Point, Piece> entry = this.board.movePiece(start, end, this.log,
                this.getThreatMap(Colour.opposite(this.player)));
        this.log.push(entry);
        this.updateKingPosition(movingPiece, end);

        // Update opponent's threats with the move performed
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board.getPieces(), this.log, start);
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board.getPieces(), this.log, end);
        // Does moving this piece put turn player in check? (opponent's updated threats now include turn player's king)
        if (this.isKingInCheck(this.player)) {
            LogEntry<Point, Piece> logEntry = this.log.pop();
            // soft undo update, which does not alter turn or player nor check game status
            this.undoLogEntryToBoard(logEntry.getSubLogEntry());
            this.undoLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
            this.updateKingPosition(movingPiece, start);
            return GameStatus.NO_CHANGE;
        }

        // Update remaining threats
        this.getThreatMap(this.player).refreshThreats(this.board.getPieces(), this.log, start);
        this.getThreatMap(this.player).refreshThreats(this.board.getPieces(), this.log, end);
        if (entry.getSubLogEntry() != null) {
            this.applyLogEntryToThreats(entry.getSubLogEntry());
        }

        if (this.board.getPiece(start) != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + end);
        }

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
            if (logEntry.getSubLogEntry() != null) {
                this.undoLogEntryToBoard(logEntry.getSubLogEntry());
                this.applyLogEntryToThreats(logEntry.getSubLogEntry());
            }
            this.undoLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);

            this.updateKingPosition(logEntry.getStartObject(), logEntry.getStart());
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
            this.turn--;
        }
        return this.status;
    }

    private void undoLogEntryToBoard(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        this.board.addPiece(logEntry.getEnd(), logEntry.getEndObject());
        this.board.addPiece(logEntry.getStart(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(false);
        }

    }

    @Override
    public GameStatus redoUpdate(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry<Point, Piece> logEntry = this.log.redo();
            if (logEntry == null) {
                break;
            }
            this.redoLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
            if (logEntry.getSubLogEntry() != null) {
                this.redoLogEntryToBoard(logEntry.getSubLogEntry());
                this.applyLogEntryToThreats(logEntry.getSubLogEntry());
            }

            this.updateKingPosition(logEntry.getStartObject(), logEntry.getEnd());
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
            this.turn++;
        }
        return this.status;
    }

    private void redoLogEntryToBoard(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        this.board.addPiece(logEntry.getEnd(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(true);
        }
        // Remove the piece at the start point
        this.board.addPiece(logEntry.getStart(), null);
    }

    private void applyLogEntryToThreats(LogEntry<Point, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        // End can be null when removing a piece
        if (logEntry.getEnd() != null) {
            this.whiteThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getEnd());
            this.blackThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getEnd());
        }
        this.whiteThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getStart());
        this.blackThreats.refreshThreats(this.board.getPieces(), this.log, logEntry.getStart());
    }

    @Override
    public Iterable<Action> potentialUpdates() {
        List<Action> potentialCaptures = new ArrayList<>(64);
        List<Action> quietActions = new ArrayList<>(128);

        if (GameStatus.isCompletedGameStatus(this.status)) {
            return List.of(); // It has been confirmed there are no moves, don't generate more
        } else if (GameStatus.WHITE_IN_CHECK.equals(this.status)) {
            return this.getActionsAgainstCheck(Colour.WHITE);
        } else if (GameStatus.BLACK_IN_CHECK.equals(this.status)) {
            return this.getActionsAgainstCheck(Colour.BLACK);
        } else {
            for (Piece piece : this.board.getPieces()) {
                if (Pieces.isAllied(this.player, piece)) {
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
                + this.blackThreats.evaluate(this.board.getPieces());
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

    private Colour opponent() {
        return Colour.opposite(this.player);
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

    private boolean isKingInCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        // Does the opponent have a threat on the current king's position
        return !this.getThreatMap(Colour.opposite(kingColour)).hasNoThreats(getKingPosition(kingColour));
    }

    private void updateKingPosition(Piece piece, Point update) {
        if (Pieces.isKing(piece)) {
            if (Pieces.isAllied(Colour.WHITE, piece)) {
                this.whiteKing = update;
            } else {
                this.blackKing = update;
            }
        }
    }

    private Point getKingPosition(Colour colour) {
        Point point;
        if (Colour.WHITE.equals(colour)) {
            point = this.whiteKing;
        } else {
            point = this.blackKing;
        }
        return point;
    }

    private Point actualKingPosition(Colour colour) {
        System.out.println("King expected: " + this.getKingPosition(colour));
        for (int y = 0; y < this.board.getPieces().length(); y++) {
            for (int x = 0; x < this.board.getPieces().width(); x++) {
                if (Pieces.isKing(this.board.getPiece(x, y)) && Pieces.isAllied(colour, this.board.getPiece(x, y))) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    private ThreatMap getThreatMap(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return whiteThreats;
        } else {
            return blackThreats;
        }
    }

    private MoveMap getMoveMap(Colour colour) {
        return new MoveMap(colour, this.board.getPieces(), this.log, this.getThreatMap(Colour.opposite(colour)));
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
        if (oppKingPoint == null || this.board.getPiece(oppKingPoint) == null) {
            System.out.println(this.whiteKing);
            System.out.println(this.blackKing);
            System.out.println(this.board);
        }
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
                System.out.println(this.board);
                System.out.println(this.whiteThreats);
                System.out.println(this.blackThreats);
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(oppColour, this.board.getPieces(), this.log, this.getThreatMap(this.player));
            for (Point c : causingCheck.getPath()) {
                // Yes, there is at least one non-king piece that can move to a point along the path causing check
                if (!moveMap.hasNoMove(c, true)) {
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

    private List<Action> getActionsAgainstCheck(Colour playerInCheck) {
        List<Action> actions = new ArrayList<>();
        // This method assumes a player is in check
        Colour causingCheck = Colour.opposite(playerInCheck);
        Point inCheckKing = this.getKingPosition(playerInCheck);
        MoveSet inCheckMoves = this.board.getPiece(inCheckKing).getMoves(this.board.getPieces(), this.log,
                this.getThreatMap(causingCheck));

        if (inCheckMoves != null && !inCheckMoves.isEmpty()) {
            for (Point p : inCheckMoves.getPoints()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (this.getThreatMap(this.player).hasNoThreats(p)) {
                    // Yes, so the king is not in checkmate
                    actions.add(new Action(playerInCheck, inCheckKing, p));
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Piece> sourcesOfCheck = this.getThreatMap(causingCheck).getPieces(inCheckKing);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return List.of();
        }

        for (Piece attacker : sourcesOfCheck) {
            // Can this piece be captured by the opponent?
            Set<Piece> defenders = this.getThreatMap(playerInCheck).getPieces(attacker.getPoint());
            for (Piece defender : defenders) {
                actions.add(new Action(playerInCheck, defender.getPoint(), attacker.getPoint()));
            }
            // Can a piece block its path?
            Movement moveCausingCheck = attacker.getMoves(this.board.getPieces(), this.log).getMove(inCheckKing);
            if (moveCausingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = this.getMoveMap(playerInCheck);
            for (Point pointOnPath : moveCausingCheck.getPath()) {
                for (Piece blocker : moveMap.getPieces(pointOnPath)) {
                    actions.add(new Action(playerInCheck, blocker.getPoint(), pointOnPath));
                }
            }
        }
        return actions;
    }
}
