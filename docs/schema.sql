-- ============================================================
-- Chess Platform Database Schema
-- MySQL 8.0
-- ============================================================

-- Active games (managed by chess-game-service)
CREATE DATABASE IF NOT EXISTS chess_games;
USE chess_games;

CREATE TABLE IF NOT EXISTS chess_games (
    id              CHAR(36)        PRIMARY KEY,          -- UUID
    white_player_id VARCHAR(100)    NOT NULL,
    black_player_id VARCHAR(100)    NOT NULL,
    status          VARCHAR(30)     NOT NULL DEFAULT 'WAITING',
    current_fen     VARCHAR(100)    NOT NULL,
    pgn_moves       TEXT,
    winner          VARCHAR(100),
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    DATETIME,

    INDEX idx_status        (status),
    INDEX idx_white_player  (white_player_id),
    INDEX idx_black_player  (black_player_id)
);

-- ============================================================
-- Historical games (managed by chess-persistence-service)
CREATE DATABASE IF NOT EXISTS chess_history;
USE chess_history;

CREATE TABLE IF NOT EXISTS game_records (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    game_uuid       CHAR(36)        NOT NULL UNIQUE,
    white_player    VARCHAR(100)    NOT NULL,
    black_player    VARCHAR(100)    NOT NULL,
    result          VARCHAR(10)     NOT NULL,     -- "1-0", "0-1", "1/2-1/2"
    termination     VARCHAR(30)     NOT NULL,     -- CHECKMATE, STALEMATE, RESIGN, DRAW
    pgn             TEXT            NOT NULL,
    final_fen       VARCHAR(100),
    started_at      DATETIME        NOT NULL,
    ended_at        DATETIME,
    move_count      INT             NOT NULL DEFAULT 0,

    INDEX idx_white (white_player),
    INDEX idx_black (black_player),
    INDEX idx_result (result)
);

CREATE TABLE IF NOT EXISTS player_stats (
    player_id       VARCHAR(100)    PRIMARY KEY,
    display_name    VARCHAR(100)    NOT NULL,
    games_played    INT             NOT NULL DEFAULT 0,
    games_won       INT             NOT NULL DEFAULT 0,
    games_lost      INT             NOT NULL DEFAULT 0,
    games_drawn     INT             NOT NULL DEFAULT 0,
    elo_rating      INT             NOT NULL DEFAULT 1200,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_active     DATETIME,

    INDEX idx_elo (elo_rating DESC)
);
