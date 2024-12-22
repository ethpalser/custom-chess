package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.event.MoveEvent;
import com.ethpalser.chess.game.state.GameState;
import com.ethpalser.chess.game.state.ReadyState;
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

    private final GameContext context;
    private GameState gameState;

    // Use default options
    @Deprecated
    public ChessGame(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }
        this.space = new Plane(8, 8);
        this.board = board;
        this.log = log;
        this.status = GameStatus.PENDING;
        for (Piece p : board) {
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = (Point) p.getCoordinate();
                } else {
                    this.blackKing = (Point) p.getCoordinate();
                }
            }
        }
        this.whiteThreats = new ThreatMap(Colour.WHITE, board, log, this.space);
        this.blackThreats = new ThreatMap(Colour.BLACK, board, log, this.space);
        this.turn = log.size() + 1;
        this.player = this.turn % 2 != 0 ? Colour.WHITE : Colour.BLACK;

        // Replacing all board information with GameContext
        this.context = new GameContext(new GameOptions(), board, log);
        this.gameState = new ReadyState(this.context);
    }

    // Use GameOptions
    @Deprecated
    public ChessGame(GameView view) {
        this.turn = Math.max(view.getTurn(), 1);
        this.player = this.turn % 2 != 0 ? Colour.WHITE : Colour.BLACK;
        Log<Coordinate, Piece> newLog = new ChessLog();
        this.log = newLog;
        this.space = new Plane(view.getBoard().getWidth() - 1, view.getBoard().getLength() - 1);
        // Todo: Remove views
        PieceFactory factory = new CustomPieceFactory(view.getPieceSpecs(), this.log, this.space);
        Board<Coordinate> newBoard = new ChessBoard(this.space, factory, view.getBoard().getPieces());
        this.board = newBoard;
        for (Piece p : newBoard) {
            if (p != null && PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.whiteKing = (Point) p.getCoordinate();
                } else {
                    this.blackKing = (Point) p.getCoordinate();
                }
            }
        }
        // this.log.addAll(this.board.getPieces(), view.getLog()); // todo: refactor log, it is a pain to recreate
        this.whiteThreats = new ThreatMap(Colour.WHITE, newBoard, newLog, this.space);
        this.blackThreats = new ThreatMap(Colour.BLACK, newBoard, newLog, this.space);

        this.context = new GameContext(new GameOptions(), newBoard, newLog); // GameOptions currently not supported
        this.gameState = new ReadyState(this.context);
        this.status = checkGameStatus();
    }

    @Deprecated
    @Override
    public Board<Coordinate> getBoard() {
        return this.context.getBoard();
    }

    @Deprecated
    @Override
    public Log<Coordinate, Piece> getLog() {
        return this.context.getLog();
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
            this.gameState.update(event);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (GameStatus.isCompletedGameStatus(this.status)) {
                return this.status;
            } else {
                return GameStatus.NO_CHANGE;
            }
        }
        this.status = this.checkGameStatus();
        this.player = Colour.opposite(this.player);
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
            this.context.undo(this.player, boardCopy, logCopy);
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
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

            this.context.update(this.player, boardCopy, logCopy);
            this.status = this.checkGameStatus();
            this.player = Colour.opposite(this.player);
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
                if (Pieces.isAllied(this.player, piece)) {
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
                whiteSum += this.getPieceValue(p);
            } else {
                blackSum += this.getPieceValue(p);
            }
        }
        // blackThreats should be a negative value
        return whiteSum + this.whiteThreats.evaluate(board) - (blackSum + this.blackThreats.evaluate(board));
    }

    @Deprecated
    public String toJson() {
        GameView info = new GameView(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(info);
    }

    @Override
    public GameInfo info() {
        return new GameInfo(this.turn, this.evaluateState(), this.checkGameStatus(), this.context);
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

    private int getPieceValue(Piece p) {
        if (p == null) {
            return 0;
        }
        Board<Coordinate> cBoard = this.context.getBoard();
        Log<Coordinate, Piece> cLog = this.context.getLog();

        int value;
        switch (PieceType.fromCode(p.getCode())) {
            case PAWN -> value = 1;
            case BISHOP, KNIGHT -> value = 3;
            case ROOK -> value = 5;
            case QUEEN -> value = 9;
            case CUSTOM -> {
                // Currently, this uses MoveSet, but this would be more accurate to use its blueprint
                MoveSet moveSet = p.getMoves(cBoard, cLog,
                        this.getThreatMap(Colour.opposite(p.getColour())));
                int numMoves = moveSet.getPoints().size();
                int base = (int) Math.ceil(numMoves / 3.0);
                value = base + base / 3;
            }
            default -> value = 0;
        }
        return value;
    }


    private Point getKingPosition(Colour colour) {
        return (Point) this.context.getKingCoordinate(colour);
    }

    private ThreatMap getThreatMap(Colour colour) {
        return this.context.getThreats(colour);
    }

    private MoveMap getMoveMap(Colour colour) {
        return new MoveMap(colour, this.context.getBoard(), this.context.getLog(), this.getThreatMap(Colour.opposite(colour)));
    }

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.player);
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        boolean opponentInCheck = !this.getThreatMap(Colour.opposite(opponent)).hasNoThreats(getKingPosition(opponent));
        if (opponentInCheck) {
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
        Board<Coordinate> boardRef = this.getBoard();
        Log<Coordinate, Piece> logRef = this.getLog();

        Colour oppColour = Colour.opposite(this.player);
        Point oppKingPoint = this.getKingPosition(Colour.opposite(this.player));
        if (oppKingPoint == null || boardRef.get(oppKingPoint) == null) {
        }
        // Assuming King is in check
        MoveSet oppKingMoveSet = boardRef.get(oppKingPoint)
                .getMoves(boardRef, logRef, this.getThreatMap(this.player));
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
            Movement causingCheck = p.getMoves(boardRef, logRef).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(oppColour, boardRef, logRef, this.getThreatMap(this.player));
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
        Board<Coordinate> boardRef = this.getBoard();
        Log<Coordinate, Piece> logRef = this.getLog();
        // Only kings remain, which is a stalemate
        if (boardRef.count() <= 2) {
            return true;
        }
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece p : boardRef) {
            if (!Pieces.isAllied(this.player, p)) {
                opponentPieces.add(p);
            }
        }
        for (Piece p : opponentPieces) {
            if (!p.getMoves(boardRef, logRef, this.getThreatMap(this.player)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<Action> getActionsAgainstCheck(Colour playerInCheck) {
        Board<Coordinate> boardRef = this.getBoard();
        Log<Coordinate, Piece> logRef = this.getLog();

        List<Action> actions = new ArrayList<>();
        // This method assumes a player is in check
        Colour causingCheck = Colour.opposite(playerInCheck);
        Point inCheckKing = this.getKingPosition(playerInCheck);
        MoveSet inCheckMoves = boardRef.get(inCheckKing).getMoves(boardRef, logRef,
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
