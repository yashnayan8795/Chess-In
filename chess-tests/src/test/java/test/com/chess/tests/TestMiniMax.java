package test.com.chess.tests;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.ai.MiniMax;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for MiniMax AI — correctness and performance.
 */
@DisplayName("MiniMax AI Tests")
class TestMiniMax {

    @Test
    @DisplayName("MiniMax depth=1 returns a legal move on starting board")
    void miniMaxReturnsLegalMove() {
        final Board board = Board.createStandardBoard();
        final MiniMax ai = new MiniMax(1);
        final Move move = ai.execute(board);
        assertThat(move).isNotEqualTo(Move.NULL_MOVE);
        assertThat(board.currentPlayer().getLegalMoves()).contains(move);
    }

    @Test
    @DisplayName("MiniMax evaluates more than 0 boards at depth=2")
    void miniMaxEvaluatesBoardsAtDepth2() {
        final Board board = Board.createStandardBoard();
        final MiniMax ai = new MiniMax(2);
        ai.execute(board);
        assertThat(ai.getNumBoardsEvaluated()).isGreaterThan(0L);
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    @DisplayName("MiniMax depth=3 completes within 30 seconds")
    void miniMaxDepth3CompletesInTime() {
        final Board board = Board.createStandardBoard();
        final MiniMax ai = new MiniMax(3);
        final Move move = ai.execute(board);
        assertThat(move).isNotNull();
    }

    @Test
    @DisplayName("MiniMax finds mate-in-one when available")
    void miniMaxFindsMateInOne() {
        // Scholars mate position, one move from checkmate (White to play Qxf7#)
        // FEN: r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4
        final String fen = "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4";
        final Board board = com.chess.engine.pgn.FenUtilities.createGameFromFEN(fen);
        final MiniMax ai = new MiniMax(2);
        final Move bestMove = ai.execute(board);
        // Best move should be Qxf7 (coordinate 45 → 13)
        assertThat(bestMove.getDestinationCoordinate()).isEqualTo(13); // f7 = coordinate 13
    }
}
