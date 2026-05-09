package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

/**
 * Standard board evaluator.
 *
 * Score = Material + Mobility + King Safety + Pawn Structure + Check bonus
 *
 * Evaluated from White's perspective:
 *   positive = white winning, negative = black winning
 */
public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS         = 50;
    private static final int CHECKMATE_BONUS      = 10000;
    private static final int DEPTH_BONUS          = 100;
    private static final int CASTLE_BONUS         = 60;
    private static final int MOBILITY_MULTIPLIER  = 5;
    private static final int ATTACK_MULTIPLIER    = 1;

    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    private StandardBoardEvaluator() {}

    public static StandardBoardEvaluator get() { return INSTANCE; }

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.whitePlayer(), depth)
             - scorePlayer(board, board.blackPlayer(), depth);
    }

    private int scorePlayer(Board board, Player player, int depth) {
        return material(player)
             + mobility(player)
             + kingThreats(player, depth)
             + castleBonus(player)
             + pawnStructure(player);
    }

    // ---- Material (piece values) ----
    private static int material(Player player) {
        return player.getActivePieces().stream()
                     .mapToInt(Piece::getPieceValue)
                     .sum();
    }

    // ---- Mobility (number of legal moves) ----
    private static int mobility(Player player) {
        return MOBILITY_MULTIPLIER * player.getLegalMoves().size();
    }

    // ---- King safety / check / checkmate ----
    private static int kingThreats(Player player, int depth) {
        if (player.getOpponent().isInCheckMate()) {
            return CHECKMATE_BONUS + (DEPTH_BONUS * depth);
        }
        if (player.getOpponent().isInCheck()) {
            return CHECK_BONUS;
        }
        return 0;
    }

    // ---- Castle bonus ----
    private static int castleBonus(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    // ---- Basic pawn structure: penalise doubled/isolated pawns ----
    private static int pawnStructure(Player player) {
        return PawnStructureAnalyzer.get().pawnStructureScore(player);
    }
}
