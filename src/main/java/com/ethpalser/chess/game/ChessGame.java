package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.event.MoveEvent;
import com.ethpalser.chess.game.logic.Heuristics;
import com.ethpalser.chess.game.state.AwaitState;
import com.ethpalser.chess.game.state.EndState;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.game.state.GameState;
import com.ethpalser.chess.game.state.ReadyState;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChessGame implements Game {

    private final GameContext context;
    private int turn;
    private GameState state; // Defines what events are handled
    private GameStatus status; // Describes the most recent result

    public ChessGame() {
        this(new GameOptions(), null);
    }

    public ChessGame(GameOptions options, GameSaveData saveData) {
        if (options == null) {
            throw new IllegalArgumentException("game options cannot be null");
        }
        if (saveData == null) {
            this.context = new GameContext(options);
            this.turn = 1;
            this.status = GameStatus.ONGOING;
            this.state = new ReadyState(this.context);
        } else {
            this.context = new GameContext(options, saveData.pieceNotations(), saveData.logNotations());
            this.turn = saveData.logNotations() == null ? 1 : saveData.logNotations().length;
            this.status = checkGameStatus();
            GamePrompt prompt = this.context.getPrompt();
            if (GameStatus.isCompletedGameStatus(this.status)) {
                this.state = new EndState(this.context);
            } else if (prompt != null) {
                this.state = new AwaitState(this.context, prompt.eventType(), prompt.choices());
            } else {
                this.state = new ReadyState(this.context);
            }
        }
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

    public GameStatus updateGame(Coordinate start, Coordinate end, Colour player) {
        Colour expectedPlayer = this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
        if (!expectedPlayer.equals(player)) {
            return GameStatus.NO_CHANGE;
        }
        MoveEvent event = new MoveEvent(start, end);
        try {
            this.state.update(event);
        } catch (IllegalActionException | IndexOutOfBoundsException ex) {
            System.err.println(ex.getMessage());
            if (GameStatus.isCompletedGameStatus(this.status)) {
                return this.status;
            } else {
                return GameStatus.NO_CHANGE;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.err.println(this.context.getBoard());
            throw ex;
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
            GameContext.Record ctxRecord = this.context.toRecord();

            for (Piece piece : ctxRecord.getBoard()) {
                if (piece == null) {
                    continue;
                }
                if (Pieces.isAllied(this.currentPlayer(), piece)) {
                    MoveSet moves = piece.getMoves(ctxRecord);
                    for (Move m : moves.moves()) {
                        Path path = m.path();
                        if (path != null && path.length() > 0) {
                            // The last point in a path is a potential capture
                            potentialCaptures.add(new Action(piece.getColour(), piece.getCoordinate(),
                                    path.getPoint(path.length() - 1)));
                            // Remaining points are quiet actions (no captures)
                            for (int i = 0; i < path.length() - 1; i++) {
                                quietActions.add(new Action(piece.getColour(), piece.getCoordinate(),
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
        // Using Record here to avoid redundant copying downstream
        GameContext.Record ctxRecord = this.context.toRecord();
        int whiteSum = 0;
        int blackSum = 0;
        for (Piece p : ctxRecord.getBoard()) {
            if (Colour.WHITE.equals(p.getColour())) {
                whiteSum += Heuristics.pieceValue(ctxRecord, p);
            } else {
                blackSum += Heuristics.pieceValue(ctxRecord, p);
            }
        }
        int whitePawn = Heuristics.pawnValue(ctxRecord, Colour.WHITE);
        int blackPawn = Heuristics.pawnValue(ctxRecord, Colour.BLACK);
        return whiteSum + whitePawn - (blackSum + blackPawn);
    }

    @Override
    public GameSaveData createSaveData() {
        Board<Coordinate> board = this.context.getBoard();
        String[] pieces = new String[board.count()];
        int i = 0;
        for (Piece piece : board) {
            pieces[i] = Pieces.asString(piece);
            i++;
        }

        Log<Coordinate, Piece> log = this.context.getLog();
        String[] notations = new String[log.size()];
        // Todo: Use newer log with chess notation

        GamePrompt prompt = this.context.getPrompt();
        return new GameSaveData(pieces, notations, prompt);
    }

    // PRIVATE METHODS

    private Colour currentPlayer() {
        return this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
    }

    private GameStatus checkGameStatus() {
        Colour opponent = Colour.opposite(this.currentPlayer());
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        boolean opponentInCheck = !this.context.getThreats(Colour.opposite(opponent))
                .hasNoThreats(this.context.getKingCoordinate(opponent));
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
        GameContext.Record ctxRecord = this.context.toRecord();

        Colour oppColour = Colour.opposite(this.currentPlayer());
        Coordinate oppKingPoint = this.context.getKingCoordinate(Colour.opposite(this.currentPlayer()));
        if (oppKingPoint == null || ctxRecord.getBoard().get(oppKingPoint) == null) {
            throw new IllegalStateException("opponent king is missing");
        }
        // Assuming King is in check
        MoveSet oppKingMoveSet = ctxRecord.getBoard().get(oppKingPoint).getMoves(ctxRecord);
        if (oppKingMoveSet != null && !oppKingMoveSet.isEmpty()) {
            for (Coordinate moveCoordinate : oppKingMoveSet.coordinates()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (ctxRecord.getThreats(this.currentPlayer()).hasNoThreats(moveCoordinate)) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Coordinate> sourcesOfCheck = ctxRecord.getThreats(this.currentPlayer()).getThreats(oppKingPoint);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return true;
        }
        // There is only one threatening check
        for (Coordinate defendCoordinate : sourcesOfCheck) {
            // Can this piece be captured by the opponent?
            Set<Coordinate> defenders = ctxRecord.getThreats(oppColour).getThreats(defendCoordinate);
            if (!defenders.isEmpty()) {
                // Yes, as this piece can be captured by a non-king, the opponent has a legal move to prevent check
                return false;
            }
            // Can a piece block its path?

            Move causingCheck = ctxRecord.getBoard().get(defendCoordinate).getMoves(ctxRecord).getMove(oppKingPoint);
            if (causingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(oppColour, this.context.toRecord());
            for (Coordinate c : causingCheck.path()) {
                // Yes, there is at least one non-king piece that can move to a point along the path causing check
                if (!moveMap.hasNoMove(c, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isStalemate() {
        GameContext.Record ctxRecord = this.context.toRecord();
        // Only kings remain, which is a stalemate
        if (ctxRecord.getBoard().count() <= 2) {
            return true;
        }
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece p : ctxRecord.getBoard()) {
            if (!Pieces.isAllied(this.currentPlayer(), p)) {
                opponentPieces.add(p);
            }
        }
        for (Piece p : opponentPieces) {
            if (!p.getMoves(ctxRecord).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<Action> getActionsAgainstCheck(Colour playerInCheck) {
        GameContext.Record ctxRecord = this.context.toRecord();

        List<Action> actions = new ArrayList<>();
        // This method assumes a player is in check
        Colour causingCheck = Colour.opposite(playerInCheck);
        Coordinate inCheckKing = this.context.getKingCoordinate(playerInCheck);
        MoveSet inCheckMoves = ctxRecord.getBoard().get(inCheckKing).getMoves(this.context.toRecord());

        if (inCheckMoves != null && !inCheckMoves.isEmpty()) {
            for (Coordinate p : inCheckMoves.coordinates()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (ctxRecord.getThreats(this.currentPlayer()).hasNoThreats(p)) {
                    // Yes, so the king is not in checkmate
                    actions.add(new Action(playerInCheck, inCheckKing, p));
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Coordinate> sourcesOfCheck = ctxRecord.getThreats(causingCheck).getThreats(inCheckKing);
        if (sourcesOfCheck.size() > 1) {
            // A piece cannot simultaneously capture one piece and block another, as neither were original blocked
            return List.of();
        }

        for (Coordinate attacker : sourcesOfCheck) {
            Piece attackerPiece = ctxRecord.getBoard().get(attacker);
            if (attackerPiece == null) {
                throw new IllegalStateException("attacker that is causing check cannot be null");
            }
            // Can this piece be captured by the opponent?
            Set<Coordinate> defenders = ctxRecord.getThreats(playerInCheck).getThreats(attacker);
            for (Coordinate defender : defenders) {
                actions.add(new Action(playerInCheck, defender, attacker));
            }
            // Can a piece block its path?
            Move moveCausingCheck = attackerPiece.getMoves(this.context.toRecord()).getMove(inCheckKing);
            if (moveCausingCheck == null) {
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(playerInCheck, this.context.toRecord());
            for (Coordinate pathCoordinate : moveCausingCheck.path()) {
                for (Piece blocker : moveMap.getPieces(pathCoordinate)) {
                    actions.add(new Action(playerInCheck, blocker.getCoordinate(), pathCoordinate));
                }
            }
        }
        return actions;
    }
}
