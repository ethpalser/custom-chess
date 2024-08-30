package com.ethpalser.chess.move.custom;

import com.ethpalser.chess.board.custom.CustomBoard;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMove {

    private final Path pathBase;
    private final CustomMoveType moveType;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean isSpecificQuadrant;
    private final boolean isAttack;
    private final boolean isMove;
    private final List<Conditional<Piece>> conditions;
    private final LogEntry<Point, Piece> followUp;

    public static class Builder {
        // required
        private final Path path;
        private final CustomMoveType moveType;
        // optional
        private boolean mirrorXAxis = true;
        private boolean mirrorYAxis = true;
        private boolean isSpecificQuadrant = false;
        private boolean isAttack = true;
        private boolean isMove = true;
        private List<Conditional<Piece>> conditions = List.of();
        private LogEntry<Point, Piece> followUp = null;

        public Builder(Path path, CustomMoveType moveType) {
            this.path = path;
            this.moveType = moveType;
        }

        public Builder isMirrorXAxis(boolean bool) {
            this.mirrorXAxis = bool;
            return this;
        }

        public Builder isMirrorYAxis(boolean bool) {
            this.mirrorYAxis = bool;
            return this;
        }

        public Builder isSpecificQuadrant(boolean bool) {
            this.isSpecificQuadrant = bool;
            return this;
        }

        public Builder isAttack(boolean bool) {
            this.isAttack = bool;
            return this;
        }

        public Builder isMove(boolean bool) {
            this.isMove = bool;
            return this;
        }

        public Builder conditions(List<Conditional<Piece>> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder followUp(LogEntry<Point, Piece> followUp) {
            this.followUp = followUp;
            return this;
        }

        public CustomMove build() {
            return new CustomMove(this);
        }
    }

    public CustomMove(Builder builder) {
        this.pathBase = builder.path;
        this.moveType = builder.moveType;
        this.mirrorXAxis = builder.mirrorXAxis;
        this.mirrorYAxis = builder.mirrorYAxis;
        this.isSpecificQuadrant = builder.isSpecificQuadrant;
        this.isAttack = builder.isAttack;
        this.isMove = builder.isMove;
        this.conditions = builder.conditions;
        this.followUp = builder.followUp;
    }

    public CustomMove(Path path, CustomMoveType moveType, boolean mirrorXAxis, boolean mirrorYAxis) {
        this.pathBase = path;
        this.moveType = moveType;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.isSpecificQuadrant = false;
        this.isAttack = true;
        this.isMove = true;
        this.conditions = List.of();
        this.followUp = null;
    }

    public boolean isMove() {
        return this.isMove;
    }

    public boolean isAttack() {
        return this.isAttack;
    }

    public List<Movement> toMovementList(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean onlyAttacks, boolean includeDefend) {
        return this.getPathsInAllQuadrants(board, threatMap, colour, offset, onlyAttacks, includeDefend).stream()
                .map(p -> new Move(p, this.followUp))
                .collect(Collectors.toList());
    }

    // PRIVATE

    private List<Path> getPathsInAllQuadrants(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean onlyAttacks, boolean includeDefend) {
        List<Path> list = new ArrayList<>();
        if (mirrorXAxis || Colour.WHITE.equals(colour)) {
            {
                Path pathQ1 = this.getPathInQuadrant(board, threatMap, colour, offset, true, true, onlyAttacks, includeDefend);
                if (pathQ1 != null) {
                    list.add(pathQ1);
                }
            }
            if (mirrorYAxis) {
                Path pathQ4 = this.getPathInQuadrant(board, threatMap, colour, offset, false, true, onlyAttacks, includeDefend);
                if (pathQ4 != null) {
                    list.add(pathQ4);
                }
            }
        }
        if (mirrorXAxis || !Colour.WHITE.equals(colour)) {
            {
                Path pathQ2 = this.getPathInQuadrant(board, threatMap, colour, offset, true, false, onlyAttacks, includeDefend);
                if (pathQ2 != null) {
                    list.add(pathQ2);
                }
            }
            if (mirrorYAxis) {
                Path pathQ3 = this.getPathInQuadrant(board, threatMap, colour, offset, false, false, onlyAttacks, includeDefend);
                if (pathQ3 != null) {
                    list.add(pathQ3);
                }
            }
        }
        return list;
    }

    private Path getPathInQuadrant(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean isRight, boolean isUp, boolean onlyAttacks, boolean includeDefend) {
        if (!this.passesConditions(board) || colour == null || offset == null) {
            return null;
        }

        boolean isKing = PieceType.KING.getCode().equals(board.get(offset).getCode());
        List<Point> points = new LinkedList<>();
        for (Point p : this.pathBase) {
            Point next = this.getVectorInQuadrant(p, offset, isRight, isUp);
            boolean isSafe = threatMap != null && threatMap.hasNoThreats(next);
            // Not a valid location, out of bounds, or fails its conditions
            if (next == null || !board.isInBounds(next) || (isKing && !isSafe)) {
                break;
            }

            Piece piece = board.get(next);
            if (piece != null) {
                boolean canCapture = !board.get(next).getColour().equals(colour);
                if (this.isAttack && (canCapture || includeDefend)) {
                    points.add(next);
                }

                boolean passAnyPiece = CustomMoveType.JUMP.equals(this.moveType);
                boolean passOppKing = onlyAttacks && canCapture && PieceType.KING.getCode().equals(piece.getCode());
                if (!passAnyPiece && !passOppKing) {
                    break; // A piece was encountered and this piece cannot move beyond it, so the path ends here
                }
            } else {
                if ((this.isMove && !onlyAttacks) || (this.isAttack && onlyAttacks)) {
                    points.add(next);
                }
            }
        }
        if (points.isEmpty()) {
            return null;
        }
        return new Path(points);
    }

    private Point getVectorInQuadrant(Point vector, Point offset, boolean isRight, boolean isUp) {
        if (vector == null || offset == null) {
            return null;
        }
        int x = isRight ? offset.getX() + vector.getX() : offset.getX() - vector.getX();
        int y = isUp ? offset.getY() + vector.getY() : offset.getY() - vector.getY();
        return new Point(x, y);
    }

    /**
     * Verifies that all {@link Conditional} defined in this Movement are meeting their criteria.
     *
     * @param board {@link CustomBoard} for the Condition to verify with
     * @return true if all Condition pass, otherwise false
     */
    private boolean passesConditions(Plane<Piece> board) {
        if (board == null) {
            return false;
        }
        for (Conditional<Piece> condition : this.conditions) {
            if (!condition.isExpected(board)) {
                return false;
            }
        }
        return true;
    }

    // DEPRECIATED METHODS

    /**
     * Retrieves all possible vectors that the Piece with this colour at this location can move to.
     *
     * @param colour {@link Colour} representing the colour of the piece this movement is for
     * @param offset {@link Point} representing the position of the piece
     * @return Map of {@link Point}
     */
    @Deprecated(since = "2024-08-15")
    private Set<Point> getCoordinates(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        return this.getCoordinates(colour, offset, null, false, false);
    }

    @Deprecated(since = "2024-08-15")
    private Set<Point> getCoordinates(Colour colour, Point offset, Plane<Piece> board,
            boolean withDefend, boolean ignoreKing) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        if (this.isSpecificQuadrant) {
            return getVectorsInSpecificQuadrant(offset, colour, board, withDefend, ignoreKing);
        } else {
            return getVectorsInAllQuadrants(offset, colour, board, withDefend, ignoreKing);
        }
    }

    @Deprecated(since = "2024-08-15")
    private Set<Point> getVectorsInSpecificQuadrant(Point offset, Colour colour, Plane<Piece> board,
            boolean withDefend, boolean ignoreKing) {
        if (offset == null || colour == null) {
            throw new NullPointerException();
        }
        boolean isRight = !mirrorYAxis;
        boolean isUp = Colour.WHITE.equals(colour) && !mirrorXAxis || !Colour.WHITE.equals(colour) && mirrorXAxis;

        Set<Point> set = new HashSet<>();
        for (Point vector : this.pathBase) {
            Point v = getVectorInQuadrant(vector, offset, isRight, isUp);
            if (canMoveInQuadrant(v, colour, board, withDefend, ignoreKing))
                set.add(v);
            if (isBlockedInQuadrant(v, board, ignoreKing))
                break;
        }
        return set;
    }

    @Deprecated(since = "2024-08-15")
    private Set<Point> getVectorsInAllQuadrants(Point offset, Colour colour, Plane<Piece> board,
            boolean withDefend, boolean ignoreKing) {
        if (offset == null || colour == null) {
            throw new NullPointerException();
        }
        boolean blockTopRight = false;
        boolean blockTopLeft = false;
        boolean blockBotRight = false;
        boolean blockBotLeft = false;

        Set<Point> set = new HashSet<>();
        for (Point vector : this.pathBase) {
            if (mirrorXAxis || Colour.WHITE.equals(colour)) {
                if (!blockTopRight) {
                    Point topRight = getVectorInQuadrant(vector, offset, true, true);
                    if (canMoveInQuadrant(topRight, colour, board, withDefend, ignoreKing))
                        set.add(topRight);
                    blockTopRight = isBlockedInQuadrant(topRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockTopLeft) {
                    Point topLeft = getVectorInQuadrant(vector, offset, false, true);
                    if (canMoveInQuadrant(topLeft, colour, board, withDefend, ignoreKing))
                        set.add(topLeft);
                    blockTopLeft = isBlockedInQuadrant(topLeft, board, ignoreKing);
                }
            }
            if (mirrorXAxis || !Colour.WHITE.equals(colour)) {
                if (!blockBotRight) {
                    Point bottomRight = getVectorInQuadrant(vector, offset, true, false);
                    if (canMoveInQuadrant(bottomRight, colour, board, withDefend, ignoreKing))
                        set.add(bottomRight);
                    blockBotRight = isBlockedInQuadrant(bottomRight, board, ignoreKing);
                }
                if (this.mirrorYAxis && !blockBotLeft) {
                    Point bottomLeft = getVectorInQuadrant(vector, offset, false, false);
                    if (canMoveInQuadrant(bottomLeft, colour, board, withDefend, ignoreKing))
                        set.add(bottomLeft);
                    blockBotLeft = isBlockedInQuadrant(bottomLeft, board, ignoreKing);
                }
            }
        }
        return set;
    }

    @Deprecated(since = "2024-08-15")
    private boolean canMoveInQuadrant(Point vector, Colour colour, Plane<Piece> board,
            boolean withDefend, boolean ignoreKing) {
        if (vector == null || colour == null) {
            throw new NullPointerException();
        }
        if (!board.isInBounds(vector)) {
            return false;
        }
        if (board != null) {
            Piece p = board.get(vector);
            if (p != null) {
                return withDefend || !colour.equals(p.getColour()) || PieceType.KING.getCode().equals(p.getCode()) && ignoreKing;
            } else {
                return true;
            }
        }
        return true;
    }

    @Deprecated(since = "2024-08-15")
    private boolean isBlockedInQuadrant(Point vector, Plane<Piece> board, boolean ignoreKing) {
        if (vector == null) {
            throw new NullPointerException();
        }
        if (!board.isInBounds(vector) || board == null) {
            return false;
        }
        Piece customPiece = board.get(vector);
        return customPiece != null && !(PieceType.KING.getCode().equals(customPiece.getCode()) && ignoreKing);
    }

    /**
     * Marks all locations valid for this piece to move to, before referencing the board, from origin. This matches
     * the original path of this movement for White pieces.
     *
     * @param colour Colour of the piece, to determine which direction is forward.
     * @return 2D boolean array, true are valid locations
     */
    @Deprecated(since = "2024-08-15")
    public boolean[][] drawCoordinates(Colour colour) {
        if (colour == null) {
            throw new NullPointerException();
        }
        return this.drawCoordinates(colour, new Point(0, 0));
    }

    /**
     * Marks all locations valid for this piece to move to, before referencing the board, from origin. This matches
     * the original path of this movement for White pieces.
     *
     * @param colour Colour of the piece, to determine which direction is forward.
     * @return 2D boolean array, true are valid locations
     */
    @Deprecated(since = "2024-08-15")
    public boolean[][] drawCoordinates(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        Set<Point> coordinates = this.getCoordinates(colour, offset);
        boolean[][] boardMove = new boolean[8][8];
        for (Point c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    @Override
    public String toString() {
        return this.toString(Colour.WHITE, new Point());
    }

    public String toString(Colour colour, Point offset) {
        if (colour == null || offset == null) {
            throw new NullPointerException();
        }
        boolean[][] boardMove = this.drawCoordinates(colour, offset);
        StringBuilder sb = new StringBuilder();
        for (int y = boardMove[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < boardMove.length; x++) {
                if (x == offset.getX() && y == offset.getY()) {
                    sb.append("| P ");
                } else if (boardMove[x][y]) {
                    sb.append("| o ");
                } else {
                    sb.append("| x ");
                }
                if (x == boardMove.length - 1) {
                    sb.append("|");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
