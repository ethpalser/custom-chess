package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.config.MoveSpec;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GameContext {

    private Board<Coordinate> board;
    private ChessLog log;
    private ThreatMap wThreats;
    private ThreatMap bThreats;
    private Coordinate wKing;
    private Coordinate bKing;

    private Map<String, List<MoveSpec>> moveSpecs;

    private GamePrompt prompt; // Nullable

    public GameContext() {
        this(new GameOptions(), null, null);
    }

    public GameContext(GameOptions options) {
        this(options, null, null);
    }

    public GameContext(GameOptions options, String[] pieceNotations, String[] logNotations) {
        if (logNotations == null) {
            this.log = new ChessLog();
        } else {
            this.log = new ChessLog(logNotations);
        }
        Space space = new Plane(options.width(), options.length(), options.unavailable());
        if (pieceNotations == null) {
            this.board = new ChessBoard(space, new CustomPieceFactory(options.pieceSpecs()));
        } else {
            this.board = new ChessBoard(space, new CustomPieceFactory(options.pieceSpecs()),
                    Arrays.asList(pieceNotations));
        }
        this.wThreats = new ThreatMap(Colour.WHITE, space, this.board, this.log);
        this.bThreats = new ThreatMap(Colour.BLACK, space, this.board, this.log);
        for (Coordinate c : this.board.occupied()) {
            Piece p = this.board.get(c);
            if (PieceType.KING.toCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.wKing = c;
                } else {
                    this.bKing = c;
                }
            }
        }
        // Creating ThreatMap may not have had the king threatened, as the threats were just being made
        this.refreshThreats(Colour.WHITE, this.wKing, this.toRecord());
        this.refreshThreats(Colour.WHITE, this.wKing, this.toRecord());
        this.moveSpecs = options.pieceSpecs();
    }


    public Board<Coordinate> getBoard() {
        return this.board;
    }

    public ChessLog getLog() {
        return this.log;
    }

    public ThreatMap getThreats(Colour colour) {
        return Colour.WHITE.equals(colour) ? this.wThreats : this.bThreats;
    }

    public Coordinate getKingCoordinate(Colour colour) {
        return Colour.WHITE.equals(colour) ? this.wKing : this.bKing;
    }

    public Map<String, List<MoveSpec>> getMoveSpecs() {
        return this.moveSpecs;
    }

    public GamePrompt getPrompt() {
        return this.prompt;
    }

    public void raisePrompt(GamePrompt prompt) {
        if (prompt == null) {
            throw new IllegalArgumentException("Cannot raise nothing to prompt");
        }
        this.prompt = prompt;
    }

    public void clearPrompt() {
        this.prompt = null;
    }

    public void update(Colour turn, Board<Coordinate> updatedBoard, ChessLog updatedLog) {
        this.update(turn, updatedBoard, updatedLog, false);
    }

    public void undo(Colour turn, Board<Coordinate> updatedBoard, ChessLog updatedLog) {
        this.update(turn, updatedBoard, updatedLog, true);
    }

    private void update(Colour turn, Board<Coordinate> updatedBoard, ChessLog updatedLog,
            boolean isUndo) {
        if (turn == null || updatedBoard == null) {
            throw new IllegalArgumentException("Cannot update game as one or more arguments are null.");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        GameContext.Record ctxRecord = new GameContext.Record(new ChessBoard((ChessBoard) updatedBoard), updatedLog,
                new ThreatMap(this.wThreats), new ThreatMap(this.bThreats));
        // Update threats wherever there was a change
        for (Coordinate change : this.getBoardChanges(updatedBoard)) {
            Piece p = updatedBoard.get(change);
            if (p != null && PieceType.KING.equals(PieceType.fromCode(p.getCode()))) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.wKing = change;
                } else {
                    this.bKing = change;
                }
            }
            this.refreshThreats(Colour.WHITE, change, ctxRecord);
            this.refreshThreats(Colour.BLACK, change, ctxRecord);
        }
        // After all changes, did the turn player put itself into check?
        if (!isUndo && !this.getThreats(Colour.opposite(turn)).hasNoThreats(this.getKingCoordinate(turn))) {
            // This may have been raised by an event
            this.clearPrompt();
            throw new IllegalActionException("Cannot update game as " + turn + " player king will be in check");
        }

        // Update the board state after all changes have been made and no exception has occurred
        this.board = updatedBoard;
        this.log = updatedLog;
        this.wThreats = ctxRecord.getThreats(Colour.WHITE);
        this.bThreats = ctxRecord.getThreats(Colour.BLACK);
    }

    private List<Coordinate> getBoardChanges(Board<Coordinate> board) {
        List<Coordinate> original = this.board.occupied().stream()
                .sorted(Comparator.comparing(Coordinate::hashCode)).toList();
        List<Coordinate> updated = board.occupied().stream()
                .sorted(Comparator.comparing(Coordinate::hashCode)).toList();

        List<Coordinate> changes = new ArrayList<>();
        int ptrO = 0;
        int ptrU = 0;
        while (ptrO < original.size() && ptrU < updated.size()) {
            // Coordinate hashCode is its ordinal value, with some limitation (see Coordinate.hashCode for specifics)
            int originalVal = original.get(ptrO).hashCode();
            int updatedVal = updated.get(ptrU).hashCode();
            // Both exist, so no change in coordinate occurred
            if (originalVal == updatedVal) {
                // Did the piece change its type (promotion) or colour (capture)?
                Coordinate match = original.get(ptrO);
                if (!board.get(match).getColour().equals(this.board.get(match).getColour()) ||
                        !board.get(match).getCode().equals(this.board.get(match).getCode())) {
                    changes.add(match);
                }
                ptrO++;
                ptrU++;
            }
            // Change in original, as the updated board guaranteed does not have this coordinate
            if (originalVal < updatedVal) {
                changes.add(original.get(ptrO));
                ptrO++;
            }
            // Change in updated not in original
            if (originalVal > updatedVal) {
                changes.add(updated.get(ptrU));
                ptrU++;
            }
        }
        // Remaining changes not in the other board
        for (int i = ptrO; i < original.size(); i++) {
            changes.add(original.get(i));
        }
        // Remaining changes not in the other board, only this or the previous will be iterated through
        for (int i = ptrU; i < updated.size(); i++) {
            changes.add(updated.get(i));
        }
        return changes;
    }

    /**
     * This updates the context record's threats where the change happened. It follows these steps to update:
     * <br>
     * <ol>
     *     <li>Fetch the piece this change is happening at, then remove its threats</li>
     *     <li>Remove this piece temporarily</li>
     *     <li>Fetch all pieces that threaten this change, then remove those threats</li>
     *     <li>Add the removed piece back</li>
     *     <li>Update all threats for each piece involved with this change</li>
     * </ol>
     */
    private void refreshThreats(Colour colour, Coordinate change, GameContext.Record ctxRecord) {
        if (colour == null || change == null || ctxRecord == null) {
            throw new NullPointerException("one or more arguments are null");
        }
        Board<Coordinate> ctxBoard = ctxRecord.getBoard();
        ThreatMap ctxThreatMap = ctxRecord.getThreats(colour);
        // Fetch this piece and temporarily remove it from the board, so paths can be fetched that exclude this change
        Piece changePiece = ctxBoard.get(change);
        ctxBoard.remove(change);
        // Get all paths that lead to the change
        List<Pair<Coordinate, Path>> pathsToChange = new ArrayList<>();
        for (Coordinate threat : ctxRecord.getThreats(colour).getThreats(change)) {
            Piece piece = ctxRecord.getBoard().get(threat);
            // Get pieces affected by the change, only pieces that match the threat map being updated are involved
            if (!threat.equals(change) && piece != null && colour.equals(piece.getColour())) {
                // Get the move that threatens the change point
                MoveSet moves = piece.getMoves(ctxRecord);
                Move moveToChange = moves.getMove(change);
                if (moveToChange != null) {
                    // This piece ("threat") may have a change along this path and will need to be refreshed
                    pathsToChange.add(new Pair<>(threat, moveToChange.path()));
                }
            }
        }

        // Remove all threats from these paths
        for (Pair<Coordinate, Path> pair : pathsToChange) {
            for (Coordinate p : pair.getSecond()) {
                ctxThreatMap.removeThreats(pair.getFirst(), p);
            }
        }
        // Remove all threats from the change, as this change may no longer have a piece
        ctxThreatMap.removeThreats(change);

        boolean changeIsPresent = changePiece != null;
        // Add all threats made by each path
        for (Pair<Coordinate, Path> attackerPathPair : pathsToChange) {
            // The only change from before and after are the paths that contain the impacted point
            boolean seenChange = false;
            for (Coordinate coordinate : attackerPathPair.getSecond()) {
                if (seenChange && changeIsPresent)
                    break;
                if (coordinate.equals(change))
                    seenChange = true;
                ctxThreatMap.addThreat(attackerPathPair.getFirst(), coordinate);
            }
        }
        // Add all threats made by the change, if it exists
        if (changePiece != null && colour.equals(changePiece.getColour())) {
            ctxBoard.add(change, changePiece);
            ctxThreatMap.addThreats(change, changePiece.getMoves(ctxRecord));
        }
    }

    public GameContext.Record toRecord() {
        return new Record(new ChessBoard((ChessBoard) this.board),
                this.log,
                new ThreatMap(this.wThreats),
                new ThreatMap(this.bThreats));
    }

    /**
     * A container for GameContext information that is copied from the original context to distribute. This
     * is intended to prevent unintended changes to the GameContext, but it will not prevent modifying the Record's
     * data and then using the modified record for methods - irrespective of intent.
     */
    public static class Record {

        private final Board<Coordinate> board;
        private final ChessLog log;
        private final ThreatMap whiteThreats;
        private final ThreatMap blackThreats;

        public Record(Board<Coordinate> board, ChessLog log, ThreatMap whiteThreats,
                ThreatMap blackThreats) {
            this.board = board;
            this.log = log;
            this.whiteThreats = whiteThreats;
            this.blackThreats = blackThreats;
        }

        public Board<Coordinate> getBoard() {
            return this.board;
        }

        public ChessLog getLog() {
            return this.log;
        }

        public ThreatMap getThreats(Colour colour) {
            if (colour == null) {
                return null;
            }
            return Colour.WHITE.equals(colour) ? this.whiteThreats : this.blackThreats;
        }
    }

}
