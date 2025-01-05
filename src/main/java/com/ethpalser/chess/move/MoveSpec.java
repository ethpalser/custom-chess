package com.ethpalser.chess.move;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.view.MoveView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MoveSpec {

    private final Path pathBase;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean isSpecificQuadrant;
    private final boolean isAttack;
    private final boolean isMove;
    private final List<Conditional> conditions;
    private final Reference followUpReference;
    private final Path followUpPath;

    public MoveSpec(Path path, boolean mirrorXAxis, boolean mirrorYAxis) {
        this(path, mirrorXAxis, mirrorYAxis, false);
    }

    public MoveSpec(Path path, boolean mirrorXAxis, boolean mirrorYAxis, boolean isSpecificQuadrant) {
        this.pathBase = path;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.isSpecificQuadrant = isSpecificQuadrant;
        this.isAttack = true;
        this.isMove = true;
        this.conditions = List.of();
        this.followUpReference = null;
        this.followUpPath = null;
    }

    private MoveSpec(Builder builder) {
        this.pathBase = builder.path;
        this.mirrorXAxis = builder.mirrorXAxis;
        this.mirrorYAxis = builder.mirrorYAxis;
        this.isSpecificQuadrant = builder.isSpecificQuadrant;
        this.isAttack = builder.isAttack;
        this.isMove = builder.isMove;
        this.conditions = builder.conditions;
        this.followUpReference = builder.followUpReference;
        this.followUpPath = builder.followUpPath;
    }

    public List<MoveReport> toMoveList(GameContext.Record context, Coordinate offset, Colour colour) {
        if (colour == null || offset == null) {
            throw new NullPointerException("one or more arguments are null, colour: " + (colour == null)
                    + " point offset: " + (offset == null));
        }
        if (this.pathBase == null || this.pathBase.length() == 0) {
            System.err.println("path base is not defined");
            return List.of();
        }
        if (this.isSpecificQuadrant) {
            boolean isWhite = Colour.WHITE.equals(colour);
            boolean isRight = !this.mirrorYAxis;
            boolean isUp = (isWhite && !this.mirrorXAxis) || (!isWhite && this.mirrorXAxis);

            MoveReport report = this.getPathInQuadrant(context, offset, colour, isRight, isUp);
            if (report != null) {
                return List.of(report);
            }
            return List.of();
        } else {
            return this.getPathsInAllQuadrants(context, offset, colour);
        }
    }

    public MoveView toView() {
        return new MoveView(this.pathBase, this.mirrorXAxis, this.mirrorYAxis, this.isSpecificQuadrant,
                this.isMove, this.isAttack, this.conditions, new Move.FollowUp(this.followUpReference, null));
    }

    // region PRIVATE

    private List<MoveReport> getPathsInAllQuadrants(GameContext.Record context, Coordinate offset, Colour colour) {
        List<MoveReport> list = new ArrayList<>();
        // Black pieces move down, so the spec must mirror the x-axis for the piece to move up
        if (this.mirrorXAxis || Colour.WHITE.equals(colour)) {
            {
                MoveReport pathQ1 = this.getPathInQuadrant(context, offset, colour, true, true);
                if (pathQ1 != null) {
                    list.add(pathQ1);
                }
            }
            if (this.mirrorYAxis) {
                MoveReport pathQ4 = this.getPathInQuadrant(context, offset, colour, false, true);
                if (pathQ4 != null) {
                    list.add(pathQ4);
                }
            }
        }
        // White pieces move up, so the spec must mirror the x-axis for the piece to move down
        if (this.mirrorXAxis || !Colour.WHITE.equals(colour)) {
            {
                MoveReport pathQ2 = this.getPathInQuadrant(context, offset, colour, true, false);
                if (pathQ2 != null) {
                    list.add(pathQ2);
                }
            }
            if (this.mirrorYAxis) {
                MoveReport pathQ3 = this.getPathInQuadrant(context, offset, colour, false, false);
                if (pathQ3 != null) {
                    list.add(pathQ3);
                }
            }
        }
        return list;
    }

    private MoveReport getPathInQuadrant(GameContext.Record context, Coordinate offset, Colour colour,
            boolean isRight, boolean isUp) {
        if (!this.passesConditions(context, offset) || colour == null || offset == null) {
            return null;
        }

        Board<Coordinate> board = context.getBoard();
        ThreatMap threatMap = context.getThreats(Colour.opposite(colour));
        List<Coordinate> moveCoords = new LinkedList<>();
        // Add coordinates that can be moved to, and additional information on the result to help with other move checks
        for (Coordinate p : this.pathBase) {
            Coordinate next = this.getVectorInQuadrant(p, offset, isRight, isUp);
            // Out of bounds
            if (board.rejects(next)) {
                return new MoveReport(this.createMove(moveCoords), MoveReport.Status.OUT_OF_BOUNDS,
                        next, this.isAttack, null);
            }
            // Kings cannot move to threatened spaces
            boolean isSafe = threatMap != null && threatMap.hasNoThreats(next);
            if (Pieces.isKing(board.get(offset)) && !isSafe) {
                return new MoveReport(this.createMove(moveCoords), MoveReport.Status.FAILED_CONDITIONS,
                        next, this.isAttack, null);
            }

            Piece nPiece = board.get(next);
            if (nPiece == null) {
                if (!this.isMove) {
                    return new MoveReport(this.createMove(moveCoords), MoveReport.Status.END_OF_PATH,
                            next, this.isAttack, null);
                }
                moveCoords.add(next);
            } else {
                if (!this.isAttack) {
                    return new MoveReport(this.createMove(moveCoords), MoveReport.Status.BLOCKED_BY_OBSTACLE,
                            next, false, null);
                }
                if (Pieces.isAllied(colour, nPiece)) {
                    return new MoveReport(this.createMove(moveCoords), MoveReport.Status.BLOCKED_BY_ALLY,
                            next, true, null);
                }
                // This is the final point of the path, which the piece can "defend" or capture
                moveCoords.add(next);
                Coordinate king = Pieces.isKing(nPiece) ? next : null;
                return new MoveReport(this.createMove(moveCoords), MoveReport.Status.BLOCKED_BY_OPPONENT,
                        next, true, king);
            }
        }
        return new MoveReport(this.createMove(moveCoords), MoveReport.Status.END_OF_PATH,
                null, this.isAttack, null);
    }

    private Coordinate getVectorInQuadrant(Coordinate vector, Coordinate offset, boolean isRight, boolean isUp) {
        if (vector == null || offset == null) {
            return null;
        }
        int x = isRight ? offset.getValue(Space.AXIS.X) + vector.getValue(Space.AXIS.X) :
                offset.getValue(Space.AXIS.X) - vector.getValue(Space.AXIS.X);
        int y = isUp ? offset.getValue(Space.AXIS.Y) + vector.getValue(Space.AXIS.Y) :
                offset.getValue(Space.AXIS.Y) - vector.getValue(Space.AXIS.Y);
        return new Point(x, y);
    }

    private Move createMove(List<Coordinate> coordinates) {
        Move.FollowUp followUp;
        if (this.followUpReference == null) {
            followUp = null;
        } else {
            // A null path may mean that the reference should be removed. The reference should always exist
            followUp = new Move.FollowUp(this.followUpReference, this.followUpPath);
        }
        return new Move(new Path(coordinates), followUp);
    }

    /**
     * Verifies that all {@link Conditional} defined in this Movement are meeting their criteria.
     *
     * @param context    Record of GameContext information
     * @param coordinate Coordinate of the piece this movement is for
     * @return true if all Condition pass, otherwise false
     */
    private boolean passesConditions(GameContext.Record context, Coordinate coordinate) {
        if (context == null) {
            return false;
        }
        if (this.conditions == null) {
            return true;
        }
        for (Conditional condition : this.conditions) {
            if (!condition.isExpected(context, coordinate)) {
                return false;
            }
        }
        return true;
    }

    // endregion
    // region Builder

    public static class Builder {
        // required
        private final Path path;
        // optional
        private boolean mirrorXAxis = true;
        private boolean mirrorYAxis = true;
        private boolean isSpecificQuadrant = false;
        private boolean isAttack = true;
        private boolean isMove = true;
        private List<Conditional> conditions = List.of();
        private Reference followUpReference = null;
        private Path followUpPath = null;

        public Builder(Path path) {
            this.path = Objects.requireNonNullElse(path, new Path(List.of()));
        }

        public Builder isMirrorXAxis(Boolean bool) {
            this.mirrorXAxis = Objects.requireNonNullElse(bool, false);
            return this;
        }

        public Builder isMirrorYAxis(Boolean bool) {
            this.mirrorYAxis = Objects.requireNonNullElse(bool, false);
            return this;
        }

        public Builder isSpecificQuadrant(Boolean bool) {
            this.isSpecificQuadrant = Objects.requireNonNullElse(bool, false);
            return this;
        }

        public Builder isAttack(Boolean bool) {
            this.isAttack = Objects.requireNonNullElse(bool, true);
            return this;
        }

        public Builder isMove(Boolean bool) {
            this.isMove = Objects.requireNonNullElse(bool, true);
            return this;
        }

        public Builder conditions(List<Conditional> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder followUp(Reference reference, Path options) {
            this.followUpReference = reference;
            this.followUpPath = options;
            return this;
        }

        public MoveSpec build() {
            return new MoveSpec(this);
        }
    }
    // endregion
}
