package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;

public sealed interface GameState permits NormalState, DeuceState, AdvantageState, GameWonState {
    void pointWonBy(Game game, String player);
    String getScore(Game game);
}
