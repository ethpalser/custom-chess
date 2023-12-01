package com.chess.api.model.movement;

import com.chess.api.model.Action;
import com.chess.api.model.Board;
import com.chess.api.model.Colour;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.condition.Reference;
import com.chess.api.model.piece.Piece;
import lombok.NonNull;

public class ExtraMovement {

    private final Reference reference;
    private final Vector2D source;
    private final Vector2D destination;
    private final boolean fixedCoordinates;

    public ExtraMovement() {
        this.source = null;
        this.destination = null;
        this.fixedCoordinates = true;
        this.reference = null;
    }

    public ExtraMovement(Vector2D source, Vector2D destination) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = true;
        this.reference = null;
    }

    public ExtraMovement(Vector2D source, Vector2D destination, boolean fixedCoordinates) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = fixedCoordinates;
        this.reference = null;
    }

    public ExtraMovement(Reference reference, Vector2D source, Vector2D destination, boolean fixedCoordinates) {
        this.source = source;
        this.destination = destination;
        this.fixedCoordinates = fixedCoordinates;
        this.reference = reference;
    }

    public void move(@NonNull Board board, Vector2D offset) {
        if (this.source == null || this.destination == null) {
            return;
        }
        Vector2D location;
        if (!fixedCoordinates) {
            // Todo: Update source to allow negative values
            location = new Vector2D(offset.getX() + source.getX(), offset.getY() + source.getY());
        } else {
            location = this.source;
        }
        Piece piece;
        if (this.reference != null) {
            piece = this.reference.getPieces(board, new Action(Colour.WHITE, new Vector2D(), offset)).get(0);
        } else {
            piece = board.getPiece(location);
        }
        if (piece != null) {
            piece.setPosition(this.destination);
        }
        board.setPiece(this.destination, piece);
    }


}
