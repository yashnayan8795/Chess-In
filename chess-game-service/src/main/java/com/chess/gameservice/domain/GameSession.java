package com.chess.gameservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity representing a chess game session.
 * Stores the FEN string as the authoritative board state.
 */
@Entity
@Table(name = "chess_games")
@Data
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String whitePlayerId;

    @Column(nullable = false)
    private String blackPlayerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.WAITING;

    /** FEN string representing the current board state */
    @Column(nullable = false, length = 100)
    private String currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /** Full PGN move history */
    @Column(columnDefinition = "TEXT")
    private String pgnMoves = "";

    private String winner;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    public enum GameStatus {
        WAITING,       // Waiting for second player
        IN_PROGRESS,   // Active game
        CHECKMATE,     // Game over by checkmate
        STALEMATE,     // Game over by stalemate
        DRAW,          // Agreed draw
        RESIGNED,      // One player resigned
        ABANDONED      // Timeout / disconnect
    }
}
