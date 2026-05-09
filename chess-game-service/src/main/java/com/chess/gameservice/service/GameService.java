package com.chess.gameservice.service;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pgn.FenUtilities;
import com.chess.engine.pgn.PGNUtilities;
import com.chess.gameservice.domain.GameSession;
import com.chess.gameservice.domain.GameSession.GameStatus;
import com.chess.gameservice.dto.GameDtos.*;
import com.chess.gameservice.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core game orchestration service.
 * Bridges the chess-engine library with the persistence layer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GameService {

    private final GameSessionRepository gameSessionRepository;

    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    // ---- Create game ----
    public GameCreatedResponse createGame(CreateGameRequest request) {
        final GameSession session = new GameSession();
        session.setWhitePlayerId(request.whitePlayerId());
        session.setBlackPlayerId(request.blackPlayerId());
        session.setCurrentFen(START_FEN);
        session.setStatus(GameStatus.IN_PROGRESS);
        gameSessionRepository.save(session);
        log.info("Game created: {} (White: {}, Black: {})", session.getId(),
                request.whitePlayerId(), request.blackPlayerId());
        return new GameCreatedResponse(
                session.getId(), session.getWhitePlayerId(),
                session.getBlackPlayerId(), session.getCurrentFen(),
                session.getStatus().name());
    }

    // ---- Make a move ----
    @CacheEvict(value = "gameStates", key = "#request.gameId()")
    public MoveResponse makeMove(MakeMoveRequest request) {
        final GameSession session = gameSessionRepository.findById(request.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + request.gameId()));

        validateGameIsActive(session);
        validatePlayerTurn(session, request.playerId());

        final Board currentBoard = FenUtilities.createGameFromFEN(session.getCurrentFen());
        final Move move = Move.createMove(currentBoard, request.fromCoordinate(), request.toCoordinate());

        if (move.equals(Move.NULL_MOVE)) {
            return new MoveResponse(false, "Illegal move: no such move exists", null);
        }

        final MoveTransition transition = currentBoard.currentPlayer().makeMove(move);

        if (!transition.getMoveStatus().isDone()) {
            return new MoveResponse(false, "Illegal move: " + transition.getMoveStatus(), null);
        }

        final Board newBoard = transition.getToBoard();
        final String newFen  = FenUtilities.createFENFromGame(newBoard);
        session.setCurrentFen(newFen);
        session.setPgnMoves(session.getPgnMoves() + " " + move.toString());
        updateGameStatus(session, newBoard);
        gameSessionRepository.save(session);

        return new MoveResponse(true, "Move accepted",
                buildGameStateResponse(session.getId(), newBoard, session, move.toString()));
    }

    // ---- Get game state ----
    @Cacheable(value = "gameStates", key = "#gameId")
    @Transactional(readOnly = true)
    public GameStateResponse getGameState(UUID gameId) {
        final GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        final Board board = FenUtilities.createGameFromFEN(session.getCurrentFen());
        return buildGameStateResponse(gameId, board, session, "");
    }

    // ---- Resign ----
    public void resignGame(ResignRequest request) {
        final GameSession session = gameSessionRepository.findById(request.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + request.gameId()));
        session.setStatus(GameStatus.RESIGNED);
        final boolean isWhite = session.getWhitePlayerId().equals(request.playerId());
        session.setWinner(isWhite ? session.getBlackPlayerId() : session.getWhitePlayerId());
        session.setCompletedAt(LocalDateTime.now());
        gameSessionRepository.save(session);
        log.info("Game {} resigned by player {}", request.gameId(), request.playerId());
    }

    // ---- Helpers ----
    private void updateGameStatus(GameSession session, Board board) {
        if (board.currentPlayer().isInCheckMate()) {
            session.setStatus(GameStatus.CHECKMATE);
            session.setWinner(board.currentPlayer().getOpponent().getAlliance().toString());
            session.setCompletedAt(LocalDateTime.now());
        } else if (board.currentPlayer().isInStaleMate()) {
            session.setStatus(GameStatus.STALEMATE);
            session.setCompletedAt(LocalDateTime.now());
        }
    }

    private void validateGameIsActive(GameSession session) {
        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not active. Status: " + session.getStatus());
        }
    }

    private void validatePlayerTurn(GameSession session, String playerId) {
        final Board board = FenUtilities.createGameFromFEN(session.getCurrentFen());
        final boolean isWhiteTurn = board.currentPlayer().getAlliance().isWhite();
        final boolean isWhitePlayer = session.getWhitePlayerId().equals(playerId);
        if (isWhiteTurn != isWhitePlayer) {
            throw new IllegalStateException("It is not your turn.");
        }
    }

    private GameStateResponse buildGameStateResponse(UUID gameId, Board board,
                                                      GameSession session, String lastMove) {
        return new GameStateResponse(
                gameId,
                FenUtilities.createFENFromGame(board),
                board.currentPlayer().getAlliance().toString(),
                session.getStatus().name(),
                lastMove,
                board.currentPlayer().isInCheck(),
                board.currentPlayer().isInCheckMate(),
                board.currentPlayer().isInStaleMate()
        );
    }
}
