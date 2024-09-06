package com.ethpalser.chess.move.custom;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.log.custom.ReferenceLogEntry;
import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.ConditionalFactory;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.Pieces;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.reference.ReferenceFactory;
import com.ethpalser.chess.view.ConditionalView;
import com.ethpalser.chess.view.MoveView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            this.path = Objects.requireNonNullElse(path, new Path(List.of()));
            this.moveType = Objects.requireNonNullElse(moveType, CustomMoveType.ADVANCE);
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

    CustomMove(Builder builder) {
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

    public CustomMove(Plane<Piece> board, Log<Point, Piece> log, MoveView view) {
        if (view == null) {
            this.pathBase = null;
            this.moveType = null;
            this.mirrorXAxis = false;
            this.mirrorYAxis = false;
            this.isSpecificQuadrant = false;
            this.isMove = true;
            this.isAttack = true;
            this.conditions = List.of();
            this.followUp = null;
        } else {
            this.pathBase = new Path(view.getBase());
            this.moveType = view.getType();
            this.mirrorXAxis = view.isMirrorXAxis();
            this.mirrorYAxis = view.isMirrorYAxis();
            this.isSpecificQuadrant = view.isOnlySpecificQuadrant();
            this.isMove = view.isMove();
            this.isAttack = view.isAttack();
            List<Conditional<Piece>> conditionalList = new ArrayList<>();
            ConditionalFactory cFactory = new ConditionalFactory(board, log);
            for (ConditionalView cv : view.getConditions()) {
                conditionalList.add(cFactory.build(cv));
            }
            this.conditions = conditionalList;
            ReferenceFactory rFactory = new ReferenceFactory(board, log);
            this.followUp = new ReferenceLogEntry<>(
                    board,
                    rFactory.build(view.getFollowUp().getTarget()),
                    rFactory.build(view.getFollowUp().getDestination())
            );
        }
    }

    public List<Movement> toMovementList(Plane<Piece> board, ThreatMap threatMap, Colour colour, Point offset,
            boolean onlyAttacks, boolean includeDefend) {
        if (colour == null || offset == null) {
            throw new NullPointerException("one or more arguments are null, colour: " + (colour == null)
                    + " point offset: " + (offset == null));
        }
        if (this.pathBase == null || this.pathBase.length() == 0) {
            System.err.println("path base is not defined");
            return List.of();
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

    public MoveView toView() {
        return new MoveView(this.pathBase, this.moveType, this.mirrorXAxis, this.mirrorYAxis, this.isSpecificQuadrant,
                this.isMove, this.isAttack, this.conditions, this.followUp);
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

        List<Point> points = new LinkedList<>();
        for (Point p : this.pathBase) {
            Point next = this.getVectorInQuadrant(p, offset, isRight, isUp);
            boolean isSafe = threatMap != null && threatMap.hasNoThreats(next);
            // Not a valid location, out of bounds, or fails its conditions
            if (next == null || !board.isInBounds(next) || (Pieces.isKing(board.get(offset)) && !isSafe)) {
                break;
            }

            Piece nPiece = board.get(next);
            if (nPiece != null) {
                boolean canCapture = Pieces.isOpponent(colour, nPiece);
                if (this.isAttack && (canCapture || includeDefend)) {
                    points.add(next);
                }

                boolean passAnyPiece = CustomMoveType.JUMP.equals(this.moveType);
                boolean passOppKing = onlyAttacks && canCapture && Pieces.isKing(nPiece);
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
     * @param board {@link Plane} for the Condition to verify with
     * @return true if all Condition pass, otherwise false
     */
    private boolean passesConditions(Plane<Piece> board) {
        if (board == null) {
            return false;
        }
        if (this.conditions == null) {
            return true;
        }
        for (Conditional<Piece> condition : this.conditions) {
            if (!condition.isExpected(board)) {
                return false;
            }
        }
        return true;
    }
}
