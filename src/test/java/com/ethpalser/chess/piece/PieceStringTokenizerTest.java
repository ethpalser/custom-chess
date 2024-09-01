package com.ethpalser.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PieceStringTokenizerTest {

    @Test
    void testConstructor_givenEmptyString_hasFiveTokens() {
        String testStr = "";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        int count = 0;
        while (tokenizer.hasTokens()) {
            tokenizer.nextToken();
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    void testConstructor_givenInvalidColour_hasFiveTokensAndWhite() {
        String testStr = "gKe3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("K", tokenizer.nextToken());
        assertEquals("e", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenInvalidStandardCode_hasFiveTokensAndPawn() {
        String testStr = "wVe3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("P", tokenizer.nextToken());
        assertEquals("e", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenMalformedCustomCode_hasFiveTokensAndPawn() {
        String testStr = "wp~e3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("P", tokenizer.nextToken());
        assertEquals("e", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenLongMalformedCustomCode_hasFiveTokensAndPawn() {
        String testStr = "wdark-knight~e3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("P", tokenizer.nextToken());
        assertEquals("e", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenInvalidFile_hasFiveTokensAtFileA() {
        String testStr = "wND3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenMissingFile_hasFiveTokensAtFileA() {
        String testStr = "wN3";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("3", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenMissingRank_hasFiveTokensAtRank1() {
        String testStr = "wNd";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("d", tokenizer.nextToken());
        assertEquals("1", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenOutOfBoundsRank_hasFiveTokensAtRank1() {
        String testStr = "wN27*"; // Note: The tokenizer has no idea what the real bounds are, only a predefined limit
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("1", tokenizer.nextToken());
        assertEquals("*", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }


    @Test
    void testConstructor_givenMissingRankAndFile_hasFiveTokensAtA1() {
        String testStr = "wN";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("1", tokenizer.nextToken());
        assertEquals("", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenMissingRankAndFileAndNotMoved_hasFiveTokensAtA1AndNotMoved() {
        String testStr = "wN*";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("w", tokenizer.nextToken());
        assertEquals("N", tokenizer.nextToken());
        assertEquals("a", tokenizer.nextToken());
        assertEquals("1", tokenizer.nextToken());
        assertEquals("*", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenValidStandardPiece_hasFiveTokens() {
        String testStr = "bBb4*";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("b", tokenizer.nextToken());
        assertEquals("B", tokenizer.nextToken());
        assertEquals("b", tokenizer.nextToken());
        assertEquals("4", tokenizer.nextToken());
        assertEquals("*", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }

    @Test
    void testConstructor_givenValidCustomPiece_hasFiveTokens() {
        String testStr = "b~batman~b24*";
        PieceStringTokenizer tokenizer = new PieceStringTokenizer(testStr);

        assertEquals("b", tokenizer.nextToken());
        assertEquals("batman", tokenizer.nextToken());
        assertEquals("b", tokenizer.nextToken());
        assertEquals("24", tokenizer.nextToken());
        assertEquals("*", tokenizer.nextToken());
        assertThrows(IndexOutOfBoundsException.class, () -> tokenizer.nextToken());
    }
}
