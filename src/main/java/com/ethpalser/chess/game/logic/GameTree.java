package com.ethpalser.chess.game.logic;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.event.Action;
import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.game.GameStatus;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-08-28",
        majorVersion = 1,
        minorVersion = 7,
        lastModified = "2025-01-13"
)
public class GameTree {

    private static final int WINNING_THRESHOLD = Integer.MAX_VALUE / 4;
    private final Game root;

    public GameTree(Game root) {
        this.root = root;
    }

    public Action nextBest(int depth) {
        if (this.root == null || depth <= 0) {
            return null;
        }

        Action best = null;

        boolean maximizingPlayer = this.root.turn() % 2 != 0; // Should correspond to when White player acts
//        for (int d = 1; d <= depth; d++) {
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            Iterable<Action> iterable = this.root.potentialUpdates();
            for (Action action : iterable) {
                int value = alphabeta(action, depth - 1, alpha, beta, !maximizingPlayer);
                if (maximizingPlayer && value > alpha) {
                    // Winning move shouldn't be ignored if available, as it was deemed min and max for a branch.
                    if (alpha >= WINNING_THRESHOLD) {
                        return action;
                    }
//                    if (d == depth) {
                        alpha = value;
                        best = action;
//                    }
                } else if (!maximizingPlayer && value < beta) {
                    // Winning move shouldn't be ignored if available, as it was deemed min and max for a branch.
                    if (beta <= -WINNING_THRESHOLD) {
                        return action;
                    }
//                    if (d == depth) {
                        beta = value;
                        best = action;
//                    }
                }
//            }
        }
        return best;
    }

    public int minimax(int depth) {
        if (this.root == null || depth <= 0) {
            return Integer.MIN_VALUE;
        }

        int best = Integer.MIN_VALUE;
        for (int d = 1; d <= depth; d++) {
            Iterable<Action> it = this.root.potentialUpdates();

            int alpha = Integer.MIN_VALUE;
            for (Action action : it) {
                alpha = Math.max(alpha, alphabeta(action, d - 1, alpha, Integer.MAX_VALUE, false));
            }
            // Winning move shouldn't be ignored if available, as it was deemed min and max for a branch.
            if (alpha >= WINNING_THRESHOLD) {
                return alpha;
            }
            // Maximize once at the deepest ply, as this will be the most informed
            if (d == depth) {
                best = Math.max(best, alpha);
            }
        }
        return best;
    }

    private int alphabeta(Action node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (node == null) {
            return maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        GameStatus status = this.root.update(node);
        if (GameStatus.NO_CHANGE.equals(status)) {
            return maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        if (GameStatus.WHITE_WIN.equals(status)) {
            int result = Integer.MAX_VALUE;
            this.root.undo();
            return result;
        }
        if (GameStatus.BLACK_WIN.equals(status)) {
            int result = Integer.MIN_VALUE;
            this.root.undo();
            return result;
        }

        Iterable<Action> it = this.root.potentialUpdates();
        if (depth <= 0 || !it.iterator().hasNext()) {
            int result = this.root.score();
            this.root.undo();
            return result;
        }

        if (maximizingPlayer) {
            int localMax = alpha;
            for (Action action : it) {
                localMax = Math.max(localMax, alphabeta(action, depth - 1, localMax, beta, false));
                // A case was encountered that guarantees minimax decision won't change (player wouldn't choose this)
                if (localMax >= beta) {
                    break;
                }
            }
            this.root.undo();
            return localMax;
        } else {
            int localMin = beta;
            for (Action action : it) {
                localMin = Math.min(localMin, alphabeta(action, depth - 1, alpha, localMin, true));
                // A case was encountered that guarantees minimax decision won't change (player wouldn't choose this)
                if (localMin <= alpha) {
                    break;
                }
            }
            this.root.undo();
            return localMin;
        }
    }

}
