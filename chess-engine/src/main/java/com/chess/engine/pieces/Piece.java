package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

/**
 * Abstract base class for all chess pieces.
 *
 * Design notes:
 *  - Pieces are VALUE OBJECTS: equal if they are the same type, alliance, position,
 *    and first-move status.
 *  - Each subclass implements {@link #calculateLegalMoves(Board)} using its own
 *    movement rules.
 *  - {@link #movePiece(Move)} returns a NEW piece at the new position (immutability).
 */
public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    protected Piece(PieceType pieceType, int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        this.pieceType     = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove   = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    // ---- Abstract API ----

    /** Generate all pseudo-legal (candidate) moves for this piece on the given board. */
    public abstract Collection<Move> calculateLegalMoves(Board board);

    /** Return a new Piece of the same type placed at the move's destination. */
    public abstract Piece movePiece(Move move);

    // ---- Accessors ----
    public PieceType getPieceType()      { return pieceType; }
    public int getPiecePosition()        { return piecePosition; }
    public Alliance getPieceAlliance()   { return pieceAlliance; }
    public boolean isFirstMove()         { return isFirstMove; }
    public int getPieceValue()           { return pieceType.getPieceValue(); }

    // ---- Value-Object equality ----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        return piecePosition == piece.piecePosition
                && isFirstMove == piece.isFirstMove
                && pieceType == piece.pieceType
                && pieceAlliance == piece.pieceAlliance;
    }

    @Override
    public int hashCode() { return cachedHashCode; }

    private int computeHashCode() {
        return Objects.hash(pieceType, pieceAlliance, piecePosition, isFirstMove);
    }

    // ---- Piece type catalogue ----
    public enum PieceType {
        PAWN("P", 100),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        ROOK("R", 500),
        QUEEN("Q", 900),
        KING("K", 10000);

        private final String pieceName;
        private final int pieceValue;

        PieceType(String pieceName, int pieceValue) {
            this.pieceName  = pieceName;
            this.pieceValue = pieceValue;
        }

        public int getPieceValue() { return pieceValue; }
        public boolean isKing()   { return this == KING; }
        public boolean isRook()   { return this == ROOK; }

        @Override
        public String toString() { return pieceName; }
    }
}
