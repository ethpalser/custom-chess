package com.chess.api.model.piece;

import com.chess.api.model.Coordinate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class Bishop implements Piece {

    @Setter
    private Coordinate coordinate;
    private Colour colour;
    private boolean moved;

    public Bishop(@NonNull Coordinate coordinate, Colour colour) {
        this.coordinate = coordinate;
        this.colour = colour;
        this.moved = false;
    }

}
