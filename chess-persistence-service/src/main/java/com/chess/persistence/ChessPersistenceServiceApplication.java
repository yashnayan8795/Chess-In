package com.chess.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Chess Persistence Service — stores and retrieves game records.
 * Default port: 8084
 */
@SpringBootApplication
public class ChessPersistenceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChessPersistenceServiceApplication.class, args);
    }
}
