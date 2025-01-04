package com.ethpalser.chess.log;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Location;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.view.ActionView;

public class ChessLogEntry implements LogEntry<Coordinate, Piece> {

    private final Coordinate start;
    private final Coordinate end;
    private final Piece moved;
    private final Piece captured;
    private final boolean isFirstMove;
    private final LogEntry<Coordinate, Piece> followUp;
    private Piece promoted;

    public ChessLogEntry(Point start, Point end, Piece moved) {
        this(start, end, moved, null);
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = this.moved != null && !this.moved.getHasMoved();
        this.followUp = null;
        this.promoted = null;
    }

    public ChessLogEntry(Point start, Point end, Piece moved, Piece captured,
            LogEntry<Coordinate, Piece> followUpMove) {
        this.start = start;
        this.end = end;
        this.moved = moved;
        this.captured = captured;
        // For this to work this object must be created after verifying but before executing the move from start to end
        this.isFirstMove = !this.moved.getHasMoved();
        this.followUp = followUpMove;
        this.promoted = null;
    }

    public ChessLogEntry(Board<Coordinate> board, String log) {
        // Cannot convert log string to entry
        if (log == null || log.isEmpty()) {
            this.start = null;
            this.end = null;
            this.moved = null;
            this.captured = null;
            this.isFirstMove = false;
            this.followUp = null;
            this.promoted = null;
            return;
        }
        // Castling log entry
        if (log.contains("O-O-O") || log.contains("O-O")) {
            int averageX = 4; // Todo: Get these from the board
            int pieceY;
            Colour pieceColour;
            if (log.charAt(0) == 'w') {
                pieceY = 0;
                pieceColour = Colour.WHITE;
            } else {
                pieceY = 7;
                pieceColour = Colour.BLACK;
            }
            // King
            this.start = new Point(averageX, pieceY);
            // Finding the king
            Piece found = null;
            for (Piece piece : board) {
                if (PieceType.KING.getCode().equals(piece.getCode()) && pieceColour.equals(piece.getColour())) {
                    found = piece;
                    break;
                }
            }
            this.moved = found;
            this.isFirstMove = this.moved != null && !this.moved.getHasMoved();

            this.captured = null;
            if (log.contains("O-O-O")) {
                this.end = new Point(averageX - 2, pieceY);
                // Rook
                this.followUp = new ChessLogEntry(
                        new Point(0, pieceY),
                        new Point(averageX - 1, pieceY),
                        new Rook(pieceColour, new Point(averageX, pieceY), true)
                );
            } else {
                this.end = new Point(averageX + 2, pieceY);
                // Rook
                this.followUp = new ChessLogEntry(
                        new Point(0, pieceY),
                        new Point(averageX + 1, pieceY),
                        new Rook(pieceColour, new Point(averageX, pieceY), true)
                );
            }
            return;
        }

        String[] split = log.split(" "); // When a log entry has a ' ' it was not a capture
        if (split.length <= 1) {
            split = log.split("X"); // Assumes the piece is not using an X in its code, which shouldn't happen
        }

        // extract start point
        if (split[0].substring(split[0].length() - 3).matches("\\w\\d\\d")) {
            // This point is on a custom board with a y value greater than 10
            this.start = new Point(split[0].substring(split[0].length() - 3));
        } else {
            this.start = new Point(split[0].substring(split[0].length() - 2));
        }
        this.end = new Point(split[1]);

        this.moved = board.get(this.start);
        this.captured = board.get(this.end);
        this.isFirstMove = !this.moved.getHasMoved();

        if (log.contains("e.p.")) {
            // removed pawn position (note: 0 and 1 are for regular move, 2 is e.p., so 3 is the removed pawn)
            Point pawnPoint = new Point(split[3]);
            this.followUp = new ChessLogEntry(
                    pawnPoint,
                    null,
                    new Pawn(Colour.opposite(this.moved.getColour()), pawnPoint, true)
            );
        } else if (log.contains("c.m.")) {
            // todo
            this.followUp = null;
        } else {
            this.followUp = null;
        }
    }

    @Override
    public Coordinate getStart() {
        return this.start;
    }

    @Override
    public Coordinate getEnd() {
        return this.end;
    }

    @Override
    public Piece getStartObject() {
        return this.moved;
    }

    @Override
    public Piece getEndObject() {
        return this.captured;
    }

    @Override
    public Piece getPromotion() {
        return this.promoted;
    }

    @Override
    public void setPromotion(Piece promoted) {
        this.promoted = promoted;
    }

    @Override
    public boolean isFirstOccurrence() {
        return this.isFirstMove;
    }

    public LogEntry<Coordinate, Piece> getSubLogEntry() {
        return this.followUp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String followUpString;
        // Determining the kind of followup
        if (this.followUp != null && this.followUp.getStartObject() != null) {
            Colour colour = this.followUp.getStartObject().getColour();
            if ("R".equals(this.followUp.getStartObject().getCode())) {
                // Not queen side rook
                if (this.followUp.getStart().getValue(Space.AXIS.Y) != 0) {
                    return colour.toCode() + "O-O";
                } else {
                    return colour.toCode() + "O-O-O";
                }
            } else if ("P".equals(this.followUp.getStartObject().getCode())) {
                followUpString = " e.p. " + this.followUp.getStart();
            } else {
                followUpString = " c.m.";
                String affectedPiece = this.followUp.getStartObject().getCode() + this.followUp.getStart();
                if (this.followUp.getEnd() == null) {
                    followUpString += " X" + affectedPiece;
                } else {
                    followUpString += " " + affectedPiece + " " + this.followUp.getEnd();
                }
            }
        } else {
            followUpString = "";
        }


        sb.append(this.moved.getCode()).append(this.start);
        if (this.captured != null) {
            sb.append("X").append(this.captured.getCode());
        } else {
            sb.append(" ");
        }
        sb.append(this.end).append(followUpString);
        return sb.toString();
    }

    @Override
    public ActionView toView() {
        return new ActionView(new Reference(Direction.AT, Location.POINT, this.start),
                new Reference(Direction.AT, Location.POINT, this.end));
    }
}
