package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.board.ChessBoard;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.game.ChessLog;
import com.ethpalser.chess.piece.ChessPiece;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.MoveSet;
import com.ethpalser.chess.piece.custom.movement.Movement;
import com.ethpalser.chess.piece.custom.movement.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomPiece implements ChessPiece {

    private final PieceType type;
    private final String code;
    private final Colour colour;
    private final List<Movement> movements;
    private Point position;
    private boolean hasMoved;

    public CustomPiece(PieceType pieceType, Colour colour, Point vector) {
        this(pieceType, colour, vector, (Movement) null);
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, Movement... movements) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.movements = Arrays.asList(movements);
        this.hasMoved = false;
        this.code = pieceType.getCode();
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, boolean hasMoved, Movement... movements) {
        this(pieceType, colour, vector, movements);
        this.hasMoved = hasMoved;
    }

    public PieceType getType() {
        return this.type;
    }

    @Override
    public String getCode() {
        if (this.type != PieceType.CUSTOM) {
            return type.getCode();
        } else {
            return code;
        }
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public MoveSet getMoves(ChessBoard board, ChessLog log) {
        Set<Point> set = new HashSet<>();
        // todo: update to use Movements, which requires refactoring all custom logic to use ChessPiece and ChessBoard
        return new MoveSet(set);
    }

    /**
     * Updates this piece's position to the new {@link Point} destination. If this destination is not the same
     * as its current position then it is considered to have moved.
     *
     * @param destination representing the new location of this piece.
     */
    @Override
    public void move(Point destination) {
        if (destination == null) {
            throw new IllegalArgumentException("illegal argument, destination is null");
        }
        if (destination.equals(this.position)) {
            return;
        }
        this.position = destination;
        this.hasMoved = true;
    }

    /**
     * Retrieves the value of hasMoved.
     *
     * @return true or false
     */
    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    // Temporary
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    public Point getPosition() {
        return this.position;
    }

    /**
     * Retrieves the first movement among all of its possible movements that are able to reach the destination, can
     * be traversed and has all its conditions met.
     *
     * @param board       {@link CustomBoard} used for reference
     * @param destination {@link Point} the piece is requested to move to
     * @return Movement if any are valid, otherwise null
     */
    public Movement getMovement(CustomBoard board, Point destination) {
        if (board == null || destination == null) {
            throw new NullPointerException();
        }
        for (Movement move : this.movements) {
            Path path = move.getPath(this.colour, this.position, destination, board);
            if (path != null && path.isTraversable(board)
                    && move.passesConditions(board, new Action(this.colour, this.position, destination))) {
                return move;
            }
        }
        return null;
    }

    public Set<Point> getMovementSet(Point location, CustomBoard board) {
        if (location == null) {
            throw new NullPointerException();
        }
        return this.getMovementSet(location, board, true, true, false, false);
    }

    public Set<Point> getMovementSet(Point location, CustomBoard board, boolean includeMove,
            boolean includeAttack, boolean includeDefend, boolean ignoreKing) {
        if (location == null) {
            throw new NullPointerException();
        }
        Set<Point> set = new HashSet<>();
        for (Movement move : this.movements) {
            if (move != null && (includeMove && move.isMove() || includeAttack && move.isAttack())) {
                Set<Point> vectorSet = move.getCoordinates(this.colour, location, board, includeDefend, ignoreKing);
                if (board != null) {
                    for (Point v : vectorSet) {
                        if (!includeMove || move.passesConditions(board, new Action(this.colour, this.getPosition(),
                                v))) {
                            set.add(v);
                        }
                    }
                } else {
                    set.addAll(vectorSet);
                }
            }
        }
        return set;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
