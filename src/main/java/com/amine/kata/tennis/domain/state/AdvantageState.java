package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;

public final class AdvantageState implements GameState {

    private final String advantagePlayer;

    public AdvantageState(String player) { this.advantagePlayer = player; }

    @Override
    public void pointWonBy(Game game, String player) {
        if (player.equals(advantagePlayer)) game.setState(new GameWonState(player));
        else game.setState(new DeuceState());
    }

    @Override
    public String getScore(Game game) { return "Advantage " + advantagePlayer; }
}
