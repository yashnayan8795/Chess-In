package test.com.chess.tests;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Board creation, tile access, piece counts.
 */
@DisplayName("Board Tests")
class TestBoard {

    @Test
    @DisplayName("Standard board has 64 tiles")
    void standardBoardHas64Tiles() {
        final Board board = Board.createStandardBoard();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            assertThat(board.getTile(i)).isNotNull();
        }
    }

    @Test
    @DisplayName("Standard board has 16 white pieces")
    void standardBoardHas16WhitePieces() {
        assertThat(Board.createStandardBoard().getWhitePieces()).hasSize(16);
    }

    @Test
    @DisplayName("Standard board has 16 black pieces")
    void standardBoardHas16BlackPieces() {
        assertThat(Board.createStandardBoard().getBlackPieces()).hasSize(16);
    }

    @Test
    @DisplayName("White moves first in standard game")
    void whiteMoveFirstOnStandardBoard() {
        final Board board = Board.createStandardBoard();
        assertThat(board.currentPlayer().getAlliance()).isEqualTo(Alliance.WHITE);
    }

    @Test
    @DisplayName("White has 20 legal moves at the start")
    void whiteLegalMovesAtStart() {
        final Board board = Board.createStandardBoard();
        // 16 pawn moves (each pawn can move 1 or 2 squares) + 4 knight moves = 20
        assertThat(board.currentPlayer().getLegalMoves()).hasSize(20);
    }

    @Test
    @DisplayName("FEN algebraic notation converts correctly")
    void algebraicNotationIsCorrect() {
        assertThat(BoardUtils.getPositionAtCoordinate(0)).isEqualTo("a8");
        assertThat(BoardUtils.getPositionAtCoordinate(63)).isEqualTo("h1");
        assertThat(BoardUtils.getCoordinateAtPosition("e4")).isEqualTo(36);
    }
}
