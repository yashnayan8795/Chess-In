package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.player.Player;

/**
 * Analyses king safety by counting the number of attacks on tiles
 * adjacent to the king — approximates exposure risk.
 */
public final class KingSafetyAnalyzer {

    private static final KingSafetyAnalyzer INSTANCE = new KingSafetyAnalyzer();
    private KingSafetyAnalyzer() {}

    public static KingSafetyAnalyzer get() { return INSTANCE; }

    /**
     * Returns a safety score (0 = safe, negative = exposed).
     * Called from StandardBoardEvaluator or a dedicated security score method.
     */
    public int kingSafetyScore(Player player) {
        final int kingPosition = player.getPlayerKing().getPiecePosition();
        final long attacksOnKingZone = player.getOpponent().getLegalMoves().stream()
                .filter(move -> isAdjacentToKing(move.getDestinationCoordinate(), kingPosition))
                .count();
        // Each attack on the king zone deducts 15 points
        return (int) (-15 * attacksOnKingZone);
    }

    private static boolean isAdjacentToKing(int destination, int kingPos) {
        int[] kingZoneOffsets = {-9, -8, -7, -1, 1, 7, 8, 9};
        for (int offset : kingZoneOffsets) {
            if (kingPos + offset == destination) return true;
        }
        return destination == kingPos; // direct king attack
    }
}
