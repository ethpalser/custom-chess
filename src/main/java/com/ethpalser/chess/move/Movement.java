package com.ethpalser.chess.move;

import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import java.util.Optional;

public interface Movement {

    Path getPath();

    LogEntry<Point, Piece> getFollowUpMove();

}
