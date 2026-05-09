package com.chess.engine.board;

/**
 * Utility constants and static helpers for the 8x8 board.
 * All coordinate logic, file/rank mappings, and column masks live here.
 */
public final class BoardUtils {

    // ---- Board Dimensions ----
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    // ---- File (column) boolean masks ----
    public static final boolean[] FIRST_FILE  = initFile(0);
    public static final boolean[] SECOND_FILE = initFile(1);
    public static final boolean[] SEVENTH_FILE = initFile(6);
    public static final boolean[] EIGHTH_FILE = initFile(7);

    // ---- Rank (row) boolean masks ----
    public static final boolean[] SECOND_RANK = initRank(48);
    public static final boolean[] SEVENTH_RANK = initRank(8);

    // Algebraic notation lookup (a8=0 … h1=63)
    private static final String[] ALGEBRAIC_NOTATION = initAlgebraicNotation();
    private static final java.util.Map<String, Integer> POSITION_TO_COORDINATE =
            initPositionToCoordinate();

    private BoardUtils() { throw new AssertionError("Utility class — not instantiable"); }

    // ---- Coordinate helpers ----

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    public static int getCoordinateAtPosition(String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(int coordinate) {
        return ALGEBRAIC_NOTATION[coordinate];
    }

    public static int getRow(int coordinate) {
        return coordinate / NUM_TILES_PER_ROW;
    }

    public static int getColumn(int coordinate) {
        return coordinate % NUM_TILES_PER_ROW;
    }

    // ---- Init helpers ----

    private static boolean[] initFile(int fileNumber) {
        final boolean[] file = new boolean[NUM_TILES];
        for (int i = fileNumber; i < NUM_TILES; i += NUM_TILES_PER_ROW) {
            file[i] = true;
        }
        return file;
    }

    private static boolean[] initRank(int rankNumber) {
        final boolean[] rank = new boolean[NUM_TILES];
        for (int i = rankNumber; i < rankNumber + NUM_TILES_PER_ROW; i++) {
            rank[i] = true;
        }
        return rank;
    }

    private static String[] initAlgebraicNotation() {
        return new String[]{
            "a8","b8","c8","d8","e8","f8","g8","h8",
            "a7","b7","c7","d7","e7","f7","g7","h7",
            "a6","b6","c6","d6","e6","f6","g6","h6",
            "a5","b5","c5","d5","e5","f5","g5","h5",
            "a4","b4","c4","d4","e4","f4","g4","h4",
            "a3","b3","c3","d3","e3","f3","g3","h3",
            "a2","b2","c2","d2","e2","f2","g2","h2",
            "a1","b1","c1","d1","e1","f1","g1","h1"
        };
    }

    private static java.util.Map<String, Integer> initPositionToCoordinate() {
        final java.util.Map<String, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < NUM_TILES; i++) {
            map.put(ALGEBRAIC_NOTATION[i], i);
        }
        return java.util.Collections.unmodifiableMap(map);
    }
}
