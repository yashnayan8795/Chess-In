package com.chess.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Chess AI Service — Spring Boot Application Entry Point
 *
 * Exposes a REST endpoint:
 *   POST /api/v1/ai/best-move
 *   Body: { "fen": "...", "depth": 4 }
 *
 * Default port: 8082
 */
@SpringBootApplication
public class ChessAIServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChessAIServiceApplication.class, args);
    }
}
