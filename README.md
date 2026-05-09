# Distributed Chess Platform

A production-grade, distributed Chess platform built with Java 21, Spring Boot 3.3, and a microservices architecture. This project features a custom-built chess engine, intelligent AI evaluation, real-time multiplayer support, and robust security through a centralized API Gateway.

## 🏗️ Architecture & Modules

The platform is divided into a parent POM and several dedicated modules to ensure clear separation of concerns:

- **`chess-engine`**: The core rules library. Handles board representation, legal move generation, check/checkmate detection, and move execution. Operates with zero external dependencies.
- **`chess-api-gateway` (Port 9090)**: The single entry point for all clients. Built with Spring Cloud Gateway and Spring Security Reactive, it handles routing, CORS, and **JWT-based authentication**.
- **`chess-game-service` (Port 8081)**: Manages active games, orchestrates engine validation, and tracks game state.
- **`chess-ai-service` (Port 8082)**: Provides intelligent move calculation using algorithms like Minimax with Alpha-Beta pruning.
- **`chess-multiplayer-service` (Port 8083)**: Handles real-time communication via WebSockets for player-vs-player matches.
- **`chess-persistence-service` (Port 8084)**: Dedicated service for storing game history, player stats, and managing authentication (login/token generation).
- **`chess-client`**: A Java Swing desktop UI for playing games locally.
- **`chess-tests`**: Comprehensive integration and unit test suite verifying the engine and services.

## 🛠️ Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.1, Spring Cloud Gateway
- **Security**: JWT (JSON Web Tokens), Spring Security Reactive
- **Data Persistence**: Spring Data JPA, MySQL (Prod), H2 (Local Dev)
- **Caching/Rate Limiting**: Redis
- **Real-time**: WebSockets
- **Build Tool**: Maven

## 🚀 Getting Started (Local Development)

The project is configured for a zero-friction local development experience. By default, services use the `dev` profile, which leverages an in-memory **H2 Database** and simple caching, meaning you don't need MySQL or Redis running locally.

### Prerequisites
- JDK 21+
- Apache Maven 3.9+

### Building the Project
From the root directory, build all modules and run the test suite:
```bash
mvn clean install
```

### Running the Services Locally
You can start the services independently in separate terminal windows:

```bash
# 1. API Gateway (Entry point, runs on 9090)
mvn spring-boot:run -pl chess-api-gateway

# 2. Persistence / Auth Service (runs on 8084)
mvn spring-boot:run -pl chess-persistence-service

# 3. Game Service (runs on 8081)
mvn spring-boot:run -pl chess-game-service

# 4. AI Service (runs on 8082)
mvn spring-boot:run -pl chess-ai-service

# 5. Multiplayer Service (runs on 8083)
mvn spring-boot:run -pl chess-multiplayer-service
```

*(Note: The services use the `dev` profile by default, exposing an H2 console at `/h2-console` on their respective ports).*

### Running via Docker (Production / Full Stack)
To run the platform with actual MySQL and Redis instances, use Docker Compose:
```bash
docker-compose up --build
```
This automatically configures the services to use the `prod` profile, connecting them to the database and cache containers.

## 🔐 Authentication & API Usage

All API endpoints (except authentication) are secured and require a valid JWT Bearer token.

**1. Obtain a Token**
Send a POST request to the API Gateway to log in:
```bash
curl -X POST http://localhost:9090/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "password"}'
```
*Response will contain the `token`.*

**2. Access Protected Routes**
Include the token in the `Authorization` header for subsequent requests:
```bash
curl -X GET http://localhost:9090/api/v1/games/{gameId} \
     -H "Authorization: Bearer <your_jwt_token_here>"
```
