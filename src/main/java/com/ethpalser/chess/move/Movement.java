package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.Optional;

public interface Movement {

    Path getPath();

    Path getPath(Plane<Piece> plane, Colour colour, Point start, Point end); // I hate this

    Optional<LogEntry<Point, Piece>> getFollowUpMove();

}
