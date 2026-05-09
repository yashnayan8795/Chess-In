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
 * King piece — moves one square in any of 8 directions.
 * Also generates castling moves (delegated to Player for full legality check).
 *
 * Tracks isCastled and kingSideCastleCapable / queenSideCastleCapable flags
 * so the Player can determine castling rights.
 */
public final class King extends Piece {

    private static final int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(int piecePosition, Alliance pieceAlliance,
                boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(int piecePosition, Alliance pieceAlliance, boolean isFirstMove,
                boolean isCastled, boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public boolean isCastled()                 { return isCastled; }
    public boolean isKingSideCastleCapable()   { return kingSideCastleCapable; }
    public boolean isQueenSideCastleCapable()  { return queenSideCastleCapable; }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
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
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(),
                        move.getMovedPiece().getPieceAlliance(),
                        false,
                        move.isCastlingMove(),
                        false,
                        false);
    }

    @Override public String toString() { return PieceType.KING.toString(); }

    private static boolean isFirstColumnExclusion(int position, int offset) {
        return BoardUtils.FIRST_FILE[position] && (offset == -9 || offset == -1 || offset == 7);
    }
    private static boolean isEighthColumnExclusion(int position, int offset) {
        return BoardUtils.EIGHTH_FILE[position] && (offset == 9 || offset == 1 || offset == -7);
    }
}
