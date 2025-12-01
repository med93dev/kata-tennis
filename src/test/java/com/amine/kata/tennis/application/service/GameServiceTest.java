package com.amine.kata.tennis.application.service;

import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.domain.exception.InvalidSequenceException;
import com.amine.kata.tennis.domain.service.GameService;
import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests pour GameService")
class GameServiceTest {

    @Mock
    private GamePort gamePort;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(gamePort);
    }

    @Test
    @DisplayName("Devrait jouer une séquence simple et retourner l'historique")
    void shouldPlaySimpleSequenceAndReturnHistory() {
        GameRequest request = new GameRequest("AB");
        List<GameResponse> result = gameService.playSequence(request);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Player A: 15 / Player B: 0", result.get(0).score());
        assertEquals("Player A: 15 / Player B: 15", result.get(1).score());
        assertEquals("Normal", result.get(0).state());
        assertEquals("Normal", result.get(1).state());
    }

    @Test
    @DisplayName("Devrait gérer une séquence avec deuce")
    void shouldHandleSequenceWithDeuce() {
        GameRequest request = new GameRequest("ABABABAB");
        List<GameResponse> result = gameService.playSequence(request);
        assertNotNull(result);
        assertTrue(result.size() >= 4);
        boolean hasDeuce = result.stream()
                .anyMatch(response -> "Deuce".equals(response.state()));
        assertTrue(hasDeuce, "La séquence devrait contenir un état Deuce");
    }

    @Test
    @DisplayName("Devrait lever InvalidSequenceException pour une séquence invalide")
    void shouldThrowInvalidSequenceExceptionForInvalidSequence() {
        GameRequest request = new GameRequest("ABC");
        InvalidSequenceException exception = assertThrows(
                InvalidSequenceException.class,
                () -> gameService.playSequence(request)
        );

        assertTrue(exception.getMessage().contains("ABC"));
        verify(gamePort, never()).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("Devrait lever InvalidSequenceException pour une séquence vide")
    void shouldThrowInvalidSequenceExceptionForEmptySequence() {
        GameRequest request = new GameRequest("");
        InvalidSequenceException exception = assertThrows(
                InvalidSequenceException.class,
                () -> gameService.playSequence(request)
        );
        assertTrue(exception.getMessage().contains(""));
        verify(gamePort, never()).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("Devrait lever InvalidSequenceException pour une séquence avec des caractères invalides")
    void shouldThrowInvalidSequenceExceptionForInvalidCharacters() {
        GameRequest request = new GameRequest("A1B2C3");
        InvalidSequenceException exception = assertThrows(
                InvalidSequenceException.class,
                () -> gameService.playSequence(request)
        );

        assertTrue(exception.getMessage().contains("A1B2C3"));
        verify(gamePort, never()).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("Devrait réinitialiser le jeu correctement")
    void shouldResetGameCorrectly() {
        GameRequest request = new GameRequest("AB");
        gameService.playSequence(request);
        GameResponse resetResponse = gameService.resetGame();
        assertNotNull(resetResponse);
        assertEquals("0-0", resetResponse.score());
        assertEquals("Normal", resetResponse.state());
        assertTrue(resetResponse.message().contains("Remet le score à zéro"));
    }

    @Test
    @DisplayName("Devrait extraire le bon gagnant de l'historique")
    void shouldExtractCorrectWinnerFromHistory() {
        GameRequest request = new GameRequest("ABABAA");
        List<GameResponse> result = gameService.playSequence(request);
        assertNotNull(result);
        assertTrue(result.size() > 0);
        GameResponse lastResponse = result.get(result.size() - 1);
        assertEquals("The Player A wins the game", lastResponse.score());
        assertEquals("GameWon", lastResponse.state());
    }

    @Test
    @DisplayName("Devrait gérer les erreurs de sauvegarde")
    void shouldHandleSaveErrors() {
        GameRequest request = new GameRequest("ABABAA");
        when(gamePort.save(any(GameEntity.class))).thenThrow(new RuntimeException("Erreur de sauvegarde"));
        assertThrows(RuntimeException.class, () -> gameService.playSequence(request));
        verify(gamePort, times(1)).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("Devrait maintenir l'état du jeu entre les appels")
    void shouldMaintainGameStateBetweenCalls() {
        GameRequest request1 = new GameRequest("AB");
        GameRequest request2 = new GameRequest("AB");
        List<GameResponse> result1 = gameService.playSequence(request1);
        List<GameResponse> result2 = gameService.playSequence(request2);
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Player A: 15 / Player B: 0", result1.get(0).score());
        assertEquals("Player A: 15 / Player B: 15", result1.get(1).score());
        assertEquals("Player A: 30 / Player B: 15", result2.get(0).score());
        assertEquals("Player A: 30 / Player B: 30", result2.get(1).score());
    }
}
