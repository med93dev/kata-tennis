package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests pour AdvantageState")
class AdvantageStateTest {

    private Game game;
    private AdvantageState advantageStateA;
    private AdvantageState advantageStateB;

    @BeforeEach
    void setUp() {
        game = new Game();
        advantageStateA = new AdvantageState("A");
        advantageStateB = new AdvantageState("B");
    }

    @Test
    @DisplayName("Devrait afficher 'Advantage A'")
    void shouldDisplayAdvantageA() {
        game.setState(advantageStateA);
        assertEquals("Advantage A", advantageStateA.getScore(game));
    }

    @Test
    @DisplayName("Devrait afficher 'Advantage B'")
    void shouldDisplayAdvantageB() {
        game.setState(advantageStateB);
        assertEquals("Advantage B", advantageStateB.getScore(game));
    }

    @Test
    @DisplayName("Devrait faire gagner A quand A a l'avantage et marque")
    void shouldMakeAWinWhenAHasAdvantageAndScores() {
        game.setState(advantageStateA);
        advantageStateA.pointWonBy(game, "A");

        assertInstanceOf(GameWonState.class, game.getState());
        assertEquals("A", ((GameWonState) game.getState()).getWinner());
    }

    @Test
    @DisplayName("Devrait retourner en deuce quand A a l'avantage et B marque")
    void shouldReturnToDeuceWhenAHasAdvantageAndBScores() {
        game.setState(advantageStateA);
        advantageStateA.pointWonBy(game, "B");

        assertInstanceOf(DeuceState.class, game.getState());
        assertEquals("Deuce", game.getState().getScore(game));
    }


}
