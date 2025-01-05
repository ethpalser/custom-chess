package com.ethpalser.chess.game;

import com.ethpalser.chess.game.state.GamePrompt;
import java.util.Arrays;
import java.util.Objects;

public record GameSaveData(String[] pieceNotations, String[] logNotations, GamePrompt prompt) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSaveData that = (GameSaveData) o;
        return Arrays.equals(pieceNotations, that.pieceNotations) && Arrays.equals(logNotations, that.logNotations) && Objects.equals(prompt, that.prompt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(prompt);
        result = 31 * result + Arrays.hashCode(pieceNotations);
        result = 31 * result + Arrays.hashCode(logNotations);
        return result;
    }

    @Override
    public String toString() {
        return "GameSaveData{" +
                "boardPieces=" + Arrays.toString(pieceNotations) +
                ", logNotations=" + Arrays.toString(logNotations) +
                ", prompt=" + prompt +
                '}';
    }
}
