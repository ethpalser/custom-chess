package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.event.MoveEvent;
import com.ethpalser.chess.game.state.EndState;
import com.ethpalser.chess.game.state.GameState;
import com.ethpalser.chess.game.state.ReadyState;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
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
import java.util.Set;

public class ChessGame implements Game {

    private final GameContext context;
    private int turn;
    private GameState state; // Defines what events are handled
    private GameStatus status; // Describes the most recent result

    public ChessGame() {
        this.context = new GameContext(new GameOptions());
        this.turn = 1;
        this.state = new ReadyState(this.context);
        this.status = GameStatus.ONGOING;
    }

    // Use default options
    @Deprecated
    public ChessGame(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        if (board == null || log == null) {
            throw new NullPointerException("arguments cannot be null");
        }
        this.context = new GameContext(new GameOptions(), board, log);
        GameStatus statusCheck = checkGameStatus();
        if (GameStatus.isCompletedGameStatus(statusCheck)) {
            this.state = new EndState(this.context);
        } else {
            this.state = new ReadyState(this.context);
        }
        this.status = statusCheck;
        this.turn = log.size() + 1;
    }

    // Use GameOptions
    @Deprecated
    public ChessGame(GameView view) {
        Log<Coordinate, Piece> newLog = new ChessLog();
        // Todo: Remove views
        Space space = new Plane(view.getBoard().getWidth() - 1, view.getBoard().getLength() - 1);
        PieceFactory factory = new CustomPieceFactory(view.getPieceSpecs(), newLog, space);
        Board<Coordinate> newBoard = new ChessBoard(space, factory, view.getBoard().getPieces());

        this.context = new GameContext(new GameOptions(), newBoard, newLog); // GameOptions currently not supported
        GameStatus statusCheck = checkGameStatus();
        if (GameStatus.isCompletedGameStatus(statusCheck)) {
            this.state = new EndState(this.context);
        } else {
            this.state = new ReadyState(this.context);
        }
        this.status = statusCheck;
        this.turn = Math.max(view.getTurn(), 1);
    }

    @Override
    public GameInfo info() {
        return new GameInfo(this.turn, this.evaluateState(), this.status, this.context);
    }

    @Override
    public GameStatus getStatus() {
        return status;
    }

    @Override
    public int getTurn() {
        return this.turn;
    }

    @Deprecated
    @Override
    public GameStatus updateGame(Action action) throws IllegalActionException {
        if (action == null) {
            throw new IllegalActionException("action cannot be null");
        }
        return this.updateGame(action.getStart(), action.getEnd(), action.getColour());
    }

    public GameStatus updateGame(Point start, Point end, Colour player) {
        Colour expectedPlayer = this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
        if (!expectedPlayer.equals(player)) {
            return GameStatus.NO_CHANGE;
        }
        MoveEvent event = new MoveEvent(start, end);
        try {
            this.state.update(event);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (GameStatus.isCompletedGameStatus(this.status)) {
                return this.status;
            } else {
                return GameStatus.NO_CHANGE;
            }
        }
        this.status = this.checkGameStatus();
        this.turn++;
        return this.status;
    }

    public GameStatus undoUpdate(int beforeCurrent, boolean saveUndone) {
        Board<Coordinate> boardCopy = this.context.getBoard();
        Log<Coordinate, Piece> logCopy = this.context.getLog();
        for (int i = 0; i < beforeCurrent; i++) {
            LogEntry<Coordinate, Piece> logEntry;
            if (saveUndone) {
                logEntry = logCopy.undo();
            } else {
                logEntry = logCopy.pop();
            }
            if (logEntry == null) {
                break;
            }
            if (logEntry.getSubLogEntry() != null) {
                this.undoLogEntryToBoard(boardCopy, logEntry.getSubLogEntry());
            }
            this.undoLogEntryToBoard(boardCopy, logEntry);
            // Commit changes to context
            this.context.undo(this.currentPlayer(), boardCopy, logCopy);
            this.status = this.checkGameStatus();
            this.turn--;
        }
        return this.status;
    }

