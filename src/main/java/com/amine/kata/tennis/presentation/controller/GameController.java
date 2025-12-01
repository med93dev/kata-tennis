package com.amine.kata.tennis.presentation.controller;

import com.amine.kata.tennis.application.port.in.GameUseCase;
import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import com.amine.kata.tennis.infrastructure.logging.StructuredLogger;
import com.amine.kata.tennis.presentation.dto.ErrorResponse;
import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/tennis")
@Tag(name = "Tennis Game", description = "API pour gérer les parties de tennis")
public class GameController {

    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String PLAY_ENDPOINT = "/api/v1/tennis/play";
    private static final String HISTORY_ENDPOINT = "/api/v1/tennis/history";
    private static final int HTTP_OK = 200;
    private static final int HTTP_ERROR = 500;

    private final GameUseCase gameUseCase;
    private final GamePort gamePort;
    private final StructuredLogger logger;

    public GameController(GameUseCase gameUseCase, GamePort gamePort) {
        this.gameUseCase = gameUseCase;
        this.gamePort = gamePort;
        this.logger = new StructuredLogger(GameController.class);
    }

    @PostMapping("/play")
    @Operation(summary = "Jouer une séquence de points",
            description = "Exécute une séquence de points de tennis et retourne l'historique complet du jeu. " +
                    "La séquence doit contenir uniquement les caractères 'A' (joueur A) et 'B' (joueur B). " +
                    "Le jeu est automatiquement réinitialisé après chaque séquence pour permettre une nouvelle partie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Séquence jouée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide - Séquence ou joueur invalide",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit - Jeu déjà terminé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<GameResponse>> play(@Valid @RequestBody GameRequest request) {
        logPlayRequest(request);

        try {
            List<GameResponse> response = gameUseCase.playSequence(request);
            gameUseCase.resetGame();

            logPlaySuccess(request, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.logApiError(POST_METHOD, PLAY_ENDPOINT, HTTP_ERROR, e.getMessage());
            resetGameOnError(e);
            throw e;
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Récupérer l'historique des parties terminées",
            description = "Retourne l'historique de toutes les parties de tennis terminées avec leurs messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<GameEntity>> getGameHistory() {
        logger.logApiRequest(GET_METHOD, HISTORY_ENDPOINT, Map.of());

        try {
            List<GameEntity> history = gamePort.findAll();
            logHistorySuccess(history);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.logApiError(GET_METHOD, HISTORY_ENDPOINT, HTTP_ERROR, e.getMessage());
            throw e;
        }
    }

    private void logPlayRequest(GameRequest request) {
        Map<String, Object> requestData = Map.of(
                "sequence", request.sequence(),
                "sequenceLength", request.sequence().length()
        );
        logger.logApiRequest(POST_METHOD, PLAY_ENDPOINT, requestData);
    }

    private void logPlaySuccess(GameRequest request, List<GameResponse> response) {
        if (response.isEmpty()) {
            return;
        }

        GameResponse lastResponse = response.get(response.size() - 1);
        Map<String, Object> responseData = Map.of(
                "gameSteps", response.size(),
                "finalScore", lastResponse.score(),
                "finalState", lastResponse.state(),
                "gameReset", true
        );

        logger.logApiResponse(POST_METHOD, PLAY_ENDPOINT, HTTP_OK, responseData);
        logger.logBusinessEvent("GAME_AUTO_RESET", "Jeu réinitialisé automatiquement après la partie",
                Map.of("sequence", request.sequence()));
    }

    private void logHistorySuccess(List<GameEntity> history) {
        Map<String, Object> responseData = Map.of(
                "totalGames", history.size(),
                "games", history.stream()
                        .map(game -> Map.of(
                                "id", game.getId(),
                                "sequence", game.getSequence(),
                                "winner", game.getWinner(),
                                "messageCount", game.getMessages().size()
                        ))
                        .toList()
        );

        logger.logApiResponse(GET_METHOD, HISTORY_ENDPOINT, HTTP_OK, responseData);
    }

    private void resetGameOnError(Exception error) {
        try {
            gameUseCase.resetGame();
            logger.logBusinessEvent("GAME_AUTO_RESET_ON_ERROR", "Jeu réinitialisé après une erreur",
                    Map.of("error", error.getMessage()));
        } catch (Exception resetException) {
            logger.logApiError(POST_METHOD, PLAY_ENDPOINT, HTTP_ERROR,
                    "Échec de la réinitialisation: " + resetException.getMessage());
        }
    }
}