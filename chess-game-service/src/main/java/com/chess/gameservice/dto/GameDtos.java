package com.chess.gameservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTOs for the Chess Game Service REST API.
 * Using Java Records for immutable, concise data transfer objects.
 */
public final class GameDtos {

    private GameDtos() {}

    // ---- Request DTOs ----

    public record CreateGameRequest(
            @NotBlank String whitePlayerId,
            @NotBlank String blackPlayerId
    ) {}

    public record MakeMoveRequest(
            @NotNull UUID gameId,
            @NotBlank String playerId,
            int fromCoordinate,
            int toCoordinate
    ) {}

    public record ResignRequest(
            @NotNull UUID gameId,
            @NotBlank String playerId
    ) {}

    // ---- Response DTOs ----

    public record GameStateResponse(
            UUID gameId,
            String fen,
            String currentPlayer,
            String status,
            String lastMove,
            boolean isCheck,
            boolean isCheckmate,
            boolean isStalemate
    ) {}

    public record MoveResponse(
            boolean success,
            String message,
            GameStateResponse gameState
    ) {}

    public record GameCreatedResponse(
            UUID gameId,
            String whitePlayerId,
            String blackPlayerId,
            String initialFen,
            String status
    ) {}
}
