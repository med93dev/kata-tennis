package com.amine.kata.tennis.integration;

import com.amine.kata.tennis.application.port.in.GameUseCase;
import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.domain.exception.InvalidSequenceException;
import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration simplifiés pour l'API Tennis")
class TennisGameIntegrationTestSimple {

    @Autowired
    private GameUseCase gameUseCase;

    @Autowired
    private GamePort gamePort;

    @Test
    @DisplayName("Scénario avec Deuce: Jouer une séquence qui se termine par Deuce")
    void scenarioWithDeuce() {
        GameRequest request = new GameRequest("BABABA");

        List<GameResponse> result = gameUseCase.playSequence(request);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("Deuce", result.get(5).score());
        assertEquals("Deuce", result.get(5).state());
        assertEquals("It's deuce!", result.get(5).message());
    }

    @Test
    @DisplayName("Scénario d'erreur: Séquence invalide")
    void errorScenarioInvalidSequence() {
        GameRequest request = new GameRequest("ABC");

        assertThrows(
                InvalidSequenceException.class,
                () -> gameUseCase.playSequence(request)
        );
    }

    @Test
    @DisplayName("Scénario d'erreur: Séquence vide")
    void errorScenarioEmptySequence() {
        GameRequest request = new GameRequest("");

        assertThrows(
                InvalidSequenceException.class,
                () -> gameUseCase.playSequence(request)
        );
    }

}
