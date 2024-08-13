package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeReference<T> implements Reference<T> {

    private final Location location;
    private final Direction direction;
    private final Colour colour;
    private final Point start;
    private final Point end;

    public RelativeReference(Location location) {
        this(location, Direction.AT, null);
    }

    public RelativeReference(Location location, Point point) {
        this(location, Direction.AT, point);
    }

    public RelativeReference(Location location, Direction direction, Point point) {
        this(location, Colour.WHITE, direction, point, null);
    }

    public RelativeReference(Location location, Colour colour, Direction direction, Point point) {
        this(location, colour, direction, point, null);
    }

    public RelativeReference(Location location, Colour colour, Direction direction, Point start, Point end) {
        this.location = location;
        this.direction = direction;
        this.colour = colour;
        this.start = start;
        this.end = end;
    }

    /**
     * Get one or more {@link CustomPiece}s that this reference is for using the current state of the board and action
     * being attempted.
     *
     * @param board  {@link CustomBoard} used for reference
     * @param action {@link Action} used for reference containing a single piece's position and destination
     * @return List of Pieces of the location is a Path, otherwise a List of one Piece
     */
    public List<Piece> getPieces(CustomBoard board, Action action) {
        if (board == null || action == null) {
            throw new NullPointerException();
        }
        if (action.getStart() == null || action.getEnd() == null) {
            throw new IllegalArgumentException("Action has null start or end vector.");
        }
        Point shiftedStart = action.getStart().shift(action.getColour(), this.direction);
        Point shiftedEnd = action.getEnd().shift(action.getColour(), this.direction);
        Point shiftedReference = this.start == null ? null : this.start.shift(action.getColour(), this.direction);

        List<Piece> list = new ArrayList<>();
        Piece toAdd = null;
        switch (this.location) {
            case LAST_MOVED -> toAdd = null; // Todo: Use ChessLog
            case START -> toAdd = board.getPiece(shiftedStart);
            case DESTINATION -> toAdd = board.getPiece(shiftedEnd);
            case VECTOR -> toAdd = board.getPiece(shiftedReference);
            case PATH_TO_DESTINATION -> list = board.getPieces(new Path(shiftedStart, shiftedEnd));
            case PATH_TO_VECTOR -> list = board.getPieces(new Path(shiftedStart, shiftedReference));
        }
        if (toAdd != null)
            list.add(toAdd);
        return list;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public List<T> getReferences(Plane<T> plane) {
        if (plane == null) {
            return List.of();
        }
        Point shiftedStart = this.start.shift(this.colour, this.direction);
        Point shiftedEnd = this.end.shift(this.colour, this.direction);

        return switch (this.location) {
            case START, VECTOR -> List.of(plane.get(shiftedStart));
            case DESTINATION -> List.of(plane.get(shiftedEnd));
            case PATH_TO_DESTINATION, PATH_TO_VECTOR -> new Path(shiftedStart, shiftedEnd).toSet().stream().map(plane::get)
                    .collect(Collectors.toList());
            default -> List.of();
        };
    }
}
