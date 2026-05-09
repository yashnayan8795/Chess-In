package test.com.chess.tests;

import com.chess.engine.board.Board;
import com.chess.engine.pgn.FenUtilities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for stalemate detection.
 */
@DisplayName("Stalemate Tests")
class TestStaleMate {

    /**
     * Classic stalemate: Black King in corner, White Queen one square away.
     * FEN: 7k/8/6Q1/8/8/8/8/7K b - - 0 1
     */
    @Test
    @DisplayName("Stalemate position correctly detected")
    void staleMateDetected() {
        final String fen = "7k/8/6Q1/8/8/8/8/7K b - - 0 1";
        final Board board = FenUtilities.createGameFromFEN(fen);
        assertThat(board.currentPlayer().isInStaleMate()).isTrue();
        assertThat(board.currentPlayer().isInCheck()).isFalse();
    }

    @Test
    @DisplayName("Stalemate is not checkmate")
    void staleMateIsNotCheckmate() {
        final String fen = "7k/8/6Q1/8/8/8/8/7K b - - 0 1";
        final Board board = FenUtilities.createGameFromFEN(fen);
        assertThat(board.currentPlayer().isInCheckMate()).isFalse();
        assertThat(board.currentPlayer().isInStaleMate()).isTrue();
    }

    @Test
    @DisplayName("Standard board is not stalemate")
    void standardBoardIsNotStaleMate() {
        assertThat(Board.createStandardBoard().currentPlayer().isInStaleMate()).isFalse();
    }
}
