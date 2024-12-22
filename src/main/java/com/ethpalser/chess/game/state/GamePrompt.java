package com.ethpalser.chess.game.state;

import com.ethpalser.chess.game.event.EventType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.List;

public record GamePrompt(EventType eventType, Coordinate source, List<String> choices) {
    public GamePrompt {
        if (eventType == null || source == null || choices == null || choices.isEmpty()) {
            throw new IllegalArgumentException("One or more prompt values are null or empty");
        }
    }

    public GamePrompt() {
        this(EventType.NO_EVENT, new Point(), List.of());
    }
}
