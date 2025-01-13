package com.ethpalser.chess.move.config;

import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.condition.Conditional;
import com.ethpalser.chess.game.context.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MoveSpec {

    private final PathOptions pathBase;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean isSpecificQuadrant;
    private final boolean isAttack;
    private final boolean isMove;
    private final List<ConditionalOptions> conditions;
    private final Reference followUpReference;
    private final PathOptions followUpPath;

    public MoveSpec(PathOptions path, boolean mirrorXAxis, boolean mirrorYAxis) {
        this(path, mirrorXAxis, mirrorYAxis, false);
    }

    public MoveSpec(PathOptions path, boolean mirrorXAxis, boolean mirrorYAxis, boolean isSpecificQuadrant) {
        if (path == null) {
            throw new IllegalArgumentException("path is not defined");
        }
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
        if (builder.path == null) {
            throw new IllegalArgumentException("path is not defined");
        }
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
        if (context == null || offset == null || colour == null) {
            throw new IllegalArgumentException("one or more arguments are null");
        }
        if (!this.passesConditions(context, offset)) {
            return List.of();
        }

        PathFactory factory = new PathFactory(context, offset);
        Path base = factory.create(this.pathBase);
        Path followUp = factory.create(this.followUpPath);
        if (!this.isSpecificQuadrant) {
            return this.getPathsInAllQuadrants(context, base, offset, colour, followUp);
        }
        boolean isWhite = Colour.WHITE.equals(colour);
        boolean isRight = !this.mirrorYAxis;
        boolean isUp = (isWhite && !this.mirrorXAxis) || (!isWhite && this.mirrorXAxis);
        MoveReport report = this.getPathInQuadrant(context, base, offset, colour, isRight, isUp, followUp);
        return List.of(report);
    }

    // region PRIVATE

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
        ConditionalFactory factory = ConditionalFactory.getInstance();
        for (ConditionalOptions options : this.conditions) {
            if (!factory.create(options).isExpected(context, coordinate)) {
                return false;
            }
        }
        return true;
    }

    private List<MoveReport> getPathsInAllQuadrants(GameContext.Record context, Path base, Coordinate offset,
            Colour colour, Path followUp) {
        List<MoveReport> list = new ArrayList<>();
        // Black pieces move down, so this specification must mirror the x-axis to move up
        if (this.mirrorXAxis || Colour.WHITE.equals(colour)) {
            // Q1 - Top Right
            MoveReport pathQ1 = this.getPathInQuadrant(context, base, offset, colour, true, true, followUp);
            list.add(pathQ1);
            // Q4 - Top Left
            if (this.mirrorYAxis) {
                MoveReport pathQ4 = this.getPathInQuadrant(context, base, offset, colour, false, true, followUp);
                list.add(pathQ4);
            }
        }
        // White pieces move up, so this specification must mirror the x-axis to move down
        if (this.mirrorXAxis || !Colour.WHITE.equals(colour)) {
            // Q2 - Bottom Right
            MoveReport pathQ2 = this.getPathInQuadrant(context, base, offset, colour, true, false, followUp);
            list.add(pathQ2);
            // Q3 - Bottom Left
            if (this.mirrorYAxis) {
                MoveReport pathQ3 = this.getPathInQuadrant(context, base, offset, colour, false, false, followUp);
                list.add(pathQ3);
            }
        }
        return list;
    }

    private MoveReport getPathInQuadrant(GameContext.Record context, Path base, Coordinate offset, Colour colour,
            boolean isRight, boolean isUp, Path followUp) {
        // Dependencies from Context
        Board<Coordinate> board = context.getBoard();
        ThreatMap threatMap = context.getThreats(Colour.opposite(colour));

        List<Coordinate> moves = new LinkedList<>();
        List<Coordinate> threats = new LinkedList<>();
        boolean kingEncountered = false;
        // Add coordinates that can be moved to, and additional information on the result to help with other move checks
        for (Coordinate p : base) {
            Coordinate next = this.getCoordinateInQuadrant(p, offset, isRight, isUp);
            MoveReport.Status status = this.getStatusOfCoordinate(board, threatMap, offset, colour, next);
            if (status == null || MoveReport.Status.BLOCKED_BY_OPPONENT.equals(status)) {
                if (kingEncountered) {
                    threats.add(next);
                } else {
                    moves.add(next);
                }
                // The king is ignored when gathering attacks, but is considered for threats
                if (!kingEncountered && Pieces.isKing(board.get(next))) {
                    kingEncountered = true;
                }
            }
            if (status != null) {
                return new MoveReport(this.createMove(moves, followUp), status, next, this.isAttack, threats);
            }
        }
        return new MoveReport(this.createMove(moves, followUp), MoveReport.Status.END_OF_PATH, null,
                this.isAttack, threats);
    }

    private Coordinate getCoordinateInQuadrant(Coordinate vector, Coordinate offset, boolean isRight, boolean isUp) {
        if (vector == null || offset == null) {
            return null;
        }
        int x = isRight ? offset.getValue(Space.AXIS.X) + vector.getValue(Space.AXIS.X) :
                offset.getValue(Space.AXIS.X) - vector.getValue(Space.AXIS.X);
        int y = isUp ? offset.getValue(Space.AXIS.Y) + vector.getValue(Space.AXIS.Y) :
                offset.getValue(Space.AXIS.Y) - vector.getValue(Space.AXIS.Y);
        return new Point(x, y);
    }

    private MoveReport.Status getStatusOfCoordinate(Board<Coordinate> board, ThreatMap threatMap, Coordinate offset,
            Colour colour, Coordinate next) {
        // Out of bounds
        if (board.rejects(next)) {
            return MoveReport.Status.OUT_OF_BOUNDS;
        }
        // Kings cannot move to threatened spaces
        boolean isSafe = threatMap != null && threatMap.hasNoThreats(next);
        if (Pieces.isKing(board.get(offset)) && !isSafe) {
            return MoveReport.Status.BLOCKED_BY_THREAT;
        }

        Piece nPiece = board.get(next);
        if (nPiece == null) {
            if (!this.isMove) {
                return MoveReport.Status.END_OF_PATH;
            }
        } else {
            if (!this.isAttack) {
                return MoveReport.Status.BLOCKED_BY_OBSTACLE;
            }
            if (Pieces.isAllied(colour, nPiece)) {
                return MoveReport.Status.BLOCKED_BY_ALLY;
            }
            if (!Pieces.isKing(nPiece)) {
                // This is the final point of the path, which the piece can "defend" or capture
                return MoveReport.Status.BLOCKED_BY_OPPONENT;
            }
        }
        return null;
    }

    private Move createMove(List<Coordinate> coordinates, Path followUpPath) {
        Move.FollowUp followUp;
        if (this.followUpReference == null) {
            followUp = null;
        } else {
            // A null path may mean that the reference should be removed. The reference should always exist
            followUp = new Move.FollowUp(this.followUpReference, followUpPath);
        }
        return new Move(new Path(coordinates), followUp);
    }

// endregion
// region Builder

    public static class Builder {
        // required
        private final PathOptions path;
        // optional
        private boolean mirrorXAxis = true;
        private boolean mirrorYAxis = true;
        private boolean isSpecificQuadrant = false;
        private boolean isAttack = true;
        private boolean isMove = true;
        private List<ConditionalOptions> conditions = List.of();
        private Reference followUpReference = null;
        private PathOptions followUpPath = null;

        public Builder(PathOptions path) {
            this.path = Objects.requireNonNullElse(path, new PathOptions(PathOptions.Type.CUSTOM, null, null));
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

        public Builder conditions(List<ConditionalOptions> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder followUp(Reference reference, PathOptions options) {
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
