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
 * Pawn piece — the most rule-heavy piece in chess.
 *
 * Special rules implemented:
 *  1. Single forward push
 *  2. Double pawn push (first move only)
 *  3. Diagonal capture (left and right)
 *  4. En passant capture
 *  5. Promotion (when reaching the back rank)
 */
public final class Pawn extends Piece {

    private static final int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            // Direction-aware offset: WHITE moves "up" (negative), BLACK moves "down" (positive)
            final int candidateDestinationCoordinate =
                    this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) continue;

            // ---- 1. Single forward push ----
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isOccupied()) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }
            // ---- 2. Double pawn push ----
            else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite())
                     || (BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isOccupied()
                        && !board.getTile(candidateDestinationCoordinate).isOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // ---- 3. Diagonal captures ----
            else if (currentCandidateOffset == 7
                    && !((BoardUtils.EIGHTH_FILE[this.piecePosition] && this.pieceAlliance.isWhite())
                      || (BoardUtils.FIRST_FILE[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece().orElseThrow();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                // ---- 4. En passant ----
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
            // ---- 5. Diagonal capture (other direction) ----
            else if (currentCandidateOffset == 9
                    && !((BoardUtils.FIRST_FILE[this.piecePosition] && this.pieceAlliance.isWhite())
                      || (BoardUtils.EIGHTH_FILE[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece().orElseThrow();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    /** Returns the Queen piece that this pawn promotes to (default = Queen). */
    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override public String toString() { return PieceType.PAWN.toString(); }
}
