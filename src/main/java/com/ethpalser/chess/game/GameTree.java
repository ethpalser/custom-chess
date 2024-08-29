package com.ethpalser.chess.game;

public class GameTree {

    private static final int WINNING_THRESHOLD = Integer.MAX_VALUE;
    private final Game root;

    public GameTree(Game root) {
        this.root = root;
    }

    public Action nextBest(int minimaxDepth, boolean greaterIsBest) {
        if (this.root == null || minimaxDepth <= 0) {
            return null;
        }

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Action best = null;

        Iterable<Action> iterable = root.potentialUpdates();
        for (Action action : iterable) {
            int value = alphabeta(action, minimaxDepth - 1, alpha, beta, !greaterIsBest);
            if (greaterIsBest && value > alpha) {
                alpha = value;
                best = action;
            } else if (!greaterIsBest && value < beta) {
                beta = value;
                best = action;
            }
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

        this.root.updateGame(node);
        Iterable<Action> it = this.root.potentialUpdates();
        if (depth <= 0 || !it.iterator().hasNext()) {
            int result = this.root.evaluateState();
            this.root.undoUpdate(1, false);
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
            this.root.undoUpdate(1, false);
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
            this.root.undoUpdate(1, false);
            return localMin;
        }
    }

}
