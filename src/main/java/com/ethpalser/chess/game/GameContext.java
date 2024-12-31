package com.ethpalser.chess.game;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.game.state.GamePrompt;
import com.ethpalser.chess.log.ChessLog;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.StandardPieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameContext {

    private Board<Coordinate> board;
    private Log<Coordinate, Piece> log;
    private ThreatMap wThreats;
    private ThreatMap bThreats;
    private Coordinate wKing;
    private Coordinate bKing;

    private GamePrompt prompt; // Nullable

    public GameContext() {
        Space space = new Plane(8, 8);
        this.board = new ChessBoard(space, new StandardPieceFactory());
        this.log = new ChessLog();
        this.wThreats = new ThreatMap(Colour.WHITE, board, log, space);
        this.bThreats = new ThreatMap(Colour.BLACK, board, log, space);
        this.wKing = new Point("e1");
        this.bKing = new Point("e8");
        this.prompt = null;
    }

    public GameContext(GameOptions config) {
        this.log = new ChessLog();
        Space space = new Plane(config.width(), config.length(), config.unavailable());
        this.board = new ChessBoard(space, new CustomPieceFactory(config.pieceSpecs(), this.log, space));
        this.wThreats = new ThreatMap(Colour.WHITE, this.board, this.log, space);
        this.bThreats = new ThreatMap(Colour.BLACK, this.board, this.log, space);
        for (Coordinate c : this.board.occupied()) {
            Piece p = this.board.get(c);
            if (PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.wKing = c;
                } else {
                    this.bKing = c;
                }
            }
        }
    }

    public GameContext(GameOptions config, Board<Coordinate> board, Log<Coordinate, Piece> log) {
        // This will eventually need to check each piece id maps correctly to piece starts for "has moved" checks
        this.log = log;
        this.board = board;

        Space space = board.space();
        this.wThreats = new ThreatMap(Colour.WHITE, this.board, this.log, space);
        this.bThreats = new ThreatMap(Colour.BLACK, this.board, this.log, space);
        for (Coordinate c : this.board.occupied()) {
            Piece p = this.board.get(c);
            if (p != null && PieceType.KING.getCode().equals(p.getCode())) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.wKing = c;
                } else {
                    this.bKing = c;
                }
            }
        }
    }

    public Board<Coordinate> getBoard() {
        return new ChessBoard((ChessBoard) this.board);
    }

    public Log<Coordinate, Piece> getLog() {
        return this.log;
    }

    public ThreatMap getThreats(Colour colour) {
        return Colour.WHITE.equals(colour) ? this.wThreats : this.bThreats;
    }

    public Coordinate getKingCoordinate(Colour colour) {
        return Colour.WHITE.equals(colour) ? this.wKing : this.bKing;
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

    public void update(Colour turn, Board<Coordinate> updatedBoard, Log<Coordinate, Piece> updatedLog) {
        this.update(turn, updatedBoard, updatedLog, false);
    }

    public void undo(Colour turn, Board<Coordinate> updatedBoard, Log<Coordinate, Piece> updatedLog) {
        this.update(turn, updatedBoard, updatedLog, true);
    }

    private void update(Colour turn, Board<Coordinate> updatedBoard, Log<Coordinate, Piece> updatedLog,
            boolean isUndo) {
        if (turn == null || updatedBoard == null) {
            throw new IllegalArgumentException("Cannot update game as one or more arguments are null.");
        }
        // Shallow copying context data for reference and to lazily discard changes if any exception occurs
        ThreatMap wThreatsRef = new ThreatMap(this.wThreats);
        ThreatMap bThreatsRef = new ThreatMap(this.bThreats);

        // Update threats wherever there was a change
        for (Coordinate coordinate : this.getBoardChanges(updatedBoard)) {
            Piece p = updatedBoard.get(coordinate);
            if (p != null && PieceType.KING.equals(PieceType.fromCode(p.getCode()))) {
                if (Colour.WHITE.equals(p.getColour())) {
                    this.wKing = coordinate;
                } else {
                    this.bKing = coordinate;
                }
            }
            wThreatsRef.refreshThreats(updatedBoard, updatedLog, (Point) coordinate);
            bThreatsRef.refreshThreats(updatedBoard, updatedLog, (Point) coordinate);
        }
        // After all changes, did the turn player put itself into check?
        if (!isUndo && !this.getThreats(Colour.opposite(turn)).hasNoThreats((Point) this.getKingCoordinate(turn))) {
            // This may have been raised by an event
            this.clearPrompt();
            throw new IllegalActionException("Cannot update game as " + turn + " player king will be in check");
        }

        // Update the board state after all changes have been made and no exception has occurred
        this.board = updatedBoard;
        this.log = updatedLog;
        this.wThreats = wThreatsRef;
        this.bThreats = bThreatsRef;
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

    public GameContext.Record toRecord() {
        return new Record(new ChessBoard((ChessBoard) this.board),
                this.getLog(),
                this.getThreats(Colour.WHITE),
                this.getThreats(Colour.BLACK));
    }

    /**
     * A container for GameContext information that is copied from the original context to distribute. This
     * is intended to prevent unintended changes to the GameContext, but it will not prevent modifying the Record's
     * data and then using the modified record for methods - irrespective of intent.
     */
    public class Record {

        private final Board<Coordinate> board;
        private final Log<Coordinate, Piece> log;
        private final ThreatMap whiteThreats;
        private final ThreatMap blackThreats;

        private Record(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap whiteThreats,
                ThreatMap blackThreats) {
            this.board = board;
            this.log = log;
            this.whiteThreats = whiteThreats;
            this.blackThreats = blackThreats;
        }

        public Board<Coordinate> getBoard() {
            return this.board;
        }

        public Log<Coordinate, Piece> getLog() {
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
