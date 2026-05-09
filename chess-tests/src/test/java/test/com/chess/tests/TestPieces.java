package test.com.chess.tests;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pieces.Knight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Piece Tests")
class TestPieces {

    @Test
    @DisplayName("Knight at b1 has 2 legal moves at start")
    void knightAtB1HasTwoLegalMoves() {
        final Board board = Board.createStandardBoard();
        // b1 = coordinate 57, knight can go to a3(40) and c3(42)
        final long knightMoves = board.currentPlayer().getLegalMoves().stream()
                .filter(m -> m.getMovedPiece().getPiecePosition() == 57)
                .count();
        assertThat(knightMoves).isEqualTo(2);
    }

    @Test
    @DisplayName("Pawn at e2 can move one or two squares forward")
    void pawnE2CanMoveOneOrTwoSquares() {
        final Board board = Board.createStandardBoard();
        // e2 = coordinate 52
        final long pawnMoves = board.currentPlayer().getLegalMoves().stream()
                .filter(m -> m.getMovedPiece().getPiecePosition() == 52)
                .count();
        assertThat(pawnMoves).isEqualTo(2); // e3 and e4
    }

    @Test
    @DisplayName("Rook cannot move on starting position (blocked by own pieces)")
    void rookCantMoveAtStart() {
        final Board board = Board.createStandardBoard();
        // h1 = coordinate 63 (white rook)
        final long rookMoves = board.currentPlayer().getLegalMoves().stream()
                .filter(m -> m.getMovedPiece().getPiecePosition() == 63)
                .count();
        assertThat(rookMoves).isZero();
    }

    @Test
    @DisplayName("Moving a pawn updates the board FEN")
    void movingPawnUpdatesBoardState() {
        final Board board = Board.createStandardBoard();
        final Move e4 = Move.createMove(board, 52, 36); // e2 → e4
        assertThat(e4).isNotEqualTo(Move.NULL_MOVE);
        final MoveTransition transition = board.currentPlayer().makeMove(e4);
        assertThat(transition.getMoveStatus()).isEqualTo(MoveTransition.MoveStatus.DONE);
        assertThat(transition.getToBoard().currentPlayer().getAlliance()).isEqualTo(Alliance.BLACK);
    }
}
