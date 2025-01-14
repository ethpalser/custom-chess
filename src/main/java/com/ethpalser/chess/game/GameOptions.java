package com.ethpalser.chess.game;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.move.config.MoveSpec;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-22",
        majorVersion = 1,
        minorVersion = 8,
        lastModified = "2025-01-13"
)
public record GameOptions(
        int width,
        int length,
        List<Coordinate> unavailable,
        Map<Coordinate, String> pieceStarts,
        Map<String, List<MoveSpec>> pieceSpecs
) {
    // Keeping GameOptions limited to a 2D ChessGame
    public GameOptions() {
        this(8, 8, new ArrayList<>(), new HashMap<>(), new HashMap<>());
        // Add all piece starts for a standard chess board
        for (int y : new int[]{0, length - 1}) {
            this.pieceStarts.putAll(Map.of(
                    new Point(0, y), PieceType.ROOK.toCode(),
                    new Point(1, y), PieceType.KNIGHT.toCode(),
                    new Point(2, y), PieceType.BISHOP.toCode(),
                    new Point(3, y), PieceType.QUEEN.toCode(),
                    new Point(4, y), PieceType.KING.toCode(),
                    new Point(5, y), PieceType.BISHOP.toCode(),
                    new Point(6, y), PieceType.KNIGHT.toCode(),
                    new Point(7, y), PieceType.ROOK.toCode()
            ));
        }
        for (int y : new int[]{1, length - 2}) {
            for (int x = 0; x < width; x++) {
                this.pieceStarts.put(new Point(x, y), PieceType.PAWN.toCode());
            }
        }
    }

    private GameOptions(GameOptionsBuilder builder) {
        this(builder.width, builder.length, builder.unavailable, builder.pieceStarts, builder.pieceSpecs);
    }

    public static GameOptionsBuilder Builder() {
        return new GameOptionsBuilder();
    }

    private static class GameOptionsBuilder {

        private int width;
        private int length;
        private List<Coordinate> unavailable;
        private Map<Coordinate, String> pieceStarts;
        private Map<String, List<MoveSpec>> pieceSpecs;

        private GameOptionsBuilder() {
            this.width = 8;
            this.length = 8;
            this.unavailable = new ArrayList<>();
            this.pieceStarts = new HashMap<>();
            this.pieceSpecs = new HashMap<>();
        }

        public GameOptionsBuilder width(int width) {
            this.width = width;
            return this;
        }


        public GameOptionsBuilder length(int length) {
            this.length = length;
            return this;
        }

        public GameOptionsBuilder unavailable(List<Coordinate> coordinates) {
            if (coordinates == null) {
                throw new IllegalArgumentException();
            }
            this.unavailable = coordinates;
            return this;
        }

        public GameOptionsBuilder addUnavailable(Coordinate coordinate) {
            this.unavailable.add(coordinate);
            return this;
        }

        public GameOptionsBuilder pieceStarts(Map<Coordinate, String> pieceStarts) {
            if (pieceStarts == null) {
                throw new IllegalArgumentException();
            }
            this.pieceStarts = pieceStarts;
            return this;
        }

        public GameOptionsBuilder addPieceStart(Coordinate coordinate, String pieceCode) {
            this.pieceStarts.put(coordinate, pieceCode);
            return this;
        }

        public GameOptionsBuilder pieceSpecs(Map<String, List<MoveSpec>> pieceSpecs) {
            if (pieceSpecs == null) {
                throw new IllegalArgumentException();
            }
            this.pieceSpecs = pieceSpecs;
            return this;
        }

        public GameOptionsBuilder addPieceSpec(String pieceCode, MoveSpec specification) {
            if (this.pieceSpecs.get(pieceCode) == null) {
                this.pieceSpecs.put(pieceCode, List.of(specification));
            } else {
                this.pieceSpecs.get(pieceCode).add(specification);
            }
            return this;
        }

        public GameOptions build() {
            return new GameOptions(this);
        }

    }


}
