package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

/**
 * Interface for board position evaluators.
 * Implementations score the board from white's perspective:
 *   positive score → white is winning
 *   negative score → black is winning
 */
public interface BoardEvaluator {

    /**
     * @param board      the board to evaluate
     * @param depth      remaining search depth (used for checkmate scoring)
     * @return           integer score (positive = white advantage)
     */
    int evaluate(Board board, int depth);
}
