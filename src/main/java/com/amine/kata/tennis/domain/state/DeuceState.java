package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;

public final class DeuceState implements GameState {
    @Override
    public void pointWonBy(Game game, String player) {
        game.setState(new AdvantageState(player));
    }

    @Override
    public String getScore(Game game) { return "Deuce"; }
}
