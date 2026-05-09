package com.chess.engine.board;

/**
 * Captures the result of attempting a move on the board.
 * Wraps both the new Board (if the move is legal) and the status of the transition.
 */
public final class MoveTransition {

    private final Board fromBoard;
    private final Board toBoard;
    private final Move transitionMove;
    private final MoveStatus moveStatus;

    public MoveTransition(Board fromBoard, Board toBoard, Move transitionMove, MoveStatus moveStatus) {
        this.fromBoard     = fromBoard;
        this.toBoard       = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus    = moveStatus;
    }

    public Board getFromBoard()       { return fromBoard; }
    public Board getToBoard()         { return toBoard; }
    public Move getTransitionMove()   { return transitionMove; }
    public MoveStatus getMoveStatus() { return moveStatus; }
    public boolean isDone()           { return moveStatus == MoveStatus.DONE; }

    /**
     * Status codes for a move attempt.
     */
    public enum MoveStatus {
        /** The move was legal and executed successfully. */
        DONE,
        /** The move is not in the list of legal moves. */
        ILLEGAL_MOVE,
        /** The move would leave the moving player's king in check. */
        LEAVES_PLAYER_IN_CHECK;

        public boolean isDone() { return this == DONE; }
    }
}
