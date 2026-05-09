package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.MoveTransition.MoveStatus;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract representation of a chess player (White or Black).
 *
 * Responsibilities:
 *  - Tracks active pieces on the board
 *  - Computes legal moves (pseudo-legal + filter out king-in-check)
 *  - Manages castling move availability
 *  - Determines: isInCheck, isInCheckMate, isInStaleMate, isKingSideCastleCapable, etc.
 */
public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    protected Player(Board board,
                     Collection<Move> legalMoves,
                     Collection<Move> opponentMoves) {
        this.board       = board;
        this.playerKing  = establishKing();
        this.isInCheck   = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        // Include castling moves in the complete legal set
        this.legalMoves  = Collections.unmodifiableList(
                Stream.concat(legalMoves.stream(), calculateKingCastles(legalMoves, opponentMoves).stream())
                      .collect(Collectors.toList())
        );
    }

    // ---- Abstract API ----
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                              Collection<Move> opponentLegals);

    // ---- King safety ----
    public King getPlayerKing() { return playerKing; }

    public boolean isInCheck()      { return isInCheck; }
    public boolean isInCheckMate()  { return isInCheck && !hasEscapeMoves(); }
    public boolean isInStaleMate()  { return !isInCheck && !hasEscapeMoves(); }
    public boolean isCastled()      { return playerKing.isCastled(); }
    public boolean isKingSideCastleCapable()  { return playerKing.isKingSideCastleCapable(); }
    public boolean isQueenSideCastleCapable() { return playerKing.isQueenSideCastleCapable(); }

    public Collection<Move> getLegalMoves() { return legalMoves; }

    /** Attempt to make a move; returns MoveTransition describing success/failure. */
    public MoveTransition makeMove(Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(board, board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        final Collection<Move> kingAttacks = calculateAttacksOnTile(
                transitionedBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionedBoard.currentPlayer().getLegalMoves()
        );
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(board, board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(board, transitionedBoard, move, MoveStatus.DONE);
    }

    public boolean isMoveLegal(Move move) {
        return legalMoves.contains(move);
    }

    // ---- Helpers ----
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition,
                                                              Collection<Move> moves) {
        return moves.stream()
                    .filter(move -> move.getDestinationCoordinate() == piecePosition)
                    .collect(Collectors.toUnmodifiableList());
    }

    private King establishKing() {
        return (King) getActivePieces().stream()
                .filter(p -> p.getPieceType() == Piece.PieceType.KING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid board: no king found for " + getAlliance()));
    }

    private boolean hasEscapeMoves() {
        return legalMoves.stream()
                .anyMatch(move -> makeMove(move).getMoveStatus().isDone());
    }
}
