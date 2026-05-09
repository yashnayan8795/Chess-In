package com.chess.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Chess API Gateway — Single entry point for all external requests.
 * Listens on port 8080 and routes to downstream services.
 */
@SpringBootApplication
public class ChessApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChessApiGatewayApplication.class, args);
    }
}
