package com.ethpalser.chess.board;

import java.util.List;

public class BoardTestCases {

    // region In Progress
    // Move QD1->G4
    public static List<String> inProgressPieceCanMove = List.of("wQd1", "wKe1*", "bKh8", "wRa7", "bPc2");
    // Move QD1->D6
    public static List<String> inProgressPieceCanCapture = List.of("wQd1", "wKe1*", "bKf6", "wPa4", "wPb4", "bPb5", "wNf3", "wBh5");
    // Move QD1->Anywhere valid
    public static List<String> inProgressNotOnlyKings = List.of("wQd1", "wKe1", "bKg7");

    // endregion
    // region Stalemate
    // Move QD1->G4
    public static List<String> stalematePieceCannotMove = List.of("wQd1", "wKe1*", "bKh8", "wRa7");
    // Move QD1->D7
    public static List<String> stalematePieceCannotCapture = List.of("wQd1", "wKe1*", "bKf6", "wPb4", "bPb5", "wNf3", "wBh5");
    // Move KE1->Anywhere valid
    public static List<String> stalemateOnlyKings = List.of("wKe1", "bKg7");

    // endregion
    // region Check
    // Move QD1->D7
    public static List<String> checkPieceCanCapture = List.of("wQd1", "wKe1*", "bKd8", "wRa7", "bPd7", "bRh7");
    // Move QD1->D8
    public static List<String> checkPieceCanBlock = List.of("wQd1", "wKe1*", "bKg8", "bBc5", "bPf7", "bPg7", "bPh7");
    // Move QD1->D7
    public static List<String> checkKingCanMove = List.of("wQd1", "wKe1*", "bKg7", "wRa8", "bPf6", "bPg5", "bPh6");
    // endregion
    // region Checkmate
    // Move QD1->D7
    public static List<String> checkmatePieceCannotCapture = List.of("wQd1", "wKe1*", "bKd8", "wQe7", "wRh7");
    // Move QD1->D8
    public static List<String> checkmatePieceCannotBlock = List.of("wQd1", "wKe1*", "bKg8", "bPf7", "bPg7", "bPh7");
    // Move QD1->D7
    public static List<String> checkmateKingCannotMove = List.of("wQd1", "wKe1*", "bKg7", "wRa8", "bPf6", "bPg5", "bPh6", "wPh5");

    // endregion
}
