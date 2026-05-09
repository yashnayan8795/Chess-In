package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

/**
 * Represents a chess move.
 *
 * Uses the Sealed Class hierarchy (Java 17+) to cleanly model:
 *  - Normal moves
 *  - Attack moves (captures)
 *  - Pawn moves (including double-pawn push, en passant, promotion)
 *  - Castling (king-side and queen-side)
 *
 * Moves are VALUE OBJECTS — two moves are equal if they encode
 * the same piece moving from the same source to the same destination.
 */
public abstract sealed class Move
        permits Move.MajorMove,
                Move.AttackMove,
                Move.PawnMove,
                Move.PawnJump,
                Move.PawnPromotion,
                Move.CastleMove,
                Move.NullMove {

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    protected Move(Board board, Piece movedPiece, int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece != null && movedPiece.isFirstMove();
    }

    // ---- Accessors ----
    public int getCurrentCoordinate()     { return movedPiece.getPiecePosition(); }
    public int getDestinationCoordinate() { return destinationCoordinate; }
    public Piece getMovedPiece()          { return movedPiece; }
    public Board getBoard()               { return board; }
    public boolean isAttack()             { return false; }
    public boolean isCastlingMove()       { return false; }
    public Piece getAttackedPiece()       { return null; }

    /** Execute this move and return the new immutable Board. */
    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        // Place all current player's pieces except the moved piece
        board.currentPlayer().getActivePieces().stream()
             .filter(p -> !movedPiece.equals(p))
             .forEach(builder::setPiece);
        // Place opponent's pieces
        board.currentPlayer().getOpponent().getActivePieces()
             .forEach(builder::setPiece);
        // Place the moved piece at its new position
        builder.setPiece(movedPiece.movePiece(this));
        builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    // ---- Equality (value semantics) ----
    @Override
    public int hashCode() {
        return 31 * destinationCoordinate + (movedPiece != null ? movedPiece.hashCode() : 0);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Move move)) return false;
        return destinationCoordinate == move.destinationCoordinate
                && getCurrentCoordinate() == move.getCurrentCoordinate()
                && movedPiece.equals(move.movedPiece);
    }

    /** Quiet move — no capture. */
    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override public String toString() {
            return movedPiece.getPieceType().toString() +
                   BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    /** Base class for all captures. */
    public abstract static sealed class AttackMove extends Move
            permits MajorAttackMove, PawnAttackMove {
        protected final Piece attackedPiece;
        protected AttackMove(Board board, Piece movedPiece, int destinationCoordinate, Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }
        @Override public boolean isAttack()          { return true; }
        @Override public Piece getAttackedPiece()    { return attackedPiece; }
        @Override public int hashCode()              { return super.hashCode() * 31 + attackedPiece.hashCode(); }
        @Override public boolean equals(Object o)    { return o instanceof AttackMove a && super.equals(o) && attackedPiece.equals(a.attackedPiece); }
    }

    /** Major piece (not pawn) capture. */
    public static final class MajorAttackMove extends AttackMove {
        public MajorAttackMove(Board b, Piece mp, int dest, Piece ap) { super(b, mp, dest, ap); }
        @Override public String toString() {
            return movedPiece.getPieceType() + "x" + BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    // ---- Pawn Moves ----
    public static non-sealed class PawnMove extends Move {
        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override public String toString() { return BoardUtils.getPositionAtCoordinate(destinationCoordinate); }
    }

    public static sealed class PawnAttackMove extends AttackMove
            permits PawnEnPassantAttack {
        public PawnAttackMove(Board board, Piece movedPiece, int dest, Piece attackedPiece) {
            super(board, movedPiece, dest, attackedPiece);
        }
        @Override public String toString() {
            return BoardUtils.getPositionAtCoordinate(movedPiece.getPiecePosition()).charAt(0) +
                   "x" + BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttack extends PawnAttackMove {
        public PawnEnPassantAttack(Board board, Piece movedPiece, int dest, Piece attackedPiece) {
            super(board, movedPiece, dest, attackedPiece);
        }
        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            board.currentPlayer().getActivePieces().stream()
                 .filter(p -> !movedPiece.equals(p)).forEach(builder::setPiece);
            board.currentPlayer().getOpponent().getActivePieces().stream()
                 .filter(p -> !attackedPiece.equals(p)).forEach(builder::setPiece);
            builder.setPiece(movedPiece.movePiece(this));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            board.currentPlayer().getActivePieces().stream()
                 .filter(p -> !movedPiece.equals(p)).forEach(builder::setPiece);
            board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            final com.chess.engine.pieces.Pawn movedPawn = (com.chess.engine.pieces.Pawn) movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnPromotion extends Move {
        private final Move decoratedMove;
        private final com.chess.engine.pieces.Pawn promotedPawn;

        public PawnPromotion(Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (com.chess.engine.pieces.Pawn) decoratedMove.getMovedPiece();
        }
        @Override
        public Board execute() {
            final Board movedBoard = decoratedMove.execute();
            final Board.Builder builder = new Board.Builder();
            movedBoard.currentPlayer().getActivePieces().stream()
                      .filter(p -> !promotedPawn.equals(p)).forEach(builder::setPiece);
            movedBoard.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            // Default promotion to Queen
            builder.setPiece(promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(movedBoard.currentPlayer().getAlliance());
            return builder.build();
        }
        @Override public boolean isAttack() { return decoratedMove.isAttack(); }
        @Override public Piece getAttackedPiece() { return decoratedMove.getAttackedPiece(); }
        @Override public String toString() { return BoardUtils.getPositionAtCoordinate(destinationCoordinate) + "=Q"; }
    }

    // ---- Castling ----
    public abstract static sealed class CastleMove extends Move
            permits KingSideCastleMove, QueenSideCastleMove {
        protected final com.chess.engine.pieces.Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        protected CastleMove(Board board, Piece movedPiece, int dest,
                          com.chess.engine.pieces.Rook castleRook, int castleRookStart, int castleRookDest) {
            super(board, movedPiece, dest);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDest;
        }
        public com.chess.engine.pieces.Rook getCastleRook() { return castleRook; }
        @Override public boolean isCastlingMove() { return true; }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            board.currentPlayer().getActivePieces().stream()
                 .filter(p -> !movedPiece.equals(p) && !castleRook.equals(p))
                 .forEach(builder::setPiece);
            board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            builder.setPiece(movedPiece.movePiece(this));
            builder.setPiece(new com.chess.engine.pieces.Rook(castleRookDestination, castleRook.getPieceAlliance(), false));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(Board b, Piece mp, int dest,
                                  com.chess.engine.pieces.Rook rook, int rookStart, int rookDest) {
            super(b, mp, dest, rook, rookStart, rookDest);
        }
        @Override public String toString() { return "O-O"; }
    }

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(Board b, Piece mp, int dest,
                                   com.chess.engine.pieces.Rook rook, int rookStart, int rookDest) {
            super(b, mp, dest, rook, rookStart, rookDest);
        }
        @Override public String toString() { return "O-O-O"; }
    }

    // ---- Null Move (sentinel) ----
    public static final class NullMove extends Move {
        public NullMove() { super(null, null, -1); }
        @Override public Board execute() { throw new RuntimeException("Cannot execute the null move!"); }
        @Override public int getCurrentCoordinate() { return -1; }
    }

    // ====================================================================
    //  Factory
    // ====================================================================
    public static Move createMove(Board board, int currentCoordinate, int destinationCoordinate) {
        for (final Move move : board.getAllLegalMoves()) {
            if (move.getCurrentCoordinate() == currentCoordinate
                    && move.getDestinationCoordinate() == destinationCoordinate) {
                return move;
            }
        }
        return NULL_MOVE;
    }
}
