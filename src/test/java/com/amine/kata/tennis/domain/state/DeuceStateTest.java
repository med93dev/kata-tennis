package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("Tests pour DeuceState")
class DeuceStateTest {

    private Game game;
    private DeuceState deuceState;

    @BeforeEach
    void setUp() {
        game = new Game();
        deuceState = new DeuceState();
        game.setScoreA(40);
        game.setScoreB(40);
        game.setState(deuceState);
    }

    @Test
    @DisplayName("Devrait afficher 'Deuce'")
    void shouldDisplayDeuce() {
        assertEquals("Deuce", deuceState.getScore(game));
    }

    @Test
    @DisplayName("Devrait passer en état Advantage pour le joueur A")
    void shouldTransitionToAdvantageStateForPlayerA() {
        deuceState.pointWonBy(game, "A");

        assertInstanceOf(AdvantageState.class, game.getState());
        assertEquals("Advantage A", game.getState().getScore(game));
    }

    @Test
    @DisplayName("Devrait maintenir l'état Deuce après un point A puis un point B")
    void shouldMaintainDeuceStateAfterPointAThenPointB() {
        deuceState.pointWonBy(game, "A");
        assertInstanceOf(AdvantageState.class, game.getState());
        game.getState().pointWonBy(game, "B");
        assertInstanceOf(DeuceState.class, game.getState());
        assertEquals("Deuce", game.getState().getScore(game));
    }

}
