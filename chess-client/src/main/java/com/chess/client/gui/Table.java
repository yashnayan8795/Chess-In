package com.chess.client.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Main chess application window — Singleton.
 *
 * Layout:
 *   WEST  → TakenPiecesPanel
 *   CENTER → BoardPanel (8x8 grid of TilePanels)
 *   EAST  → GameHistoryPanel
 *   SOUTH → status bar
 *
 * Click logic:
 *   1st click → select source tile (highlight piece)
 *   2nd click → select destination tile (execute move or deselect)
 */
public final class Table {

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(900, 700);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600, 600);
    private static final Dimension TILE_PANEL_DIMENSION  = new Dimension(75, 75);
    private static final Color LIGHT_TILE_COLOR = new Color(240, 217, 181);
    private static final Color DARK_TILE_COLOR  = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR  = new Color(205, 210, 106, 180);

    private static final String PIECE_ICON_PATH = "chess-client/src/main/resources/art/";

    private static final Table INSTANCE = new Table();

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;

    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private Table() {
        this.chessBoard       = Board.createStandardBoard();
        this.moveLog          = new MoveLog();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel       = new BoardPanel();

        this.gameFrame = new JFrame("Chess Platform");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setJMenuBar(createMenuBar());

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel,       BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
    }

    public static Table get() { return INSTANCE; }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            gameFrame.setVisible(true);
            boardPanel.drawBoard(chessBoard);
        });
    }

    // ---- Menu ----
    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createOptionsMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> {
            chessBoard = Board.createStandardBoard();
            moveLog.clear();
            boardPanel.drawBoard(chessBoard);
        });
        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newGame);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(e -> boardPanel.flip());
        optionsMenu.add(flipBoard);
        return optionsMenu;
    }

    // ====================================================================
    //  BoardPanel — 8×8 grid of TilePanels
    // ====================================================================
    class BoardPanel extends JPanel {

        private final List<TilePanel> boardTiles = new ArrayList<>();
        private boolean flipped = false;

        BoardPanel() {
            super(new GridLayout(8, 8));
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            for (int i = 0; i < 64; i++) {
                final TilePanel tp = new TilePanel(this, i);
                boardTiles.add(tp);
                add(tp);
            }
            validate();
        }

        void drawBoard(Board board) {
            removeAll();
            for (int i = 0; i < 64; i++) {
                final int tileId = flipped ? (63 - i) : i;
                boardTiles.get(tileId).drawTile(board);
                add(boardTiles.get(tileId));
            }
            validate();
            repaint();
        }

        void flip() {
            flipped = !flipped;
            drawBoard(chessBoard);
        }
    }

    // ====================================================================
    //  TilePanel — single square on the board
    // ====================================================================
    class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(BoardPanel boardPanel, int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Right click → deselect
                    if (SwingUtilities.isRightMouseButton(e)) {
                        sourceTile        = null;
                        destinationTile   = null;
                        humanMovedPiece   = null;
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // First click: select piece
                        if (sourceTile == null) {
                            sourceTile      = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece().orElse(null);
                            if (humanMovedPiece == null) sourceTile = null;
                        } else {
                            // Second click: attempt move
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.createMove(chessBoard,
                                    sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile      = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        gameHistoryPanel.redo(chessBoard, moveLog);
                        takenPiecesPanel.redo(moveLog);
                        boardPanel.drawBoard(chessBoard);
                    });
                }
            });
            validate();
        }

        void drawTile(Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        private void assignTileColor() {
            final boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
        }

        private void assignTilePieceIcon(Board board) {
            removeAll();
            if (board.getTile(tileId).isOccupied()) {
                final Piece piece = board.getTile(tileId).getPiece().orElseThrow();
                final String colorPrefix  = piece.getPieceAlliance().isWhite() ? "W" : "B";
                final String pieceSymbol  = piece.toString();
                final String iconPath     = PIECE_ICON_PATH + colorPrefix + pieceSymbol + ".gif";
                try {
                    final BufferedImage image = ImageIO.read(new File(iconPath));
                    add(new JLabel(new ImageIcon(image)));
                } catch (Exception e) {
                    // Fallback: show piece letter if icon not found
                    final JLabel lbl = new JLabel(colorPrefix + pieceSymbol);
                    lbl.setFont(new Font("Arial", Font.BOLD, 20));
                    lbl.setForeground(piece.getPieceAlliance().isWhite() ? Color.WHITE : Color.BLACK);
                    add(lbl);
                }
            }
        }

        private void highlightLegalMoves(Board board) {
            if (sourceTile != null && humanMovedPiece != null
                    && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                for (final Move move : humanMovedPiece.calculateLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == tileId) {
                        // Semi-transparent overlay to highlight legal moves
                        setBorder(BorderFactory.createLineBorder(new Color(20, 200, 20), 3));
                    }
                }
            } else {
                setBorder(null);
            }
        }
    }

    // ====================================================================
    //  MoveLog
    // ====================================================================
    public static final class MoveLog {
        private final List<Move> moves = new ArrayList<>();

        public void addMove(Move move) { moves.add(move); }
        public List<Move> getMoves()   { return moves; }
        public void clear()            { moves.clear(); }
        public int size()              { return moves.size(); }
        public Move removeMove(int i)  { return moves.remove(i); }
    }
}
