package com.ethpalser.chess.piece.standard;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveReport;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.PathOptions;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.List;

public class King implements Piece {

    private final Colour colour;
    private Coordinate point;
    private boolean hasMoved;

    public King(Colour colour, Coordinate point) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = false;
    }

    public King(Colour colour, Coordinate point, boolean hasMoved) {
        this.colour = colour;
        this.point = point;
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        return "K";
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Coordinate getCoordinate() {
        return this.point;
    }

    @Override
    public void setCoordinate(Coordinate point) {
        this.point = point;
    }

    @Override
    public MoveSet getMoves(GameContext.Record context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        MoveSpec vSpec = new MoveSpec(new PathOptions(new Point(0, 1)), true, false);
        MoveSpec hSpec = new MoveSpec(new PathOptions(new Point(1, 0)), false, true);
        MoveSpec dSpec = new MoveSpec(new PathOptions(new Point(1, 1)), true, true);

        List<MoveReport> results = new ArrayList<>(8);
        results.addAll(vSpec.toMoveList(context, this.point, this.colour));
        results.addAll(hSpec.toMoveList(context, this.point, this.colour));
        results.addAll(dSpec.toMoveList(context, this.point, this.colour));

        Board<Coordinate> board = context.getBoard();
        ThreatMap opponentThreats = context.getThreats(Colour.opposite(this.colour));
        // King conditions, defined in if-statement instead of in MoveSpec
        if (this.hasMoved || opponentThreats == null || !opponentThreats.hasNoThreats(this.point)) {
            return new MoveSet(results);
        }

        // Special Move: Castle
        int startRank = Colour.WHITE.equals(this.colour) ? board.space().min(Space.AXIS.Y) :
                board.space().max(Space.AXIS.Y);
        // Queen-side Castle
        Coordinate[] castleQueenPath = new Coordinate[] {
                this.point.translate(1, Direction.LEFT.vector()),
                this.point.translate(2, Direction.LEFT.vector())
        };
        // Cannot move along this path if threatened or blocked
        boolean isQueenSideSafe = true;
        for (int i = 0; i < castleQueenPath.length && isQueenSideSafe; i++) {
            isQueenSideSafe = isEmptyAndSafe(board, opponentThreats, castleQueenPath[i]);
        }
        // Queen-side rook starts at the left-most edge of the board
        Coordinate qsrStart = new Point(board.space().min(Space.AXIS.X), startRank);
        Piece queenSideRook = board.get(qsrStart);
        if (queenSideRook != null && !queenSideRook.getHasMoved() && isQueenSideSafe) {
            MoveSpec castleQueen = (new MoveSpec.Builder(new PathOptions(castleQueenPath)))
                    .isAttack(false)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(true)
                    .followUp(
                            new Reference(Reference.Location.WEST_EDGE, Direction.AT),
                            new PathOptions(new Reference(Reference.Location.POINT, Direction.RIGHT))
                    )
                    .build();
            results.addAll(castleQueen.toMoveList(context, this.point, this.colour));
        }
        // King-side castle
        Coordinate[] castleKingPath = new Coordinate[]{
                this.point.translate(1, Direction.RIGHT.vector()),
                this.point.translate(2, Direction.RIGHT.vector())
        };
        // Cannot move along this path if threatened or blocked
        boolean isKingSideSafe = true;
        for (int i = 0; i < castleKingPath.length && isKingSideSafe; i++) {
            isKingSideSafe = isEmptyAndSafe(board, opponentThreats, castleKingPath[i]);
        }
        // King-side rook starts at the right-most edge of the board
        Point ksrStart = new Point(board.space().max(Space.AXIS.X), startRank);
        Piece kingSideRook = board.get(ksrStart);
        if (kingSideRook != null && !kingSideRook.getHasMoved() && isKingSideSafe) {
            MoveSpec castleKing = (new MoveSpec.Builder(new PathOptions(castleKingPath)))
                    .isAttack(false)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .followUp(
                            new Reference(Reference.Location.EAST_EDGE, Direction.AT),
                            new PathOptions(new Reference(Reference.Location.POINT, Direction.LEFT))
                    )
                    .build();
            results.addAll(castleKing.toMoveList(context, this.point, this.colour));
        }
        return new MoveSet(results);
    }

    @Override
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canPromote(Board<Coordinate> board) {
        return false;
    }

    @Override
    public List<String> promoteOptions() {
        return List.of();
    }

    // PRIVATE METHODS

    private boolean isEmptyAndSafe(Board<Coordinate> board, ThreatMap threatMap, Coordinate coordinate) {
        return board.get(coordinate) == null && threatMap != null && threatMap.hasNoThreats(coordinate);
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.point.toString() + (this.hasMoved ? "" : "*");
    }
}
