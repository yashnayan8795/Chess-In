package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/**
 * Strategy interface for computer move selection.
 * All AI implementations (MiniMax, Random, MCTS, etc.) must implement this.
 */
public interface MoveStrategy {

    /**
     * Select the best move for the current player on the given board.
     *
     * @param board the current game state
     * @return the chosen Move, or Move.NULL_MOVE if no legal moves exist
     */
    Move execute(Board board);

    /**
     * Human-readable name of this strategy (for display in UI/debug panel).
     */
    String name();

    /**
     * Number of positions evaluated during the last call to {@link #execute(Board)}.
     */
    long getNumBoardsEvaluated();
}
