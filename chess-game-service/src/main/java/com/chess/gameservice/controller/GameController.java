package com.chess.gameservice.controller;

import com.chess.gameservice.dto.GameDtos.*;
import com.chess.gameservice.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for chess game management.
 *
 * Base URL: /api/v1/games
 *
 * Endpoints:
 *   POST   /api/v1/games             - Create a new game
 *   POST   /api/v1/games/{id}/move   - Submit a move
 *   GET    /api/v1/games/{id}        - Get current game state
 *   POST   /api/v1/games/{id}/resign - Resign the game
 */
@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(name = "Chess Games", description = "Chess game lifecycle management")
public class GameController {

    private final GameService gameService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new chess game")
    public ResponseEntity<GameCreatedResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(request));
    }

    @PostMapping("/{gameId}/move")
    @Operation(summary = "Submit a move for the current player")
    public ResponseEntity<MoveResponse> makeMove(
            @PathVariable UUID gameId,
            @Valid @RequestBody MakeMoveRequest request) {
        final MoveResponse response = gameService.makeMove(request);
        return response.success()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get the current game state")
    public ResponseEntity<GameStateResponse> getGameState(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGameState(gameId));
    }

    @PostMapping("/{gameId}/resign")
    @Operation(summary = "Resign the current game")
    public ResponseEntity<Void> resign(
            @PathVariable UUID gameId,
            @Valid @RequestBody ResignRequest request) {
        gameService.resignGame(request);
        return ResponseEntity.noContent().build();
    }
}
