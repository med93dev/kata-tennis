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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/tennis")
@Tag(name = "Tennis Game", description = "API pour gérer les parties de tennis")
public class GameController {

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
                           "La séquence doit contenir uniquement les caractères 'A' (joueur A) et 'B' (joueur B).")
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
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("sequence", request.sequence());
        requestData.put("sequenceLength", request.sequence().length());
        
        logger.logApiRequest("POST", "/api/v1/tennis/play", requestData);
        
        try {
            List<GameResponse> response = gameUseCase.playSequence(request);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("gameSteps", response.size());
            responseData.put("finalScore", response.isEmpty() ? "N/A" : response.get(response.size() - 1).score());
            responseData.put("finalState", response.isEmpty() ? "N/A" : response.get(response.size() - 1).state());
            
            logger.logApiResponse("POST", "/api/v1/tennis/play", 200, responseData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.logApiError("POST", "/api/v1/tennis/play", 500, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/reset")
    @Operation(summary = "Réinitialiser le jeu",
               description = "Remet le score à zéro et réinitialise l'état du jeu. Aucun paramètre requis.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jeu réinitialisé avec succès",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<GameResponse> resetGame() {
        logger.logApiRequest("POST", "/api/v1/tennis/reset", new HashMap<>());
        
        try {
            GameResponse response = gameUseCase.resetGame();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("score", response.score());
            responseData.put("state", response.state());
            responseData.put("message", response.message());
            
            logger.logApiResponse("POST", "/api/v1/tennis/reset", 200, responseData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.logApiError("POST", "/api/v1/tennis/reset", 500, e.getMessage());
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
        logger.logApiRequest("GET", "/api/v1/tennis/history", new HashMap<>());
        
        try {
            List<GameEntity> history = gamePort.findAll();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalGames", history.size());
            responseData.put("games", history.stream().map(game -> Map.of(
                "id", game.getId(),
                "sequence", game.getSequence(),
                "winner", game.getWinner(),
                "messageCount", game.getMessages().size()
            )).toList());
            
            logger.logApiResponse("GET", "/api/v1/tennis/history", 200, responseData);
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.logApiError("GET", "/api/v1/tennis/history", 500, e.getMessage());
            throw e;
        }
    }
}
