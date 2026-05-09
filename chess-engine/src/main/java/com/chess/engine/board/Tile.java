package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import java.util.Optional;

/**
 * Represents a single tile on the 8×8 chess board.
 * A tile is either empty (EmptyTile) or occupied (OccupiedTile).
 *
 * Design: sealed hierarchy with factory method — callers always get a Tile,
 * never construct one directly.
 */
public abstract sealed class Tile permits Tile.EmptyTile, Tile.OccupiedTile {

    protected final int tileCoordinate;

    private Tile(int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    // ---- Factory ----
    public static Tile createTile(int coordinate, Piece piece) {
        return piece != null
                ? new OccupiedTile(coordinate, piece)
                : new EmptyTile(coordinate);
    }

    // ---- Abstract API ----
    public abstract boolean isOccupied();
    public abstract Optional<Piece> getPiece();
    public int getTileCoordinate() { return tileCoordinate; }

    // -----------------------------------------------------------------------
    // EmptyTile
    // -----------------------------------------------------------------------
    public static final class EmptyTile extends Tile {

        EmptyTile(int coordinate) {
            super(coordinate);
        }

        @Override public boolean isOccupied() { return false; }
        @Override public Optional<Piece> getPiece() { return Optional.empty(); }

        @Override
        public String toString() { return "-"; }
    }

    // -----------------------------------------------------------------------
    // OccupiedTile
    // -----------------------------------------------------------------------
    public static final class OccupiedTile extends Tile {

        private final Piece pieceOnTile;

        OccupiedTile(int coordinate, Piece pieceOnTile) {
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override public boolean isOccupied() { return true; }
        @Override public Optional<Piece> getPiece() { return Optional.of(pieceOnTile); }

        @Override
        public String toString() {
            return pieceOnTile.getPieceAlliance().isBlack()
                    ? pieceOnTile.toString().toLowerCase()
                    : pieceOnTile.toString().toUpperCase();
        }
    }
}
