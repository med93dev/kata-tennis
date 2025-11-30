package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests pour GameWonState")
class GameWonStateTest {

    private Game game;
    private GameWonState gameWonStateA;
    private GameWonState gameWonStateB;

    @BeforeEach
    void setUp() {
        game = new Game();
        gameWonStateA = new GameWonState("A");
        gameWonStateB = new GameWonState("B");
    }

    @Test
    @DisplayName("Devrait afficher 'A wins'")
    void shouldDisplayAWins() {
        game.setState(gameWonStateA);
        assertEquals("A wins", gameWonStateA.getScore(game));
    }

    @Test
    @DisplayName("Devrait afficher 'B wins'")
    void shouldDisplayBWins() {
        game.setState(gameWonStateB);
        assertEquals("B wins", gameWonStateB.getScore(game));
    }

    @Test
    @DisplayName("Devrait retourner le bon gagnant pour A")
    void shouldReturnCorrectWinnerForA() {
        assertEquals("A", gameWonStateA.getWinner());
    }

    @Test
    @DisplayName("Devrait retourner le bon gagnant pour B")
    void shouldReturnCorrectWinnerForB() {
        assertEquals("B", gameWonStateB.getWinner());
    }


}
