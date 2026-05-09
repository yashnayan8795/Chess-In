package com.chess.client.gui;

import com.chess.client.gui.Table.MoveLog;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Side panel showing the move history in algebraic notation.
 * Columns: Move# | White | Black
 */
public final class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(150, 600);

    public GameHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(HISTORY_PANEL_DIMENSION);
        setBorder(BorderFactory.createTitledBorder("Move History"));

        this.model = new DataModel();
        final JTable table = new JTable(model);
        this.scrollPane    = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void redo(Board board, MoveLog moveLog) {
        model.clear();
        int currentRow = 0;
        for (int i = 0; i < moveLog.getMoves().size(); i++) {
            final Move move = moveLog.getMoves().get(i);
            if (i % 2 == 0) {
                model.setValueAt(move.toString(), currentRow, 1); // White column
            } else {
                model.setValueAt(move.toString(), currentRow, 2); // Black column
                currentRow++;
            }
        }
        if (board.currentPlayer().isInCheckMate()) {
            model.setValueAt("# (Checkmate)", model.getRowCount() - 1,
                    board.currentPlayer().getAlliance().isWhite() ? 1 : 2);
        }
        // Scroll to bottom
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    // ---- Table model ----
    private static final class DataModel extends DefaultTableModel {
        private static final String[] NAMES = {"#", "White", "Black"};

        DataModel() {
            super(NAMES, 0);
        }

        @Override
        public boolean isCellEditable(int row, int col) { return false; }

        void clear() {
            setRowCount(0);
            // Pre-populate empty rows
            for (int i = 0; i < 50; i++) addRow(new Object[]{i + 1, "", ""});
        }
    }
}
