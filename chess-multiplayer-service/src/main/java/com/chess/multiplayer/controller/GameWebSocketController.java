package com.chess.multiplayer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WebSocket message handler for live chess moves.
 *
 * Client sends:    /app/game/{gameId}/move   → MoveMessage
 * Server replies:  /topic/game/{gameId}      → GameStateMessage
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // ---- Message types (Records) ----

    public record MoveMessage(
            String playerId,
            int fromCoordinate,
            int toCoordinate
    ) {}

    public record GameStateMessage(
            String gameId,
            String fen,
            String lastMove,
            String currentPlayer,
            String status,
            boolean isCheck,
            boolean isCheckmate,
            LocalDateTime timestamp
    ) {}

    public record ChatMessage(
            String playerId,
            String text,
            LocalDateTime timestamp
    ) {}

    // ---- Move handler ----
    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}")
    public GameStateMessage handleMove(
            @DestinationVariable String gameId,
            MoveMessage moveMessage) {

        log.info("Move received in game {}: {} → {}",
                gameId, moveMessage.fromCoordinate(), moveMessage.toCoordinate());

        // In a real implementation: delegate to GameService via REST or shared cache
        // Here we return a placeholder acknowledgement
        return new GameStateMessage(
                gameId,
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", // would be updated FEN
                moveMessage.fromCoordinate() + "-" + moveMessage.toCoordinate(),
                "BLACK",
                "IN_PROGRESS",
                false,
                false,
                LocalDateTime.now()
        );
    }

    // ---- Chat handler ----
    @MessageMapping("/game/{gameId}/chat")
    @SendTo("/topic/game/{gameId}/chat")
    public ChatMessage handleChat(
            @DestinationVariable String gameId,
            ChatMessage chatMessage) {
        log.info("Chat in game {}: {}", gameId, chatMessage.text());
        return new ChatMessage(chatMessage.playerId(), chatMessage.text(), LocalDateTime.now());
    }
}
