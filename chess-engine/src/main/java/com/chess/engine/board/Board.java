package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Immutable representation of the chess board.
 *
 * Internally stores 64 Tiles. Created via the Builder pattern to ensure
 * all boards are fully constructed before being observed.
 *
 * Key invariants:
 *  - Exactly one white king and one black king must be present.
 *  - All legal move lists are pre-computed at construction time.
 */
public final class Board {

    // The 64 tiles
    private final List<Tile> gameBoard;

    // Cached legal moves for both sides (computed once in constructor)
    private final Collection<Move> whiteStandardLegalMoves;
    private final Collection<Move> blackStandardLegalMoves;

    // Players
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    // En-passant pawn (if any, set when a pawn double-advances)
    private final Pawn enPassantPawn;

    private Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Piece> whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        final Collection<Piece> blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        this.whiteStandardLegalMoves = calculateLegalMoves(whitePieces);
        this.blackStandardLegalMoves = calculateLegalMoves(blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    // ---- Public API ----

    public Tile getTile(int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    public Collection<Piece> getWhitePieces() {
        return calculateActivePieces(this.gameBoard, Alliance.WHITE);
    }

    public Collection<Piece> getBlackPieces() {
        return calculateActivePieces(this.gameBoard, Alliance.BLACK);
    }

    public Player whitePlayer() { return whitePlayer; }
    public Player blackPlayer() { return blackPlayer; }
    public Player currentPlayer() { return currentPlayer; }
    public Pawn getEnPassantPawn() { return enPassantPawn; }

    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(
                whiteStandardLegalMoves.stream(),
                blackStandardLegalMoves.stream()
        ).collect(Collectors.toUnmodifiableList());
    }

    public Collection<Move> getWhiteStandardLegalMoves() { return whiteStandardLegalMoves; }
    public Collection<Move> getBlackStandardLegalMoves() { return blackStandardLegalMoves; }

    // ---- Internal helpers ----

    private static List<Tile> createGameBoard(Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return Collections.unmodifiableList(Arrays.asList(tiles));
    }

    private static Collection<Piece> calculateActivePieces(List<Tile> board, Alliance alliance) {
        return board.stream()
                .filter(Tile::isOccupied)
                .map(tile -> tile.getPiece().orElseThrow())
                .filter(piece -> piece.getPieceAlliance() == alliance)
                .collect(Collectors.toUnmodifiableList());
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        return pieces.stream()
                .flatMap(piece -> piece.calculateLegalMoves(this).stream())
                .collect(Collectors.toUnmodifiableList());
    }

    // ---- Standard starting position ----
    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // Black pieces
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK, true, true));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        for (int i = 8; i < 16; i++) builder.setPiece(new Pawn(i, Alliance.BLACK));
        // White pieces
        for (int i = 48; i < 56; i++) builder.setPiece(new Pawn(i, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE, true, true));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = gameBoard.get(i).toString();
            sb.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) sb.append("\n");
        }
        return sb.toString();
    }

    // ====================================================================
    // Builder
    // ====================================================================
    public static final class Builder {

        Map<Integer, Piece> boardConfig = new HashMap<>();
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder setPiece(Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Builder setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

        public Board build() {
            return new Board(this);
        }
    }
}
