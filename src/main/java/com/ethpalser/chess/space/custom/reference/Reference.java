package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.game.view.ReferenceView;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import java.util.List;

public interface Reference<T extends Positional> {

    Location getLocation();

    List<T> getReferences(Plane<T> plane);

    ReferenceView toView();

}
