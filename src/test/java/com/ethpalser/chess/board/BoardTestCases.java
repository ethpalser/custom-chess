package com.ethpalser.chess.board;

import java.util.List;

public class BoardTestCases {

    // region In Progress
    // Move QD1->G4
    public static List<String> inProgressPieceCanMove = List.of("d1#wQ", "e1*#wK", "h8#bK", "a7#wR", "c2#bP");
    // Move QD1->D6
    public static List<String> inProgressPieceCanCapture = List.of("d1#wQ", "e1*#wK", "f6#bK", "a4#wP", "b4#wP", "b5#bP", "f3#wN", "h5#wB");
    // Move QD1->Anywhere valid
    public static List<String> inProgressNotOnlyKings = List.of("d1#wQ", "e1#wK", "g7#bK");

    // endregion
    // region Stalemate
    // Move QD1->G4
    public static List<String> stalematePieceCannotMove = List.of("d1#wQ", "e1*#wK", "h8#bK", "a7#wR");
    // Move QD1->D7
    public static List<String> stalematePieceCannotCapture = List.of("d1#wQ", "e1*#wK", "f6#bK", "b4#wP", "b5#bP", "f3#wN", "h5#wB");
    // Move KE1->Anywhere valid
    public static List<String> stalemateOnlyKings = List.of("e1#wK", "g7#bK");

    // endregion
    // region Check
    // Move QD1->D7
    public static List<String> checkPieceCanCapture = List.of("d1#wQ", "e1*#wK", "d8#bK", "a7#wR", "d7#bP", "h7#bR");
    // Move QD1->D8
    public static List<String> checkPieceCanBlock = List.of("d1#wQ", "e1*#wK", "g8#bK", "c5#bB", "f7#bP", "g7#bP", "h7#bP");
    // Move QD1->D7
    public static List<String> checkKingCanMove = List.of("d1#wQ", "e1*#wK", "g7#bK", "a8#wR", "f6#bP", "g5#bP", "h6#bP");
    // endregion
    // region Checkmate
    // Move QD1->D7
    public static List<String> checkmatePieceCannotCapture = List.of("d1#wQ", "e1*#wK", "d8#bK", "e7#wQ", "h7#wR");
    // Move QD1->D8
    public static List<String> checkmatePieceCannotBlock = List.of("d1#wQ", "e1*#wK", "g8#bK", "f7#bP", "g7#bP", "h7#bP");
    // Move QD1->D7
    public static List<String> checkmateKingCannotMove = List.of("d1#wQ", "e1*#wK", "g7#bK", "a8#wR", "f6#bP", "g5#bP", "h6#bP", "h5#wP");

    // endregion
}
