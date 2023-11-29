package com.chess.api.model.movement;

import com.chess.api.model.Board;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.Piece;
import lombok.NonNull;

public class ExtraMovement {

    private final Reference reference;
    private final Coordinate source;
    private final Coordinate destination;
    private final boolean fixedCoordinates;

    public ExtraMovement() {
        this.source = null;
        this.destination = null;
        this.fixedCoordinates = true;
        this.reference = null;
    }

    public ExtraMovement(Coordinate source, Coordinate destination) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = true;
        this.reference = null;
    }

    public ExtraMovement(Coordinate source, Coordinate destination, boolean fixedCoordinates) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = fixedCoordinates;
        this.reference = null;
    }

    public ExtraMovement(Reference reference, Coordinate source, Coordinate destination, boolean fixedCoordinates) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = fixedCoordinates;
        this.reference = reference;
    }

    public void move(@NonNull Board board, Coordinate offset) {
        if (this.source == null || this.destination == null) {
            return;
        }
        Coordinate location;
        if (!fixedCoordinates) {
            // Todo: Update source to allow negative values
            location = new Coordinate(offset.getX() + source.getX(), offset.getY() + source.getY());
        } else {
            location = this.source;
        }
        Piece piece;
        if (this.reference != null) {
            piece = board.getReferencePiece(this.reference, offset, offset).get(0);
        } else {
            piece = board.getPiece(location);
        }
        if (piece != null) {
            piece.performMove(this.destination);
        }
        board.updatePieceLocation(this.source, this.destination);
    }


}
