package com.chess.client;

import com.chess.client.gui.Table;

import javax.swing.*;

/**
 * Chess Desktop Client — Application Entry Point
 *
 * Launches the Swing GUI on the Event Dispatch Thread (EDT).
 * The Table class manages the main window, board rendering,
 * and user interaction.
 */
public final class ChessClient {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            Table.get().show();
        });
    }
}
