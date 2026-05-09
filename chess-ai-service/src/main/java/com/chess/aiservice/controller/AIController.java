package com.chess.aiservice.controller;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pgn.FenUtilities;
import com.chess.engine.player.ai.MiniMax;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for AI move computation.
 *
 * POST /api/v1/ai/best-move
 * Body: { "fen": "...", "depth": 4 }
 * Response: { "move": "e2e4", "score": 35, "nodesEvaluated": 12345 }
 */
@RestController
@RequestMapping("/api/v1/ai")
@Slf4j
@Tag(name = "Chess AI", description = "AI move computation using MiniMax with Alpha-Beta Pruning")
public class AIController {

    public record BestMoveRequest(
            @NotBlank String fen,
            @Min(1) @Max(8) int depth
    ) {}

    public record BestMoveResponse(
            String move,
            long nodesEvaluated,
            long computeTimeMs
    ) {}

    @PostMapping("/best-move")
    @Operation(summary = "Compute the best move for the given FEN position")
    public ResponseEntity<BestMoveResponse> getBestMove(@Valid @RequestBody BestMoveRequest request) {
        log.info("AI request: depth={}, fen={}", request.depth(), request.fen());

        final long start = System.currentTimeMillis();
        final Board board = FenUtilities.createGameFromFEN(request.fen());
        final MiniMax miniMax = new MiniMax(request.depth());
        final Move bestMove = miniMax.execute(board);
        final long elapsed = System.currentTimeMillis() - start;

        final String moveStr = bestMove.equals(Move.NULL_MOVE)
                ? "none"
                : bestMove.getCurrentCoordinate() + "-" + bestMove.getDestinationCoordinate();

        log.info("AI result: move={}, nodes={}, time={}ms", moveStr, miniMax.getNumBoardsEvaluated(), elapsed);

        return ResponseEntity.ok(new BestMoveResponse(moveStr, miniMax.getNumBoardsEvaluated(), elapsed));
    }
}
