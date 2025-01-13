package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.exception.CoordinateOutOfBoundsException;
import com.ethpalser.chess.exception.IllegalMoveException;
import com.ethpalser.chess.exception.IllegalResultException;
import com.ethpalser.chess.exception.MissingPieceException;
import com.ethpalser.chess.exception.UnsupportedEventException;
import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.MoveEvent;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.game.logic.Heuristics;
import com.ethpalser.chess.game.state.AwaitState;
import com.ethpalser.chess.game.state.EndState;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.game.state.GameState;
import com.ethpalser.chess.game.state.ReadyState;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.MoveMap;
import com.ethpalser.chess.move.notation.ChessRecord;
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
            // Count changes in turn
            int turnCount = 1;
            for (ChessLog.Entry entry : this.context.getLog()) {
                ChessRecord rec = entry.notation().toRecord();
                // Ignore records that are either of these, as they are part of the same player's turn
                if (!rec.isFollowUp() && rec.promoteCode() == null) {
                    turnCount++;
                }
            }
            this.turn = turnCount;
            this.status = checkGameStatus(this.currentPlayer(), false);
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
        return new GameInfo(this.turn, this.score(), this.status, this.context);
    }

    @Override
    public GameContext context() {
        return this.context;
    }

    @Override
    public GameStatus status() {
        return status;
    }

    @Override
    public int turn() {
        return this.turn;
    }

    @Override
    public int score() {
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

    @Deprecated
    @Override
    public GameStatus update(Action action) throws IllegalMoveException {
        if (action == null) {
            throw new IllegalMoveException("Action is null");
        }
        GameEvent event = new MoveEvent(action.getColour(), action.getStart(), action.getEnd());
        return this.update(event);
    }

    @Override
    public GameStatus update(GameEvent event) {
        Colour expectedPlayer = this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
        if (!expectedPlayer.equals(event.player())) {
            return GameStatus.NO_CHANGE;
        }

        try {
            this.state = this.state.update(event);
        } catch (CoordinateOutOfBoundsException | IllegalResultException | MissingPieceException
                | UnsupportedEventException ex) {
            // These are regular exceptions that are expected and ignored with not changes to state
            return GameStatus.isCompletedGameStatus(this.status) ? this.status : GameStatus.NO_CHANGE;
        } catch (IllegalMoveException ex) {
            // These are irregular exceptions that can be ignored but should be handled
            System.err.println(ex.getMessage());
            return GameStatus.isCompletedGameStatus(this.status) ? this.status : GameStatus.NO_CHANGE;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.err.println(this.context.getBoard());
            throw ex;
        }
        this.status = this.checkGameStatus(this.currentPlayer(), false);
        this.turn++;
        return this.status;
    }

    @Override
    public GameStatus undo() {
        ChessLog log = this.context.getLog();
        if (log.peek() == null) {
            return GameStatus.NO_CHANGE;
        }
        boolean undoneBaseMove = false;
        while (!undoneBaseMove) {
            ChessLog.Entry peek = log.peek();
            if (peek == null) {
                break;
            }
            // Undo all non-base moves
            ChessRecord rec = peek.notation().toRecord();
            if (!rec.isFollowUp() && rec.promoteCode() == null) {
                undoneBaseMove = true;
            }
            GameEvent event = peek.event();
            // un-execute should be responsible for context updates
            event.unExecute(this.context);
        }

        // Commit changes to context
        this.turn--; // Reduce the turn count first to be on the player we just reverted the event(s) for.
        this.status = this.checkGameStatus(this.currentPlayer(), true);
        return this.status;
    }

    @Override
    public GameStatus redo() {
        ChessLog logCopy = this.context.toRecord().getLog();
        if (logCopy.peekUndone() == null) {
            return GameStatus.NO_CHANGE;
        }

        GameEvent event = logCopy.peekUndone().event();
        // un-execute should be responsible for context updates
        event.execute(this.context);

        boolean atNextBaseMoved = false;
        while (!atNextBaseMoved) {
            ChessLog.Entry toRedo = logCopy.peekUndone();
            if (toRedo == null) {
                break;
            }
            // Redo the first base move and all non-base moves up to the next
            ChessRecord rec = toRedo.notation().toRecord();
            if (!rec.isFollowUp() && rec.promoteCode() == null) {
                atNextBaseMoved = true;
            } else {
                event = toRedo.event();
                event.execute(this.context);
            }
        }
        // Commit changes to context
        this.status = this.checkGameStatus(this.currentPlayer(), true);
        this.turn++; // Increase the turn count after, as we need to check the status of the executing player
        return this.status;
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
    public GameSaveData createSaveData() {
        Board<Coordinate> board = this.context.getBoard();
        String[] pieces = new String[board.count()];
        int i = 0;
        for (Piece piece : board) {
            pieces[i] = Pieces.asString(piece);
            i++;
        }

        ChessLog log = this.context.getLog();
        String[] notations = new String[log.size()];
        int k = 0;
        for (ChessLog.Entry entry : log) {
            notations[k] = entry.notation().toString();
            k++;
        }

        GamePrompt prompt = this.context.getPrompt();
        return new GameSaveData(pieces, notations, prompt, null);
    }

    // PRIVATE METHODS

    private Colour currentPlayer() {
        return this.turn % 2 == 1 ? Colour.WHITE : Colour.BLACK;
    }

    private GameStatus checkGameStatus(Colour playerColour, boolean isUndo) {
        Colour lastMovedPlayer = !isUndo ? playerColour : Colour.opposite(playerColour);
        Colour opponentColour = Colour.opposite(lastMovedPlayer);
        // Is there a check, checkmate or stalemate?
        GameStatus nextStatus;
        boolean opponentInCheck = !this.context.getThreats(lastMovedPlayer)
                .hasNoThreats(this.context.getKingCoordinate(opponentColour));
        if (opponentInCheck) {
            if (this.isCheckmate(lastMovedPlayer)) {
                nextStatus = GameStatus.colourWinStatus(lastMovedPlayer);
            } else {
                nextStatus = GameStatus.colourInCheckStatus(opponentColour);
            }
        } else {
            if (this.isStalemate(lastMovedPlayer)) {
                nextStatus = GameStatus.STALEMATE;
            } else {
                nextStatus = GameStatus.ONGOING;
            }
        }
        return nextStatus;
    }

    private boolean isCheckmate(Colour playerColour) {
        GameContext.Record ctxRecord = this.context.toRecord();

        Colour oppColour = Colour.opposite(playerColour);
        Coordinate oppKingPoint = this.context.getKingCoordinate(Colour.opposite(playerColour));
        if (oppKingPoint == null || ctxRecord.getBoard().get(oppKingPoint) == null) {
            throw new IllegalStateException("opponent king is missing");
        }
        // Assuming King is in check
        MoveSet oppKingMoveSet = ctxRecord.getBoard().get(oppKingPoint).getMoves(ctxRecord);
        if (oppKingMoveSet != null && !oppKingMoveSet.isEmpty()) {
            for (Coordinate moveCoordinate : oppKingMoveSet.coordinates()) {
                // Is there a location the opponent king can move to that is not threatened by the opponent?
                if (ctxRecord.getThreats(playerColour).hasNoThreats(moveCoordinate)) {
                    // Yes, so the king is not in checkmate
                    return false;
                }
            }
        }

        // The opponent king cannot move, but can another piece move to block all sources of check?
        Set<Coordinate> sourcesOfCheck = ctxRecord.getThreats(playerColour).getThreats(oppKingPoint);
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
            MoveMap moveMap = new MoveMap(oppColour, ctxRecord);
            for (Coordinate c : causingCheck.path()) {
                // Yes, there is at least one non-king piece that can move to a point along the path causing check
                if (!moveMap.hasNoMove(c, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isStalemate(Colour playerColour) {
        GameContext.Record ctxRecord = this.context.toRecord();
        // Only kings remain, which is a stalemate
        if (ctxRecord.getBoard().count() <= 2) {
            return true;
        }
        // Are there any opponent pieces that can move?
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece p : ctxRecord.getBoard()) {
            if (!Pieces.isAllied(playerColour, p)) {
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
        MoveSet inCheckMoves = ctxRecord.getBoard().get(inCheckKing).getMoves(ctxRecord);

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
            Move moveCausingCheck = attackerPiece.getMoves(ctxRecord).getMove(inCheckKing);
            if (moveCausingCheck == null) {
                System.err.println("Player in check " + playerInCheck);
                System.err.println(this.context.getBoard());
                throw new NullPointerException("exception in game state, move causing check should not be null");
            }
            MoveMap moveMap = new MoveMap(playerInCheck, ctxRecord);
            for (Coordinate pathCoordinate : moveCausingCheck.path()) {
                for (Piece blocker : moveMap.getPieces(pathCoordinate)) {
                    actions.add(new Action(playerInCheck, blocker.getCoordinate(), pathCoordinate));
                }
            }
        }
        return actions;
    }
}
