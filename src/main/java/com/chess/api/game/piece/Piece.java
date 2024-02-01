package com.chess.api.game.piece;

import com.chess.api.game.Board;
import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;
import com.chess.api.game.movement.Action;
import com.chess.api.game.movement.Movement;
import com.chess.api.game.movement.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Piece {

    private final PieceType type;
    private final Colour colour;
    private final List<Movement> movements;
    private Vector2D position;
    private int lastMoveDistance;

    @Getter(AccessLevel.NONE)
    private boolean hasMoved;

    public Piece() {
        this(PieceType.PAWN, Colour.WHITE, new Vector2D());
    }

    public Piece(PieceType pieceType, Colour colour, Vector2D vector) {
        this(pieceType, colour, vector, (Movement) null);
    }

    public Piece(PieceType pieceType, Colour colour, Vector2D vector, Movement... movements) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.movements = Arrays.asList(movements);
        this.hasMoved = false;
        this.lastMoveDistance = 0;
    }

    /**
     * Updates this piece's position to the new {@link Vector2D} destination. If this destination is not the same
     * as its current position then it is considered to have moved.
     *
     * @param destination representing the new location of this piece.
     */
    public void setPosition(@NonNull Vector2D destination) {
        if (destination.equals(this.position)) {
            return;
        }

        this.lastMoveDistance = Math.max(Math.abs(destination.getX() - position.getX()),
                Math.abs(destination.getY() - position.getY()));
        this.position = destination;
        this.hasMoved = true;
    }

    /**
     * Retrieves the value of hasMoved.
     *
     * @return true or false
     */
    public boolean getHasMoved() {
        return hasMoved;
    }

    /**
     * Retrieves the first movement among all of its possible movements that are able to reach the destination, can
     * be traversed and has all its conditions met.
     *
     * @param board {@link Board} used for reference
     * @param destination {@link Vector2D} the piece is requested to move to
     * @return Movement if any are valid, otherwise null
     */
    public Movement getMovement(@NonNull Board board, @NonNull Vector2D destination) {
        for (Movement move : this.movements) {
            Path path = move.getPath(this.colour, this.position, destination);
            if (path != null && path.isTraversable(board)
                    && move.passesConditions(board, new Action(this.colour, this.position, destination))) {
                return move;
            }
        }
        return null;
    }

    public Set<Vector2D> getMovementSet(@NonNull Vector2D location, Board board) {
        return this.getMovementSet(location, board, false);
    }

    public Set<Vector2D> getMovementSet(@NonNull Vector2D location, Board board, boolean withDefend) {
        Set<Vector2D> set = new HashSet<>();
        for (Movement move : this.movements) {
            set.addAll(move.getCoordinates(this.colour, location, board, withDefend));
        }
        return set;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
