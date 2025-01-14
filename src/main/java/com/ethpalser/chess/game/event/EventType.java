package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-19",
        lastModified = "2025-01-13"
)
public enum EventType {
    NO_EVENT,
    MOVE,
    MOVE_FOLLOW_UP,
    PROMOTE,
    SPECIAL,
    PAUSE,
    RESUME,
    CONCEDE;
}
