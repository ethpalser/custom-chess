package com.ethpalser.chess.game.state;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.event.EventType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-21",
        majorVersion = 1,
        minorVersion = 1,
        lastModified = "2025-01-13"
)
public record GamePrompt(EventType eventType, Coordinate source, List<String> choices) {
    public GamePrompt {
        if (eventType == null || source == null || choices == null) {
            throw new IllegalArgumentException("One or more prompt values are null");
        }
        // Follow up is a special case, which should automatically treat an empty list as removing the source
        if (!EventType.MOVE_FOLLOW_UP.equals(eventType) && choices.isEmpty()) {
            throw new IllegalArgumentException("Provided prompt choices are empty");
        }
    }

    public GamePrompt() {
        this(EventType.NO_EVENT, new Point(), List.of());
    }
}
