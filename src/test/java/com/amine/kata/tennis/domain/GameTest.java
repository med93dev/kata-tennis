package com.amine.kata.tennis.domain;

import com.amine.kata.tennis.domain.state.AdvantageState;
import com.amine.kata.tennis.domain.state.DeuceState;
import com.amine.kata.tennis.domain.state.GameWonState;
import com.amine.kata.tennis.domain.state.NormalState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests pour Game")
class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    @DisplayName("Devrait initialiser avec un état Normal et des scores à 0")
    void shouldInitializeWithNormalStateAndZeroScores() {
        assertInstanceOf(NormalState.class, game.getState());
        assertEquals(0, game.getScoreA());
        assertEquals(0, game.getScoreB());
    }


    @Test
    @DisplayName("Devrait passer en état Deuce quand les deux joueurs atteignent 40")
    void shouldTransitionToDeuceStateWhenBothPlayersReach40() {
        game.setScoreA(40);
        game.setScoreB(40);

        game.pointWonBy("A");

        assertInstanceOf(DeuceState.class, game.getState());
    }

    @Test
    @DisplayName("Devrait faire gagner A quand A atteint 40 et B a moins de 30")
    void shouldMakeAWinWhenAReaches40AndBHasLessThan30() {
        game.setScoreA(40);
        game.setScoreB(15);

        game.pointWonBy("A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner B quand B atteint 40 et A a moins de 30")
    void shouldMakeBWinWhenBReaches40AndAHasLessThan30() {
        game.setScoreA(15);
        game.setScoreB(40);

        game.pointWonBy("B");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("B", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait réinitialiser le jeu correctement")
    void shouldResetGameCorrectly() {

        game.pointWonBy("A");
        game.pointWonBy("A");
        game.pointWonBy("B");

        assertTrue(game.getScoreA() > 0 || game.getScoreB() > 0);

        game.reset();

        assertInstanceOf(NormalState.class, game.getState());
        assertEquals(0, game.getScoreA());
        assertEquals(0, game.getScoreB());
    }

    @Test
    @DisplayName("Devrait retourner le score via getScore()")
    void shouldReturnScoreViaGetScore() {
        game.setScoreA(15);
        game.setScoreB(30);

        String score = game.getScore();
        assertEquals("Player A: 15 / Player B: 30", score);
    }

    @Test
    @DisplayName("Devrait gérer une séquence complète de jeu")
    void shouldHandleCompleteGameSequence() {
        game.pointWonBy("A");
        game.pointWonBy("A");
        game.pointWonBy("B");
        game.pointWonBy("B");
        game.pointWonBy("A");
        game.pointWonBy("A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait gérer une séquence avec deuce")
    void shouldHandleSequenceWithDeuce() {
        game.setScoreA(40);
        game.setScoreB(40);
        game.pointWonBy("A");


        assertInstanceOf(DeuceState.class, game.getState());
        assertEquals("Deuce", game.getState().getScore(game));

        game.pointWonBy("A");
        assertInstanceOf(AdvantageState.class, game.getState());
        assertEquals("Advantage A", game.getState().getScore(game));

        game.pointWonBy("B");
        assertInstanceOf(DeuceState.class, game.getState());
        assertEquals("Deuce", game.getState().getScore(game));

        game.pointWonBy("A");
        game.pointWonBy("A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }
}
