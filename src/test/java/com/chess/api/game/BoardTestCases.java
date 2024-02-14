package com.chess.api.game;

import java.util.List;

public class BoardTestCases {

    public static List<String> inProgressPieceCanMove = List.of("e1#wK*", "h8#bK", "e7#wR", "g5#wR", "c4#bB");

    public static List<String> inProgressPieceCanCapture = List.of("e1#wK*", "f6#bK", "a4#wP", "b4#wP", "b5#bP",
            "c3#wP", "c4#bP", "d7#wQ", "f3#wN", "h5#wP");

    public static List<String> inProgressKingCanMove = List.of("e1#wK*", "f6#bK", "e7#bN");

    public static List<String> checkPieceCanCapture = List.of("e1#wK*", "e8#bK", "b7#bR", "e7#wQ", "e2#wR");

    public static List<String> checkPieceCanBlock = List.of("e1#wK*", "g8#bK", "b4#bB", "c8#wQ", "d2#wP", "f7#bP",
            "g7#bP", "h7#bP");

    public static List<String> checkKingCanMove = List.of("e1#wK*", "g7#bK", "c7#wR", "c8#wR", "f7#bP", "g6#bP",
            "h7#bP");

    public static List<String> stalemateKingCannotMove = List.of("e1#wK*", "h8#bK", "e7#wR", "g5#wR");

    public static List<String> stalematePiecesCannotMove = List.of("e1#wK*", "f6#bK", "b4#wP", "b5#bP", "c3#wP",
            "c4#bP", "d7#wQ", "f3#wN", "h5#wP");

    public static List<String> stalemateOnlyKings = List.of("e1#wK", "g4#bK");

    public static List<String> checkmateSupportedQueen = List.of("e1#wK*", "e8#bK", "e7#wQ", "e2#wR");

    public static List<String> checkmateCannotBlock = List.of("e1#wK*", "g8#bK", "c8#wQ", "d2#wP", "f7#bP", "g7#bP",
            "h7#bP");

    public static List<String> checkmateTrappedKing = List.of("e1#wK*", "g7#bK", "c7#wR", "c8#wR", "f7#bP", "g6#bP",
            "h7#bP", "h6#wP");

}
