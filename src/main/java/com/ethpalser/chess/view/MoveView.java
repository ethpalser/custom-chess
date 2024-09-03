package com.ethpalser.chess.view;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.custom.CustomMoveType;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.stream.Collectors;

public class MoveView {

    private final List<Point> base;
    private final CustomMoveType type;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean onlySpecificQuadrant;
    private final boolean isMove;
    private final boolean isAttack;
    private final List<ConditionalView> conditions;
    private final ActionView followUp;

    public MoveView(Path pathBase, CustomMoveType type, boolean mirrorXAxis, boolean mirrorYAxis,
            boolean onlySpecificQuadrant, boolean isMove, boolean isAttack, List<Conditional<Piece>> conditionals,
            LogEntry<Point, Piece> followup) {
        if (pathBase == null) {
            this.base = List.of();
        } else {
            this.base = pathBase.toList();
        }
        this.type = type;
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
        if (followup == null) {
            this.followUp = null;
        } else {
            this.followUp = followup.toView();
        }
    }

    public List<Point> getBase() {
        return base;
    }

    public CustomMoveType getType() {
        return type;
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
