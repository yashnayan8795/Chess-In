package com.chess.engine.player.ai;

import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analyses pawn structure and returns a penalty score for weaknesses.
 *
 * Penalties applied:
 *  - Doubled pawns: two pawns on the same file
 *  - Isolated pawns: no friendly pawn on an adjacent file
 */
public final class PawnStructureAnalyzer {

    private static final int ISOLATED_PAWN_PENALTY  = -10;
    private static final int DOUBLED_PAWN_PENALTY   = -10;

    private static final PawnStructureAnalyzer INSTANCE = new PawnStructureAnalyzer();
    private PawnStructureAnalyzer() {}
    public static PawnStructureAnalyzer get() { return INSTANCE; }

    public int pawnStructureScore(Player player) {
        final List<Piece> playerPawns = playerPawns(player);
        return calculatePawnColumnScore(playerPawns) + isolatedPawnScore(playerPawns);
    }

    private List<Piece> playerPawns(Player player) {
        return player.getActivePieces().stream()
                     .filter(p -> p.getPieceType() == Piece.PieceType.PAWN)
                     .collect(Collectors.toList());
    }

    /** Doubled pawn penalty: count how many pawns share a column. */
    private int calculatePawnColumnScore(List<Piece> pawns) {
        int score = 0;
        final int[] pawnCountPerColumn = new int[8];
        for (Piece pawn : pawns) {
            pawnCountPerColumn[pawn.getPiecePosition() % 8]++;
        }
        for (int count : pawnCountPerColumn) {
            if (count > 1) score += (count - 1) * DOUBLED_PAWN_PENALTY;
        }
        return score;
    }

    /** Isolated pawn penalty: no pawn on adjacent files. */
    private int isolatedPawnScore(List<Piece> pawns) {
        int score = 0;
        final boolean[] occupiedFiles = new boolean[8];
        for (Piece pawn : pawns) occupiedFiles[pawn.getPiecePosition() % 8] = true;

        for (int file = 0; file < 8; file++) {
            if (!occupiedFiles[file]) continue;
            final boolean leftEmpty  = (file == 0) || !occupiedFiles[file - 1];
            final boolean rightEmpty = (file == 7) || !occupiedFiles[file + 1];
            if (leftEmpty && rightEmpty) score += ISOLATED_PAWN_PENALTY;
        }
        return score;
    }
}
