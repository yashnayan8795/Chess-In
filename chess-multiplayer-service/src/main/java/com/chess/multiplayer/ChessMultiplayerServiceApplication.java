package com.chess.multiplayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Chess Multiplayer Service
 * WebSocket endpoint: ws://localhost:8083/ws
 * STOMP topics:
 *   /topic/game/{gameId}      - receive game state broadcasts
 *   /app/game/{gameId}/move   - send a move
 */
@SpringBootApplication
public class ChessMultiplayerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChessMultiplayerServiceApplication.class, args);
    }
}
