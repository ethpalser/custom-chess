package com.ethpalser.chess.game;

import com.ethpalser.chess.piece.Colour;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GameTreeTest {

    @Test
    void testNextBest_givenNull_thenNull() {
        MockNode testCase = null;
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);

        Action result = tree.nextBest(1, true);

        assertNull(result);
    }

    @Test
    void testNextBest_givenOnlyRoot_thenNull() {
        MockNode testCase = new MockNode(Colour.BLACK, 0, List.of());
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);

        Action result = tree.nextBest(1, true);

        assertNull(result);
    }


    @Test
    void testNextBest_givenInvalidDepth_thenNull() {
        MockNode testCase = new MockNode(Colour.BLACK, 0, List.of());
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);

        Action result = tree.nextBest(-1, true);

        assertNull(result);
    }

    @Test
    void testNextBest_givenDepthOfOneGeneratingTree_thenNotNull() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 1;

        Action result = tree.nextBest(depth, true);

        // Children from root are -5, 5 and 15 so 15 should be chosen
        assertNotNull(result);
        assertEquals(15, result.getEnd().getX());
        assertEquals(tree.minimax(depth), result.getEnd().getX());
    }

    @Test
    void testNextBest_givenLargeGeneratingTree_thenNotNull() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 4;

        Action result = tree.nextBest(depth, true);

        assertNotNull(result);
        // Expected value was manually determined
        assertEquals(5, result.getEnd().getX());
    }

    @Test
    void testMinimax_givenNull_thenMinimumValue() {
        MockNode testCase = null;
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 1;

        int result = tree.minimax(depth);
        assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    void testMinimax_givenOnlyRoot_thenMinimumValue() {
        MockNode testCase = new MockNode(Colour.BLACK, 0, List.of());
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 4;

        int result = tree.minimax(depth);
        assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    void testMinimax_givenInvalidDepth_thenMinimumValue() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = -1;

        int result = tree.minimax(depth);
        assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    void testMinimax_givenDepthOfOne_thenHasValue() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 1;

        int result = tree.minimax(depth);
        // Manually determined result
        assertEquals(15, result);
    }

    @Test
    void testMinimax_givenDepthOfTwo_thenHasValue() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 2;

        int result = tree.minimax(depth);
        // Manually determined result
        assertEquals(-9, result);
    }

    @Test
    void testMinimax_givenLargeDepth_thenHasValue() {
        MockNode testCase = this.testSnapshotTree();
        MockGame game = new MockGame(testCase);
        GameTree tree = new GameTree(game);
        int depth = 4;

        int result = tree.minimax(depth);
        // Manually determined result
        assertEquals(-1, result);
    }

    private MockNode testSnapshotTree() {
        return new MockNode(Colour.BLACK, 0, List.of(
                new MockNode(Colour.WHITE, -5, List.of(
                        new MockNode(Colour.BLACK, 6, List.of(
                                new MockNode(Colour.WHITE, 8, List.of(
                                        new MockNode(Colour.BLACK, -14, List.of()),
                                        new MockNode(Colour.BLACK, 14, List.of())
                                )),
                                new MockNode(Colour.WHITE, -8, List.of(
                                        new MockNode(Colour.BLACK, 4, List.of()),
                                        new MockNode(Colour.BLACK, -4, List.of())
                                ))
                        )),
                        new MockNode(Colour.BLACK, -9, List.of(
                                new MockNode(Colour.WHITE, -12, List.of()),
                                new MockNode(Colour.WHITE, 12, List.of())
                        ))
                )),
                new MockNode(Colour.WHITE, 5, List.of(
                        new MockNode(Colour.BLACK, -15, List.of(
                                new MockNode(Colour.WHITE, 1, List.of()),
                                new MockNode(Colour.WHITE, -11, List.of())
                        )),
                        new MockNode(Colour.BLACK, 10, List.of(
                                new MockNode(Colour.WHITE, -1, List.of())
                        ))
                )),
                new MockNode(Colour.WHITE, 15, List.of(
                        new MockNode(Colour.BLACK, -10, List.of(
                                new MockNode(Colour.WHITE, 3, List.of(
                                        new MockNode(Colour.BLACK, -2, List.of()),
                                        new MockNode(Colour.BLACK, 2, List.of())
                                )),
                                new MockNode(Colour.WHITE, -7, List.of(
                                        new MockNode(Colour.BLACK, 13, List.of()),
                                        new MockNode(Colour.BLACK, -13, List.of())
                                ))
                        )),
                        new MockNode(Colour.BLACK, -6, List.of(
                                new MockNode(Colour.WHITE, 7, List.of(
                                        new MockNode(Colour.BLACK, 9, List.of()),
                                        new MockNode(Colour.BLACK, 11, List.of())
                                )),
                                new MockNode(Colour.WHITE, -3, List.of(
                                        new MockNode(Colour.BLACK, Integer.MAX_VALUE, List.of()),
                                        new MockNode(Colour.BLACK, Integer.MIN_VALUE, List.of())
                                ))
                        ))
                ))
        ));
    }
}
