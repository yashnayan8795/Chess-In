package com.chess.engine.pgn;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.*;

/**
 * FEN (Forsyth-Edwards Notation) parser and generator.
 *
 * FEN string format:
 *   <piece placement> <active color> <castling> <en passant> <halfmove> <fullmove>
 *
 * Example starting position:
 *   rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
 */
public final class FenUtilities {

    private FenUtilities() { throw new AssertionError(); }

    // ---- FEN → Board ----
    public static Board createGameFromFEN(String fenString) {
        final String[] fenPartitions = fenString.trim().split(" ");
        final Builder builder = new Builder();
        parsePiecePlacement(builder, fenPartitions[0]);
        builder.setMoveMaker(fenPartitions[1].equals("w") ? Alliance.WHITE : Alliance.BLACK);
        return builder.build();
    }

    private static void parsePiecePlacement(Builder builder, String fenBoard) {
        int coordinate = 0;
        for (char symbol : fenBoard.toCharArray()) {
            if (symbol == '/') continue;
            if (Character.isDigit(symbol)) {
                coordinate += Character.getNumericValue(symbol);
            } else {
                builder.setPiece(fenToPiece(symbol, coordinate));
                coordinate++;
            }
        }
    }

    private static Piece fenToPiece(char symbol, int coordinate) {
        final Alliance alliance = Character.isUpperCase(symbol) ? Alliance.WHITE : Alliance.BLACK;
        return switch (Character.toUpperCase(symbol)) {
            case 'P' -> new Pawn(coordinate, alliance);
            case 'N' -> new Knight(coordinate, alliance);
            case 'B' -> new Bishop(coordinate, alliance);
            case 'R' -> new Rook(coordinate, alliance);
            case 'Q' -> new Queen(coordinate, alliance);
            case 'K' -> new King(coordinate, alliance, false, false);
            default  -> throw new IllegalArgumentException("Unknown FEN piece: " + symbol);
        };
    }

    // ---- Board → FEN ----
    public static String createFENFromGame(Board board) {
        return calculateBoardText(board)
                + " " + calculateCurrentPlayerText(board)
                + " " + calculateCastleText(board)
                + " " + calculateEnPassantSquare(board)
                + " 0 1"; // halfmove and fullmove clocks (simplified)
    }

    private static String calculateBoardText(Board board) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            final String tileText = board.getTile(i).toString();
            sb.append(tileText.equals("-") ? "1" : tileText);
            if ((i + 1) % 8 == 0 && i != 63) sb.append("/");
        }
        // Compress consecutive "1"s into digits
        return sb.toString()
                 .replaceAll("11111111", "8")
                 .replaceAll("1111111", "7")
                 .replaceAll("111111", "6")
                 .replaceAll("11111", "5")
                 .replaceAll("1111", "4")
                 .replaceAll("111", "3")
                 .replaceAll("11", "2");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.currentPlayer().getAlliance().isWhite() ? "w" : "b";
    }

    private static String calculateCastleText(Board board) {
        final StringBuilder sb = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable())  sb.append("K");
        if (board.whitePlayer().isQueenSideCastleCapable()) sb.append("Q");
        if (board.blackPlayer().isKingSideCastleCapable())  sb.append("k");
        if (board.blackPlayer().isQueenSideCastleCapable()) sb.append("q");
        return sb.isEmpty() ? "-" : sb.toString();
    }

    private static String calculateEnPassantSquare(Board board) {
        return board.getEnPassantPawn() != null
                ? com.chess.engine.board.BoardUtils.getPositionAtCoordinate(board.getEnPassantPawn().getPiecePosition())
                : "-";
    }
}
