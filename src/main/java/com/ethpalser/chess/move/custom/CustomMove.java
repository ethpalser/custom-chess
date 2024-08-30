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

    public List<Movement> toMovementList(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean onlyAttacks, boolean includeDefend) {
        if (colour == null || offset == null) {
            throw new NullPointerException("one or more arguments are null, colour: " + (colour == null)
                    + " point offset: " + (offset == null));
        }
        if (this.isSpecificQuadrant) {
            boolean isRight = !mirrorYAxis;
            boolean isUp = (Colour.WHITE.equals(colour) && !mirrorXAxis)
                    || (!Colour.WHITE.equals(colour) && mirrorXAxis);
            Path path = this.getPathInQuadrant(board, threatMap, colour, offset, isRight, isUp, onlyAttacks,
                    includeDefend);
            if (path != null) {
                return List.of(new Move(path, this.followUp));
            } else {
                return List.of();
            }
        } else {
            return this.getPathsInAllQuadrants(board, threatMap, colour, offset, onlyAttacks, includeDefend)
                    .stream()
                    .map(p -> new Move(p, this.followUp))
                    .collect(Collectors.toList());
        }
    }

    // PRIVATE

    private List<Path> getPathsInAllQuadrants(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean onlyAttacks, boolean includeDefend) {
        List<Path> list = new ArrayList<>();
        if (mirrorXAxis || Colour.WHITE.equals(colour)) {
            {
                Path pathQ1 = this.getPathInQuadrant(board, threatMap, colour, offset, true, true, onlyAttacks,
                        includeDefend);
                if (pathQ1 != null) {
                    list.add(pathQ1);
                }
            }
            if (mirrorYAxis) {
                Path pathQ4 = this.getPathInQuadrant(board, threatMap, colour, offset, false, true, onlyAttacks,
                        includeDefend);
                if (pathQ4 != null) {
                    list.add(pathQ4);
                }
            }
        }
        if (mirrorXAxis || !Colour.WHITE.equals(colour)) {
            {
                Path pathQ2 = this.getPathInQuadrant(board, threatMap, colour, offset, true, false, onlyAttacks,
                        includeDefend);
                if (pathQ2 != null) {
                    list.add(pathQ2);
                }
            }
            if (mirrorYAxis) {
                Path pathQ3 = this.getPathInQuadrant(board, threatMap, colour, offset, false, false, onlyAttacks,
                        includeDefend);
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
}
