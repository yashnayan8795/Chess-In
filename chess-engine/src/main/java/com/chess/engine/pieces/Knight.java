package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Knight piece.
 * Moves in an "L" shape: 2 squares in one direction, 1 in the perpendicular.
 * The 8 possible offsets are: -17, -15, -10, -6, +6, +10, +15, +17
 * Edge-case exclusions are based on file membership to prevent board wrap-around.
 */
public final class Knight extends Piece {

    private static final int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    public Knight(int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            if (isFirstColumnExclusion(piecePosition, currentCandidateOffset) ||
                isSecondColumnExclusion(piecePosition, currentCandidateOffset) ||
                isSeventhColumnExclusion(piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
                continue;
            }
            final int candidateDestinationCoordinate = piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final var candidateTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateTile.isOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateTile.getPiece().orElseThrow();
                    if (this.pieceAlliance != pieceAtDestination.getPieceAlliance()) {
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override public String toString() { return PieceType.KNIGHT.toString(); }

    // ---- Column exclusion guards (prevent board wrap-around) ----
    private static boolean isFirstColumnExclusion(int position, int offset) {
        return BoardUtils.FIRST_FILE[position] && (offset == -17 || offset == -10 || offset == 6 || offset == 15);
    }
    private static boolean isSecondColumnExclusion(int position, int offset) {
        return BoardUtils.SECOND_FILE[position] && (offset == -10 || offset == 6);
    }
    private static boolean isSeventhColumnExclusion(int position, int offset) {
        return BoardUtils.SEVENTH_FILE[position] && (offset == 10 || offset == -6);
    }
    private static boolean isEighthColumnExclusion(int position, int offset) {
        return BoardUtils.EIGHTH_FILE[position] && (offset == 17 || offset == 10 || offset == -6 || offset == -15);
    }
}
