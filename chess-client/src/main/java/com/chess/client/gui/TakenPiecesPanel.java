package com.chess.client.gui;

import com.chess.client.gui.Table.MoveLog;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Side panel showing captured pieces, sorted by value.
 * Displays white captures on top, black captures on bottom.
 */
public final class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel; // White's captured pieces
    private final JPanel southPanel; // Black's captured pieces

    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(120, 600);
    private static final Color PANEL_COLOR = new Color(245, 245, 220); // beige

    public TakenPiecesPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(TAKEN_PIECES_DIMENSION);
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createTitledBorder("Captured Pieces"));

        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        northPanel.setBackground(PANEL_COLOR);
        southPanel.setBackground(PANEL_COLOR);

        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void redo(MoveLog moveLog) {
        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece taken = move.getAttackedPiece();
                if (taken.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(taken);
                } else {
                    blackTakenPieces.add(taken);
                }
            }
        }

        // Sort by piece value descending
        whiteTakenPieces.sort((a, b) -> Integer.compare(b.getPieceValue(), a.getPieceValue()));
        blackTakenPieces.sort((a, b) -> Integer.compare(b.getPieceValue(), a.getPieceValue()));

        for (Piece p : whiteTakenPieces) {
            southPanel.add(new JLabel(p.toString() + " "));
        }
        for (Piece p : blackTakenPieces) {
            northPanel.add(new JLabel(p.toString() + " "));
        }

        validate();
        repaint();
    }
}
