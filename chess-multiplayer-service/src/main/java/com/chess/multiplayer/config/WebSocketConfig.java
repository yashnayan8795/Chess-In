package com.chess.multiplayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * Spring STOMP-over-WebSocket configuration.
 *
 * Clients connect to: ws://host:8083/ws
 * - Send moves to:       /app/game/{id}/move
 * - Subscribe to:        /topic/game/{id}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable in-memory simple broker for /topic destinations
        registry.enableSimpleBroker("/topic", "/queue");
        // Client sends to /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS fallback for browsers that don't support WebSocket natively
    }
}
