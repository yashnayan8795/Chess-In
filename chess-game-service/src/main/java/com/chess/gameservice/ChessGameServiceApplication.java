package com.chess.gameservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Chess Game Service — Spring Boot Application Entry Point
 *
 * Responsibilities:
 *  - Manages active game sessions (create, make move, get state, resign, draw)
 *  - Uses the chess-engine library for move validation
 *  - Exposes REST API and WebSocket endpoints
 *  - Caches hot game states in Redis
 *
 * Default port: 8081
 */
@SpringBootApplication
@EnableCaching
public class ChessGameServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChessGameServiceApplication.class, args);
    }
}
