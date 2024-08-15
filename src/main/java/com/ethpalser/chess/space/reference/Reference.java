package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import java.util.List;

public interface Reference<T extends Positional> {

    Location getLocation();

    List<T> getReferences(Plane<T> plane);

}
