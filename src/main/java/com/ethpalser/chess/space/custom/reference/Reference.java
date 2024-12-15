package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.view.ReferenceView;
import java.util.List;

public interface Reference<T extends Positional> {

    Location getLocation();

    List<T> getReferences(Board<Coordinate> plane);

    ReferenceView toView();

}
