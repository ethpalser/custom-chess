package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CustomPiece implements Piece {

    private final PieceType type;
    private final String code;
    private final Colour colour;
    private final List<CustomMove> customMoves;
    private Point position;
    private boolean hasMoved;

    public CustomPiece(PieceType pieceType, Colour colour, Point vector) {
        this(pieceType, colour, vector, (CustomMove) null);
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, CustomMove... customMoves) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.customMoves = Arrays.asList(customMoves);
        this.hasMoved = false;
        this.code = pieceType.getCode();
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, boolean hasMoved, CustomMove... customMoves) {
        this(pieceType, colour, vector, customMoves);
        this.hasMoved = hasMoved;
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
    public Point getPoint() {
        return this.position;
    }

    @Override
    public MoveSet getMoves(Board board) {
        Set<Point> set = new HashSet<>();
        // todo: update to use Movements, which requires refactoring all custom logic to use ChessPiece and ChessBoard
        return new MoveSet(set);
    }

    public void addMove(CustomMove move) {
        this.customMoves.add(move);
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

    /**
     * Retrieves the first movement among all of its possible movements that are able to reach the destination, can
     * be traversed and has all its conditions met.
     *
     * @param board       {@link CustomBoard} used for reference
     * @param destination {@link Point} the piece is requested to move to
     * @return Movement if any are valid, otherwise null
     */
    @Deprecated
    public CustomMove getMovement(CustomBoard board, Point destination) {
        if (board == null || destination == null) {
            throw new NullPointerException();
        }
        for (CustomMove move : this.customMoves) {
            Path path = move.getPath(this.colour, this.position, destination, board);
            if (path != null && this.isTraversable(path, board)
                    && move.passesConditions(board, new Action(this.colour, this.position, destination))) {
                return move;
            }
        }
        return null;
    }

    @Deprecated
    public Set<Point> getMovementSet(Point location, CustomBoard board) {
        if (location == null) {
            throw new NullPointerException();
        }
        return this.getMovementSet(location, board, true, true, false, false);
    }

    @Deprecated
    public Set<Point> getMovementSet(Point location, CustomBoard board, boolean includeMove,
            boolean includeAttack, boolean includeDefend, boolean ignoreKing) {
        if (location == null) {
            throw new NullPointerException();
        }
        Set<Point> set = new HashSet<>();
        for (CustomMove move : this.customMoves) {
            if (move != null && (includeMove && move.isMove() || includeAttack && move.isAttack())) {
                Set<Point> vectorSet = move.getCoordinates(this.colour, location, board, includeDefend, ignoreKing);
                if (board != null) {
                    for (Point v : vectorSet) {
                        if (!includeMove || move.passesConditions(board, new Action(this.colour, this.getPoint(),
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

    /**
     * Iterates through the path to determine if there is a piece in the path between the start and end.
     *
     * @param board {@link CustomBoard} referred to for checking pieces
     * @return true if no piece is in the middle of the path, false otherwise
     */
    private boolean isTraversable(Path path, CustomBoard board) {
        if (board == null) {
            throw new NullPointerException();
        }
        Iterator<Point> iterator = path.iterator();
        while (iterator.hasNext()) {
            Point vector = iterator.next();
            if (board.getPiece(vector) != null && iterator.hasNext()) {
                // Piece is in the middle of the path
                return false;
            }
        }
        return true;
    }

}
