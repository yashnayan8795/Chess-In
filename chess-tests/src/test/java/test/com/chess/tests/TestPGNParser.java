package test.com.chess.tests;

import com.chess.engine.board.Board;
import com.chess.engine.pgn.FenUtilities;
import com.chess.engine.pgn.PGNUtilities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for FEN parsing and PGN generation.
 */
@DisplayName("FEN and PGN Parser Tests")
class TestPGNParser {

    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Test
    @DisplayName("FEN starting position can be parsed")
    void startingFenIsParseable() {
        final Board board = FenUtilities.createGameFromFEN(START_FEN);
        assertThat(board).isNotNull();
        assertThat(board.getWhitePieces()).hasSize(16);
        assertThat(board.getBlackPieces()).hasSize(16);
    }

    @Test
    @DisplayName("Board can be converted back to FEN")
    void boardConvertedToFen() {
        final Board board = Board.createStandardBoard();
        final String fen = FenUtilities.createFENFromGame(board);
        assertThat(fen).isNotBlank();
        assertThat(fen).contains("w"); // White to move
        assertThat(fen).contains("KQkq"); // All castling rights
    }

    @Test
    @DisplayName("FEN round-trip: parse then regenerate is stable")
    void fenRoundTripIsStable() {
        final Board board = FenUtilities.createGameFromFEN(START_FEN);
        final String regeneratedFen = FenUtilities.createFENFromGame(board);
        // The piece placement section must match exactly
        assertThat(regeneratedFen.split(" ")[0]).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }

    @Test
    @DisplayName("PGN generation produces valid move list")
    void pgnGenerationProducesValidText() {
        final String pgn = PGNUtilities.generatePGN("White", "Black", "1-0", "Test", java.util.List.of());
        assertThat(pgn).contains("[Event \"Test\"]");
        assertThat(pgn).contains("[White \"White\"]");
        assertThat(pgn).contains("1-0");
    }

    @Test
    @DisplayName("PGN result '*' for in-progress game")
    void pgnResultForActiveGame() {
        final Board board = Board.createStandardBoard();
        assertThat(PGNUtilities.getResult(board)).isEqualTo("*");
    }
}
