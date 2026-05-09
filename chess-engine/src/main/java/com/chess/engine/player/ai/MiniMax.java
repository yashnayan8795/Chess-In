package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.Player;

/**
 * MiniMax algorithm with Alpha-Beta Pruning.
 *
 * Alpha-Beta pruning dramatically reduces the search space by eliminating
 * branches that cannot possibly affect the final result.
 *
 *   alpha = best score the maximiser (White) can guarantee
 *   beta  = best score the minimiser (Black) can guarantee
 *
 * Time complexity: O(b^(d/2)) with perfect ordering (vs O(b^d) without pruning)
 */
public final class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long numBoardsEvaluated;

    public MiniMax(int searchDepth) {
        this.boardEvaluator   = StandardBoardEvaluator.get();
        this.searchDepth      = searchDepth;
        this.numBoardsEvaluated = 0;
    }

    @Override
    public String name() { return "MiniMax (depth=" + searchDepth + ")"; }

    @Override
    public long getNumBoardsEvaluated() { return numBoardsEvaluated; }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        this.numBoardsEvaluated = 0;

        Move bestMove = Move.NULL_MOVE;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue  = Integer.MAX_VALUE;

        int currentValue;

        System.out.printf("%s THINKING with depth = %d%n", board.currentPlayer(), searchDepth);

        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final var moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite()
                        ? min(moveTransition.getToBoard(), searchDepth - 1, highestSeenValue, lowestSeenValue)
                        : max(moveTransition.getToBoard(), searchDepth - 1, highestSeenValue, lowestSeenValue);

                if (board.currentPlayer().getAlliance().isWhite()
                        && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack()
                        && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("Boards evaluated: %d | Time: %dms | Best move: %s%n",
                numBoardsEvaluated, executionTime, bestMove);

        return bestMove;
    }

    // ---- Maximiser (White) ----
    private int max(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || isGameOver(board)) {
            this.numBoardsEvaluated++;
            return boardEvaluator.evaluate(board, depth);
        }
        int highest = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final var transition = board.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                final int currentValue = min(transition.getToBoard(), depth - 1, alpha, beta);
                if (currentValue > highest) highest = currentValue;
                if (highest >= beta) return highest; // beta cut-off
                alpha = Math.max(alpha, highest);
            }
        }
        return highest;
    }

    // ---- Minimiser (Black) ----
    private int min(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || isGameOver(board)) {
            this.numBoardsEvaluated++;
            return boardEvaluator.evaluate(board, depth);
        }
        int lowest = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final var transition = board.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                final int currentValue = max(transition.getToBoard(), depth - 1, alpha, beta);
                if (currentValue < lowest) lowest = currentValue;
                if (lowest <= alpha) return lowest; // alpha cut-off
                beta = Math.min(beta, lowest);
            }
        }
        return lowest;
    }

    private static boolean isGameOver(Board board) {
        return board.currentPlayer().isInCheckMate()
            || board.currentPlayer().isInStaleMate();
    }
}
