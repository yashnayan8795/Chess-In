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
 * White player — manages white pieces and white castling rights.
 *
 * White castles:
 *  - King-side:  e1(60) → g1(62), Rook h1(63) → f1(61)
 *  - Queen-side: e1(60) → c1(58), Rook a1(56) → d1(59)
 */
public final class WhitePlayer extends Player {

    public WhitePlayer(Board board,
                       Collection<Move> whiteStandardLegalMoves,
                       Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override public Collection<Piece> getActivePieces() { return board.getWhitePieces(); }
    @Override public Alliance getAlliance()               { return Alliance.WHITE; }
    @Override public Player getOpponent()                { return board.blackPlayer(); }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                     Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            // ---- King-side castle ----
            if (!board.getTile(61).isOccupied() && !board.getTile(62).isOccupied()) {
                final Tile rookTile = board.getTile(63);
                if (rookTile.isOccupied() && rookTile.getPiece().orElseThrow().isFirstMove()) {
                    if (calculateAttacksOnTile(61, opponentLegals).isEmpty()
                            && calculateAttacksOnTile(62, opponentLegals).isEmpty()
                            && rookTile.getPiece().orElseThrow().getPieceType() == Piece.PieceType.ROOK) {
                        kingCastles.add(new KingSideCastleMove(
                                board, playerKing, 62,
                                (Rook) rookTile.getPiece().orElseThrow(), 63, 61));
                    }
                }
            }

            // ---- Queen-side castle ----
            if (!board.getTile(59).isOccupied() && !board.getTile(58).isOccupied()
                    && !board.getTile(57).isOccupied()) {
                final Tile rookTile = board.getTile(56);
                if (rookTile.isOccupied() && rookTile.getPiece().orElseThrow().isFirstMove()) {
                    if (calculateAttacksOnTile(58, opponentLegals).isEmpty()
                            && calculateAttacksOnTile(59, opponentLegals).isEmpty()
                            && rookTile.getPiece().orElseThrow().getPieceType() == Piece.PieceType.ROOK) {
                        kingCastles.add(new QueenSideCastleMove(
                                board, playerKing, 58,
                                (Rook) rookTile.getPiece().orElseThrow(), 56, 59));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    @Override public String toString() { return "White"; }
}
