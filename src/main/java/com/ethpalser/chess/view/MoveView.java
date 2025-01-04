package com.ethpalser.chess.view;

import com.ethpalser.chess.move.Move;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Location;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Reference;
import java.util.List;
import java.util.stream.Collectors;

public class MoveView {

    private final List<Coordinate> base;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean onlySpecificQuadrant;
    private final boolean isMove;
    private final boolean isAttack;
    private final List<ConditionalView> conditions;
    private final ActionView followUp;

    public MoveView(Path pathBase, boolean mirrorXAxis, boolean mirrorYAxis,
            boolean onlySpecificQuadrant, boolean isMove, boolean isAttack, List<Conditional> conditionals,
            Move.FollowUp followup) {
        if (pathBase == null) {
            this.base = List.of();
        } else {
            this.base = pathBase.toList();
        }
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.onlySpecificQuadrant = onlySpecificQuadrant;
        this.isMove = isMove;
        this.isAttack = isAttack;
        if (conditionals == null) {
            this.conditions = List.of();
        } else {
            this.conditions = conditionals.stream().map(Conditional::toView).collect(Collectors.toList());
        }
        if (followup == null || followup.reference() == null || followup.path() == null || followup.path().isEmpty()) {
            this.followUp = null;
        } else {
            this.followUp = new ActionView(followup.reference(), new Reference(Location.POINT, Direction.AT,
                    followup.path().getPoint(followup.path().length() - 1)));
        }
    }

    public List<Coordinate> getBase() {
        return base;
    }

    public boolean isMirrorXAxis() {
        return mirrorXAxis;
    }

    public boolean isMirrorYAxis() {
        return mirrorYAxis;
    }

    public boolean isOnlySpecificQuadrant() {
        return onlySpecificQuadrant;
    }

    public boolean isMove() {
        return isMove;
    }

    public boolean isAttack() {
        return isAttack;
    }

    public List<ConditionalView> getConditions() {
        return conditions;
    }

    public ActionView getFollowUp() {
        return followUp;
    }
}
