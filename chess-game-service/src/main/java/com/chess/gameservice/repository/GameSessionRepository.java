package com.chess.gameservice.repository;

import com.chess.gameservice.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    List<GameSession> findByWhitePlayerIdOrBlackPlayerId(String whitePlayerId, String blackPlayerId);

    List<GameSession> findByStatus(GameSession.GameStatus status);
}
