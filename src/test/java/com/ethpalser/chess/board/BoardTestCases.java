package com.ethpalser.chess.board;

public class BoardTestCases {

    // region In Progress
    // Move QD1->G4
    public static String[] inProgressPieceCanMove = new String[]{"wQd1", "wKe1*", "bKh8", "wRa7", "bPc2"};
    // Move QD1->D6
    public static String[] inProgressPieceCanCapture = new String[]{"wQd1", "wKe1*", "bKf6", "wPa4", "wPb4", "bPb5",
            "wNf3", "wBh5"};
    // Move QD1->Anywhere valid
    public static String[] inProgressNotOnlyKings = new String[]{"wQd1", "wKe1", "bKg7"};

    // endregion
    // region Stalemate
    // Move QD1->G4
    public static String[] stalematePieceCannotMove = new String[]{"wQd1", "wKe1*", "bKh8", "wRa7"};
    // Move QD1->D7
    public static String[] stalematePieceCannotCapture = new String[]{"wQd1", "wKe1*", "bKf6", "wPb4", "bPb5", "wNf3"
            , "wBh5"};
    // Move KE1->Anywhere valid
    public static String[] stalemateOnlyKings = new String[]{"wKe1", "bKg7"};

    // endregion
    // region Check
    // Move QD1->D7
    public static String[] checkPieceCanCapture = new String[]{"wQd1", "wKe1*", "bKd8", "wRa7", "bPd7", "bRh7"};
    // Move QD1->D8
    public static String[] checkPieceCanBlock = new String[]{"wQd1", "wKe1*", "bKg8", "bBc5", "bPf7", "bPg7", "bPh7"};
    // Move QD1->D7
    public static String[] checkKingCanMove = new String[]{"wQd1", "wKe1*", "bKg7", "wRa8", "bPf6", "bPg5", "bPh6"};
    // endregion
    // region Checkmate
    // Move QD1->D7
    public static String[] checkmatePieceCannotCapture = new String[]{"wQd1", "wKe1*", "bKd8", "bRd7", "wRh7"};
    // Move QD1->D8
    public static String[] checkmatePieceCannotBlock = new String[]{"wQd1", "wKe1*", "bKg8", "bPf7", "bPg7", "bPh7"};
    // Move QD1->D7
    public static String[] checkmateKingCannotMove = new String[]{"wQd1", "wKe1*", "bKg7", "wRa8", "bPf6", "bPg5",
            "bPh6", "wPh5"};

    // endregion
}
