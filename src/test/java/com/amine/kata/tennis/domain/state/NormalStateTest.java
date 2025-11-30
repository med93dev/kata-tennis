package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests pour NormalState")
class NormalStateTest {

    private Game game;
    private NormalState normalState;

    @BeforeEach
    void setUp() {
        game = new Game();
        normalState = new NormalState();
    }

    @Test
    @DisplayName("Devrait afficher le score initial 0-0")
    void shouldDisplayInitialScore() {
        assertEquals("Player A: 0 / Player B: 0", normalState.getScore(game));
    }

    @Test
    @DisplayName("Devrait passer de 0 à 15 pour le joueur A")
    void shouldIncrementScoreFrom0To15ForPlayerA() {
        normalState.pointWonBy(game, "A");
        assertEquals("Player A: 15 / Player B: 0", normalState.getScore(game));
    }

    @Test
    @DisplayName("Devrait passer de 0 à 15 pour le joueur B")
    void shouldIncrementScoreFrom0To15ForPlayerB() {
        normalState.pointWonBy(game, "B");
        assertEquals("Player A: 0 / Player B: 15", normalState.getScore(game));
    }

    @Test
    @DisplayName("Devrait passer en état Deuce quand les deux joueurs sont à 40")
    void shouldTransitionToDeuceStateWhenBothPlayersAt40() {
        game.setScoreA(40);
        game.setScoreB(40);
        normalState.pointWonBy(game, "A");

        assertInstanceOf(DeuceState.class, game.getState());
    }

    @Test
    @DisplayName("Devrait faire gagner A quand A est à 40 et B a moins de 30")
    void shouldMakeAWinWhenAIsAt40AndBHasLessThan30() {
        game.setScoreA(40);
        game.setScoreB(15);
        normalState.pointWonBy(game, "A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner A quand A est à 40 et B a 0")
    void shouldMakeAWinWhenAIsAt40AndBHas0() {
        game.setScoreA(40);
        game.setScoreB(0);
        normalState.pointWonBy(game, "A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner B quand B est à 40 et A a moins de 30")
    void shouldMakeBWinWhenBIsAt40AndAHasLessThan30() {
        game.setScoreA(15);
        game.setScoreB(40);
        normalState.pointWonBy(game, "B");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("B", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner B quand B est à 40 et A a 0")
    void shouldMakeBWinWhenBIsAt40AndAHas0() {
        game.setScoreA(0);
        game.setScoreB(40);
        normalState.pointWonBy(game, "B");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("B", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner A quand A est déjà à 40 et B passe à 30")
    void shouldMakeAWinWhenAIsAlreadyAt40AndBGoesTo30() {
        game.setScoreA(40);
        game.setScoreB(30);
        normalState.pointWonBy(game, "A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait faire gagner B quand B est déjà à 40 et A passe à 30")
    void shouldMakeBWinWhenBIsAlreadyAt40AndAGoesTo30() {
        game.setScoreA(30);
        game.setScoreB(40);
        normalState.pointWonBy(game, "B");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("B", ((GameWonState) game.getState()).getWinner());
    }

}
