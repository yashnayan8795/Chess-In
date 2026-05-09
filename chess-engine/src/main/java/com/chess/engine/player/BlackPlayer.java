package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Black player — manages black pieces and black castling rights.
 *
 * Black castles:
 *  - King-side:  e8(4) → g8(6),  Rook h8(7) → f8(5)
 *  - Queen-side: e8(4) → c8(2),  Rook a8(0) → d8(3)
 */
public final class BlackPlayer extends Player {

    public BlackPlayer(Board board,
                       Collection<Move> blackStandardLegalMoves,
                       Collection<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override public Collection<Piece> getActivePieces() { return board.getBlackPieces(); }
    @Override public Alliance getAlliance()               { return Alliance.BLACK; }
    @Override public Player getOpponent()                { return board.whitePlayer(); }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                     Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            // ---- King-side castle ----
            if (!board.getTile(5).isOccupied() && !board.getTile(6).isOccupied()) {
                final Tile rookTile = board.getTile(7);
                if (rookTile.isOccupied() && rookTile.getPiece().orElseThrow().isFirstMove()) {
                    if (calculateAttacksOnTile(5, opponentLegals).isEmpty()
                            && calculateAttacksOnTile(6, opponentLegals).isEmpty()
                            && rookTile.getPiece().orElseThrow().getPieceType() == Piece.PieceType.ROOK) {
                        kingCastles.add(new KingSideCastleMove(
                                board, playerKing, 6,
                                (Rook) rookTile.getPiece().orElseThrow(), 7, 5));
                    }
                }
            }

            // ---- Queen-side castle ----
            if (!board.getTile(1).isOccupied() && !board.getTile(2).isOccupied()
                    && !board.getTile(3).isOccupied()) {
                final Tile rookTile = board.getTile(0);
                if (rookTile.isOccupied() && rookTile.getPiece().orElseThrow().isFirstMove()) {
                    if (calculateAttacksOnTile(2, opponentLegals).isEmpty()
                            && calculateAttacksOnTile(3, opponentLegals).isEmpty()
                            && rookTile.getPiece().orElseThrow().getPieceType() == Piece.PieceType.ROOK) {
                        kingCastles.add(new QueenSideCastleMove(
                                board, playerKing, 2,
                                (Rook) rookTile.getPiece().orElseThrow(), 0, 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    @Override public String toString() { return "Black"; }
}
