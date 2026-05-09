package com.chess.engine;

import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

/**
 * Enumeration for the two sides in chess.
 * Provides direction (pawn movement direction) and helper factory methods.
 */
public enum Alliance {

    WHITE {
        @Override public int getDirection()       { return -1; }   // moves toward rank 8 (lower index)
        @Override public int getOppositeDirection(){ return 1; }
        @Override public boolean isBlack()        { return false; }
        @Override public boolean isWhite()        { return true; }
        @Override public boolean isPawnPromotionSquare(int position) { return position >= 0 && position <= 7; }
        @Override public Player choosePlayer(WhitePlayer w, BlackPlayer b) { return w; }
        @Override public String toString()        { return "White"; }
    },

    BLACK {
        @Override public int getDirection()       { return 1; }    // moves toward rank 1 (higher index)
        @Override public int getOppositeDirection(){ return -1; }
        @Override public boolean isBlack()        { return true; }
        @Override public boolean isWhite()        { return false; }
        @Override public boolean isPawnPromotionSquare(int position) { return position >= 56 && position <= 63; }
        @Override public Player choosePlayer(WhitePlayer w, BlackPlayer b) { return b; }
        @Override public String toString()        { return "Black"; }
    };

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();
    public abstract boolean isPawnPromotionSquare(int position);
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