    private void undoLogEntryToBoard(Board<Coordinate> board, LogEntry<Coordinate, Piece> logEntry) {
        if (board == null || logEntry == null) {
            return;
        }
        if (logEntry.getEndObject() != null) {
            board.add(logEntry.getEnd(), logEntry.getEndObject());
        } else {
            board.remove(logEntry.getEnd());
        }
        board.add(logEntry.getStart(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(false);
        }
    }

    @Override
    public GameStatus redoUpdate(int afterCurrent) {
        Board<Coordinate> boardCopy = this.context.getBoard();
        Log<Coordinate, Piece> logCopy = this.context.getLog();
        for (int i = 0; i < afterCurrent; i++) {
            LogEntry<Coordinate, Piece> logEntry = logCopy.redo();
            if (logEntry == null) {
                break;
            }
            this.redoLogEntryToBoard(boardCopy, logEntry);
            if (logEntry.getSubLogEntry() != null) {
                this.redoLogEntryToBoard(boardCopy, logEntry.getSubLogEntry());
            }

            Piece promoted = logEntry.getPromotion();
            if (promoted != null) {
                boardCopy.add(promoted.getCoordinate(), promoted);
            }

            this.context.update(this.currentPlayer(), boardCopy, logCopy);
            this.status = this.checkGameStatus();
            this.turn++;
        }
        return this.status;
    }

    private void redoLogEntryToBoard(Board<Coordinate> board, LogEntry<Coordinate, Piece> logEntry) {
        if (logEntry == null) {
            return;
        }
        board.add(logEntry.getEnd(), logEntry.getStartObject());
        if (logEntry.isFirstOccurrence()) {
            logEntry.getStartObject().setHasMoved(true);
        }
        board.remove(logEntry.getStart());
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
            Board<Coordinate> board = this.context.getBoard();
            Log<Coordinate, Piece> log = this.context.getLog();

            for (Piece piece : board) {
                if (piece == null) {
                    continue;
                }
                if (Pieces.isAllied(this.currentPlayer(), piece)) {
                    MoveSet moves = piece.getMoves(board, log,
                            this.getThreatMap(Colour.opposite(piece.getColour())));
                    for (Movement m : moves.toSet()) {
                        Path path = m.getPath();
                        if (path != null && path.length() > 0) {
                            // The last point in a path is a potential capture
                            potentialCaptures.add(new Action(piece.getColour(), (Point) piece.getCoordinate(),
                                    path.getPoint(path.length() - 1)));
                            // Remaining points are quiet actions (no captures)
                            for (int i = 0; i < path.length() - 1; i++) {
                                quietActions.add(new Action(piece.getColour(), (Point) piece.getCoordinate(),
                                        path.getPoint(i)));
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
        Board<Coordinate> board = this.context.getBoard();

        int whiteSum = 0;
        int blackSum = 0;
        for (Piece p : board) {
            if (Colour.WHITE.equals(p.getColour())) {
                whiteSum += Heuristics.pieceValue(this.context, p);
            } else {
                blackSum += Heuristics.pieceValue(this.context, p);
            }
        }
        // blackThreats should be a negative value
        return whiteSum + this.context.getThreats(Colour.WHITE).evaluate(board) -
                (blackSum + this.context.getThreats(Colour.BLACK).evaluate(board));
    }

    @Deprecated
    public String toJson() {
        GameView info = new GameView(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(info);
    }

    @Deprecated
    public static Game fromJson(String json) {
        if (json == null) {
            return null;
        }
        GameView info = new Gson().fromJson(json, GameView.class);
        return new ChessGame(info);
    }

    // PRIVATE METHODS

    private Colour currentPlayer() {
        return this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
    }

    private Point getKingPosition(Colour colour) {
        return (Point) this.context.getKingCoordinate(colour);
    }

    private ThreatMap getThreatMap(Colour colour) {
        return this.context.getThreats(colour);
    }

    private MoveMap getMoveMap(Colour colour) {
        return new MoveMap(colour, this.context.getBoard(), this.context.getLog(),
                this.getThreatMap(Colour.opposite(colour)));
    }

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.currentPlayer());
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        boolean opponentInCheck = !this.getThreatMap(Colour.opposite(opponent)).hasNoThreats(getKingPosition(opponent));
        if (opponentInCheck) {
            if (this.isCheckmate()) {
                nextStatus = GameStatus.colourWinStatus(this.currentPlayer());
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
        Board<Coordinate> boardRef = this.context.getBoard();
        Log<Coordinate, Piece> logRef = this.context.getLog();

        Colour oppColour = Colour.opposite(this.currentPlayer());
        Point oppKingPoint = this.getKingPosition(Colour.opposite(this.currentPlayer()));
        if (oppKingPoint == null || boardRef.get(oppKingPoint) == null) {
        }
        // Assuming King is in check
        MoveSet oppKingMoveSet = boardRef.get(oppKingPoint)
                .getMoves(boardRef, logRef, this.getThreatMap(this.currentPlayer()));
        if (oppKingMoveSet != null && !oppKingMoveSet.isEmpty()) {
            for (Point p : oppKingMoveSet.getPoints()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (this.getThreatMap(this.currentPlayer()).hasNoThreats(p)) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Piece> sourcesOfCheck = this.getThreatMap(this.currentPlayer()).getPieces(oppKingPoint);
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
            Movement causingCheck = p.getMoves(boardRef, logRef).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(oppColour, boardRef, logRef, this.getThreatMap(this.currentPlayer()));
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
        Board<Coordinate> boardRef = this.context.getBoard();
        Log<Coordinate, Piece> logRef = this.context.getLog();
        // Only kings remain, which is a stalemate
        if (boardRef.count() <= 2) {
            return true;
        }
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece p : boardRef) {
            if (!Pieces.isAllied(this.currentPlayer(), p)) {
                opponentPieces.add(p);
            }
        }
        for (Piece p : opponentPieces) {
            if (!p.getMoves(boardRef, logRef, this.getThreatMap(this.currentPlayer())).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<Action> getActionsAgainstCheck(Colour playerInCheck) {
        Board<Coordinate> boardRef = this.context.getBoard();
        Log<Coordinate, Piece> logRef = this.context.getLog();

        List<Action> actions = new ArrayList<>();
        // This method assumes a player is in check
        Colour causingCheck = Colour.opposite(playerInCheck);
        Point inCheckKing = this.getKingPosition(playerInCheck);
        MoveSet inCheckMoves = boardRef.get(inCheckKing).getMoves(boardRef, logRef,
                this.getThreatMap(causingCheck));

        if (inCheckMoves != null && !inCheckMoves.isEmpty()) {
            for (Point p : inCheckMoves.getPoints()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (this.getThreatMap(this.currentPlayer()).hasNoThreats(p)) {
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
                actions.add(new Action(playerInCheck, (Point) defender.getCoordinate(),
                        (Point) attacker.getCoordinate()));
            }
            // Can a piece block its path?
            Movement moveCausingCheck = attacker.getMoves(boardRef, logRef).getMove(inCheckKing);
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
