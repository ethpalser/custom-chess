package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.MoveMap;
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
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.view.GameView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChessGame implements Game {

    private final Space space; // temporary
    private final Board<Coordinate> board;
    private final Log<Coordinate, Piece> log;
    private final ThreatMap whiteThreats;
    private final ThreatMap blackThreats;

    private GameStatus status;
    private Colour player;
    private Point whiteKing;
    private Point blackKing;
    private int turn;
    private Point promotePoint;

    public ChessGame(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }
        this.space = new Plane(8, 8);
        this.board = board;
        this.log = log;
        this.status = GameStatus.PENDING;
        for (Piece p : this.board) {
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = (Point) p.getCoordinate();
                } else {
                    this.blackKing = (Point) p.getCoordinate();
                }
            }
        }
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.space);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.space);
        this.turn = log.size() + 1;
        this.player = this.turn % 2 != 0 ? Colour.WHITE : Colour.BLACK;
    }

    public ChessGame(GameView view) {
        this.turn = Math.max(view.getTurn(), 1);
        this.player = this.turn % 2 != 0 ? Colour.WHITE : Colour.BLACK;
        this.log = new ChessLog();
        this.space = new Plane(view.getBoard().getWidth() - 1, view.getBoard().getLength() - 1);
        // Todo: Remove views
        PieceFactory factory = new CustomPieceFactory(view.getPieceSpecs(), this.log, null);
        this.board = new ChessBoard(this.space, factory, view.getBoard().getPieces());
        for (Piece p : this.board) {
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = (Point) p.getCoordinate();
                } else {
                    this.blackKing = (Point) p.getCoordinate();
                }
            }
        }
        // this.log.addAll(this.board.getPieces(), view.getLog()); // todo: refactor log, it is a pain to recreate
        this.whiteThreats = new ThreatMap(Colour.WHITE, this.space);
        this.blackThreats = new ThreatMap(Colour.BLACK, this.space);
        this.status = checkGameStatus();
    }

    @Override
    public Board<Coordinate> getBoard() {
        return this.board;
    }

    @Override
    public Log<Coordinate, Piece> getLog() {
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

    public LogEntry<Coordinate, Piece> movePiece(Point start, Point end,
            Log<Coordinate, Piece> log, ThreatMap threatMap) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        Piece piece = this.board.get(start);
        if (piece == null) {
            throw new IllegalActionException("piece cannot move as it does not exist at " + start);
        }

        Movement move = piece.getMoves(this.board, log, threatMap).getMove(end);
        if (move == null) {
            throw new IllegalActionException("piece (" + piece + ") cannot move to " + end);
        }
        Piece captured = this.board.get(end);

        LogEntry<Coordinate, Piece> response = new ChessLogEntry(start, end, piece, captured, move.getFollowUpMove());

        this.board.remove(end);
        this.board.remove(start);
        this.board.add(end, piece);
        piece.move(end);

        LogEntry<Coordinate, Piece> followUp = move.getFollowUpMove();
        if (followUp != null) {
            Piece toForcePush = followUp.getStartObject();
            this.board.remove(followUp.getStart());
            if (followUp.getEnd() != null) {
                this.board.add(followUp.getEnd(), toForcePush);
            }
        }
        this.board.remove(null);
        return response;
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
            return GameStatus.NO_CHANGE;
        }

        Piece movingPiece = this.board.get(start);
        if (movingPiece == null) {
            return GameStatus.NO_CHANGE;
        }
        if (isNotAllowedToMove(movingPiece)) {
            return GameStatus.NO_CHANGE;
        }
        LogEntry<Coordinate, Piece> entry = this.movePiece(start, end, this.log,
                this.getThreatMap(Colour.opposite(this.player)));
        this.log.push(entry);
        this.updateKingPosition(movingPiece, end);

        // Update opponent's threats with the move performed
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board, this.log, start);
        this.getThreatMap(Colour.opposite(this.player)).refreshThreats(this.board, this.log, end);
        // Does moving this piece put turn player in check? (opponent's updated threats now include turn player's king)
        if (this.isKingInCheck(this.player)) {
            LogEntry<Coordinate, Piece> logEntry = this.log.pop();
            // soft undo update, which does not alter turn or player nor check game status
            this.undoLogEntryToBoard(logEntry.getSubLogEntry());
            this.undoLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
            this.updateKingPosition(movingPiece, start);
            return GameStatus.NO_CHANGE;
        }

        // Update remaining threats
        this.getThreatMap(this.player).refreshThreats(this.board, this.log, start);
        this.getThreatMap(this.player).refreshThreats(this.board, this.log, end);
        if (entry.getSubLogEntry() != null) {
            this.applyLogEntryToThreats(entry.getSubLogEntry());
        }

        if (this.board.get(start) != null) {
            throw new IllegalActionException("cannot perform move as it cannot move to " + end);
        }

        // Promote the piece if it can be promoted
        List<String> promoteOptions = movingPiece.promoteOptions();
        if (movingPiece.canPromote(this.board)) {
            this.promotePoint = end;
            if (!promoteOptions.isEmpty()) {
                // TEMPORARY use only the first option for promotion
                this.promotePiece(movingPiece.promoteOptions().get(0));
            }
        } else {
            // Piece was not promoted (if promotion was not enforced), so remove ability to promote it
            this.promotePoint = null;
        }

        this.status = this.checkGameStatus();
        this.player = Colour.opposite(this.player);
        this.turn++;
        return this.status;
    }

    public void promotePiece(String selection) {
        if (this.promotePoint == null) {
            throw new IllegalActionException("cannot promote a piece when there are none to promote.");
        }
        Piece promoting = this.board.get(this.promotePoint);
        // Manually modify piece's string then convert it into a piece
        String pieceStr = Pieces.asString(promoting, selection);
        Piece replacement;
        if (PieceType.fromCode(selection) == PieceType.CUSTOM) {
            // CustomPieceFactory should load custom piece specifications to determine how to make the custom piece
            PieceFactory factory = new CustomPieceFactory(Map.of(), this.log, this.space);
            PieceStringTokenizer tokenizer = new PieceStringTokenizer(pieceStr);
            // colour then code
            replacement = factory.create(Colour.fromCode(tokenizer.nextToken()), tokenizer.nextToken());
        } else {
            // Build a standard piece using information from the piece string
            replacement = Pieces.fromString(pieceStr);
        }
        // Update the board and latest log with this promotion
        this.board.add(this.promotePoint, replacement);
        this.log.peek().setPromotion(replacement);
    }

    @Override
    public GameStatus undoUpdate(int beforeCurrent, boolean saveUndone) {
        for (int i = 0; i < beforeCurrent; i++) {
            LogEntry<Coordinate, Piece> logEntry;
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

            this.updateKingPosition(logEntry.getStartObject(), (Point) logEntry.getStart());
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
            this.turn--;
        }
        return this.status;
    }

    private void undoLogEntryToBoard(LogEntry<Coordinate, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        this.board.add(logEntry.getEnd(), logEntry.getEndObject());
        this.board.add(logEntry.getStart(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(false);
        }

    }

    @Override
    public GameStatus redoUpdate(int afterCurrent) {
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry<Coordinate, Piece> logEntry = this.log.redo();
            if (logEntry == null) {
                break;
            }
            this.redoLogEntryToBoard(logEntry);
            this.applyLogEntryToThreats(logEntry);
            if (logEntry.getSubLogEntry() != null) {
                this.redoLogEntryToBoard(logEntry.getSubLogEntry());
                this.applyLogEntryToThreats(logEntry.getSubLogEntry());
            }

            Piece promoted = logEntry.getPromotion();
            if (promoted != null) {
                this.board.add(promoted.getCoordinate(), promoted);
            }

            this.updateKingPosition(logEntry.getStartObject(), (Point) logEntry.getEnd());
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
            this.turn++;
        }
        return this.status;
    }

    private void redoLogEntryToBoard(LogEntry<Coordinate, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        this.board.add(logEntry.getEnd(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(true);
        }
        // Remove the piece at the start point
        this.board.add(logEntry.getStart(), null);
    }

    private void applyLogEntryToThreats(LogEntry<Coordinate, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        // End can be null when removing a piece
        if (logEntry.getEnd() != null) {
            this.whiteThreats.refreshThreats(this.board, this.log, (Point) logEntry.getEnd());
            this.blackThreats.refreshThreats(this.board, this.log, (Point) logEntry.getEnd());
        }
        this.whiteThreats.refreshThreats(this.board, this.log, (Point) logEntry.getStart());
        this.blackThreats.refreshThreats(this.board, this.log, (Point) logEntry.getStart());
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
            for (Piece piece : this.board) {
                if (Pieces.isAllied(this.player, piece)) {
                    MoveSet moves = piece.getMoves(this.board, this.log,
                            this.getThreatMap(Colour.opposite(piece.getColour())));
                    for (Movement m : moves.toSet()) {
                        Path path = m.getPath();
                        if (path != null && path.length() > 0) {
                            // The last point in a path is a potential capture
                            potentialCaptures.add(new Action(piece.getColour(), (Point) piece.getCoordinate(),
                                    path.getPoint(path.length() - 1)));
                            // Remaining points are quiet actions (no captures)
                            for (int i = 0; i < path.length() - 1; i++) {
                                quietActions.add(new Action(piece.getColour(), (Point) piece.getCoordinate(), path.getPoint(i)));
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
                + this.whiteThreats.evaluate(this.board)
                + this.blackThreats.evaluate(this.board);
    }

    public String toJson() {
        GameView info = new GameView(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(info);
    }

    public static Game fromJson(String json) {
        if (json == null) {
            return null;
        }
        GameView info = new Gson().fromJson(json, GameView.class);
        return new ChessGame(info);
    }

    // PRIVATE METHODS

    private int evaluateBoardState() {
        int whiteSum = 0;
        int blackSum = 0;
        for (Piece p : this.board) {
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
                MoveSet moveSet = p.getMoves(this.board, this.log,
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

    private ThreatMap getThreatMap(Colour colour) {
        if (Colour.WHITE.equals(colour)) {
            return whiteThreats;
        } else {
            return blackThreats;
        }
    }

    private MoveMap getMoveMap(Colour colour) {
        return new MoveMap(colour, this.board, this.log, this.getThreatMap(Colour.opposite(colour)));
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
        if (oppKingPoint == null || this.board.get(oppKingPoint) == null) {
        }
        // Assuming King is in check
        MoveSet oppKingMoveSet = this.board.get(oppKingPoint)
                .getMoves(this.board, this.log, this.getThreatMap(this.player));
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
            Set<Piece> defenders = this.getThreatMap(oppColour).getPieces((Point) p.getCoordinate());
            if (!defenders.isEmpty()) {
                // Yes, as this piece can be captured by a non-king, the opponent has a legal move to prevent check
                return false;
            }
            // Can a piece block its path?
            Movement causingCheck = p.getMoves(this.board, this.log).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(oppColour, this.board, this.log, this.getThreatMap(this.player));
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
        if (this.board.count() <= 2) {
            return true;
        }
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece p : this.board) {
            if (!Pieces.isAllied(this.player, p)) {
                opponentPieces.add(p);
            }
        }
        for (Piece p : opponentPieces) {
            if (!p.getMoves(this.board, this.log, this.getThreatMap(this.player)).isEmpty()) {
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
        MoveSet inCheckMoves = this.board.get(inCheckKing).getMoves(this.board, this.log,
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
            Set<Piece> defenders = this.getThreatMap(playerInCheck).getPieces((Point) attacker.getCoordinate());
            for (Piece defender : defenders) {
                actions.add(new Action(playerInCheck, (Point) defender.getCoordinate(), (Point) attacker.getCoordinate()));
            }
            // Can a piece block its path?
            Movement moveCausingCheck = attacker.getMoves(this.board, this.log).getMove(inCheckKing);
            if (moveCausingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = this.getMoveMap(playerInCheck);
            for (Point pointOnPath : moveCausingCheck.getPath()) {
                for (Piece blocker : moveMap.getPieces(pointOnPath)) {
                    actions.add(new Action(playerInCheck, (Point) blocker.getCoordinate(), pointOnPath));
                }
            }
        }
        return actions;
    }
}
