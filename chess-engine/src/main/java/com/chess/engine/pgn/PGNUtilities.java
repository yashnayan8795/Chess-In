package com.chess.engine.pgn;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.List;

/**
 * PGN (Portable Game Notation) parser and generator.
 *
 * PGN format example:
 *   [Event "Chess Platform Game"]
 *   [White "PlayerOne"]
 *   [Black "PlayerTwo"]
 *   [Result "1-0"]
 *
 *   1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 *
 */
public final class PGNUtilities {

    private PGNUtilities() { throw new AssertionError(); }

    /**
     * Converts a list of moves to PGN move text (e.g. "1. e4 e5 2. Nf3 Nc6").
     */
    public static String movesToPGN(List<Move> moves) {
        final StringBuilder sb = new StringBuilder();
        int moveNumber = 1;
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                sb.append(moveNumber++).append(". ");
            }
            sb.append(moves.get(i).toString()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Generates a full PGN string with tags + move text.
     */
    public static String generatePGN(String white, String black, String result,
                                      String event, List<Move> moves) {
        return "[Event \"" + event + "\"]\n" +
               "[White \"" + white + "\"]\n" +
               "[Black \"" + black + "\"]\n" +
               "[Result \"" + result + "\"]\n\n" +
               movesToPGN(moves) + " " + result;
    }

    /**
     * Returns the result string based on the final board state.
     */
    public static String getResult(Board board) {
        if (board.currentPlayer().isInCheckMate()) {
            return board.currentPlayer().getAlliance().isWhite() ? "0-1" : "1-0";
        }
        if (board.currentPlayer().isInStaleMate()) return "1/2-1/2";
        return "*"; // game in progress
    }
}
