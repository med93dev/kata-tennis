package com.amine.kata.tennis.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amine.kata.tennis.application.port.in.GameUseCase;
import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.domain.exception.InvalidPlayerException;
import com.amine.kata.tennis.domain.exception.InvalidSequenceException;
import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@DisplayName("Tests pour GameController")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameUseCase gameUseCase;

    @MockBean
    private GamePort gamePort;

    @Autowired
    private ObjectMapper objectMapper;

    private GameRequest validRequest;
    private List<GameResponse> mockGameHistory;
    private GameResponse mockResetResponse;
    private List<GameEntity> mockGameEntities;

    @BeforeEach
    void setUp() {
        validRequest = new GameRequest("ABABAA");

        mockGameHistory = Arrays.asList(
                new GameResponse("Player A: 15 / Player B: 0", "Normal", "A scored a point!", Instant.now()),
                new GameResponse("Player A: 15 / Player B: 15", "Normal", "B scored a point!", Instant.now()),
                new GameResponse("Player A: 30 / Player B: 15", "Normal", "A scored a point!", Instant.now()),
                new GameResponse("Player A: 30 / Player B: 30", "Normal", "B scored a point!", Instant.now()),
                new GameResponse("Player A: 40 / Player B: 30", "Normal", "A scored a point!", Instant.now()),
                new GameResponse("A wins", "GameWon", "A wins!", Instant.now())
        );

        mockResetResponse = new GameResponse("0-0", "Normal", "Remet le score à zéro et réinitialise l'état du jeu.", Instant.now());

        GameEntity gameEntity = new GameEntity();
        gameEntity.setId(1L);
        gameEntity.setSequence("ABABAA");
        gameEntity.setWinner("A");
        gameEntity.setFinalScore("A wins");
        gameEntity.setCreatedAt(Instant.now());
        gameEntity.setMessages(Arrays.asList("A scored a point!", "B scored a point!", "A wins!"));

        mockGameEntities = Arrays.asList(gameEntity);
    }

    @Test
    @DisplayName("POST /api/v1/tennis/play - Devrait jouer une séquence valide avec succès")
    void shouldPlayValidSequenceSuccessfully() throws Exception {
        when(gameUseCase.playSequence(any(GameRequest.class))).thenReturn(mockGameHistory);

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].score").value("Player A: 15 / Player B: 0"))
                .andExpect(jsonPath("$[0].state").value("Normal"))
                .andExpect(jsonPath("$[0].message").value("A scored a point!"))
                .andExpect(jsonPath("$[5].score").value("A wins"))
                .andExpect(jsonPath("$[5].state").value("GameWon"))
                .andExpect(jsonPath("$[5].message").value("A wins!"));

        verify(gameUseCase, times(1)).playSequence(any(GameRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tennis/play - Devrait retourner 400 pour une séquence invalide")
    void shouldReturn400ForInvalidSequence() throws Exception {
        GameRequest invalidRequest = new GameRequest("ABC");
        when(gameUseCase.playSequence(any(GameRequest.class)))
                .thenThrow(new InvalidSequenceException("ABC"));

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Séquence invalide"))
                .andExpect(jsonPath("$.message").value("Séquence invalide: 'ABC'. La séquence ne peut contenir que les caractères 'A' et 'B'."));

        verify(gameUseCase, times(1)).playSequence(any(GameRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tennis/play - Devrait retourner 400 pour un joueur invalide")
    void shouldReturn400ForInvalidPlayer() throws Exception {

        GameRequest invalidRequest = new GameRequest("C");
        when(gameUseCase.playSequence(any(GameRequest.class)))
                .thenThrow(new InvalidPlayerException("C"));

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Joueur invalide"))
                .andExpect(jsonPath("$.message").value("Joueur invalide: 'C'. Seuls 'A' et 'B' sont autorisés."));

        verify(gameUseCase, times(1)).playSequence(any(GameRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tennis/play - Devrait retourner 400 pour une requête invalide")
    void shouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sequence\": null}"))
                .andExpect(status().isBadRequest());

        verify(gameUseCase, never()).playSequence(any(GameRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tennis/reset - Devrait réinitialiser le jeu avec succès")
    void shouldResetGameSuccessfully() throws Exception {
        when(gameUseCase.resetGame()).thenReturn(mockResetResponse);

        mockMvc.perform(post("/api/v1/tennis/reset"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.score").value("0-0"))
                .andExpect(jsonPath("$.state").value("Normal"))
                .andExpect(jsonPath("$.message").value("Remet le score à zéro et réinitialise l'état du jeu."));

        verify(gameUseCase, times(1)).resetGame();
    }


    @Test
    @DisplayName("GET /api/v1/tennis/history - Devrait récupérer l'historique avec succès")
    void shouldGetGameHistorySuccessfully() throws Exception {
        when(gamePort.findAll()).thenReturn(mockGameEntities);

        mockMvc.perform(get("/api/v1/tennis/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sequence").value("ABABAA"))
                .andExpect(jsonPath("$[0].winner").value("A"))
                .andExpect(jsonPath("$[0].finalScore").value("A wins"))
                .andExpect(jsonPath("$[0].messages").isArray())
                .andExpect(jsonPath("$[0].messages.length()").value(3))
                .andExpect(jsonPath("$[0].messages[0]").value("A scored a point!"))
                .andExpect(jsonPath("$[0].messages[1]").value("B scored a point!"))
                .andExpect(jsonPath("$[0].messages[2]").value("A wins!"));

        verify(gamePort, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tennis/history - Devrait retourner une liste vide quand aucun historique")
    void shouldReturnEmptyListWhenNoHistory() throws Exception {
        when(gamePort.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/tennis/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(gamePort, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/tennis/history - Devrait gérer les erreurs de récupération d'historique")
    void shouldHandleHistoryRetrievalErrors() throws Exception {
        when(gamePort.findAll()).thenThrow(new RuntimeException("Erreur de base de données"));

        mockMvc.perform(get("/api/v1/tennis/history"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Erreur interne du serveur"))
                .andExpect(jsonPath("$.message").value("Une erreur inattendue s'est produite"));

        verify(gamePort, times(1)).findAll();
    }

    @Test
    @DisplayName("POST /api/v1/tennis/play - Devrait gérer les erreurs internes du serveur")
    void shouldHandleInternalServerErrors() throws Exception {
        when(gameUseCase.playSequence(any(GameRequest.class)))
                .thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Erreur interne du serveur"))
                .andExpect(jsonPath("$.message").value("Une erreur inattendue s'est produite"));

        verify(gameUseCase, times(1)).playSequence(any(GameRequest.class));
    }

}
