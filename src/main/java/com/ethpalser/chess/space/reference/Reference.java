package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.space.Plane;
import java.util.List;

public interface Reference<T> {

    Location getLocation();

    List<T> getReferences(Plane<T> plane);

}
