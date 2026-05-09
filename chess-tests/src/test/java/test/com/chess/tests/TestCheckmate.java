package test.com.chess.tests;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pgn.FenUtilities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for checkmate detection using known positions.
 * Uses FEN strings to set up specific board states.
 */
@DisplayName("Checkmate Tests")
class TestCheckmate {

    /**
     * Scholar's Mate (4-move checkmate):
     * 1. e4 e5 2. Bc4 Nc6 3. Qh5 Nf6?? 4. Qxf7#
     * FEN after Qxf7#: r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4
     */
    @Test
    @DisplayName("Scholars Mate results in checkmate for Black")
    void scholarsMateIsCheckmate() {
        final String fen = "r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4";
        final Board board = FenUtilities.createGameFromFEN(fen);
        assertThat(board.currentPlayer().isInCheckMate()).isTrue();
        assertThat(board.currentPlayer().isInCheck()).isTrue();
        assertThat(board.currentPlayer().getLegalMoves()).isEmpty();
    }

    /**
     * Back-rank mate: White Queen on d8, Black King on e8, all escape squares covered.
     * FEN: 3Qk3/8/8/8/8/8/8/4K3 b - - 0 1
     */
    @Test
    @DisplayName("Back-rank mate is detected")
    void backRankMateDetected() {
        final String fen = "3Qk3/8/8/8/8/8/8/4K3 b - - 0 1";
        final Board board = FenUtilities.createGameFromFEN(fen);
        assertThat(board.currentPlayer().isInCheckMate()).isTrue();
    }

    @Test
    @DisplayName("Starting position is not checkmate")
    void startingPositionIsNotCheckmate() {
        assertThat(Board.createStandardBoard().currentPlayer().isInCheckMate()).isFalse();
    }
}
