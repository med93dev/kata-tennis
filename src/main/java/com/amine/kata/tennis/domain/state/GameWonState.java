package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;

public final class GameWonState implements GameState {

    private final String winner;

    public GameWonState(String winner) { this.winner = winner; }

    @Override
    public void pointWonBy(Game game, String player) {  }

    @Override
    public String getScore(Game game) { return winner + " wins"; }

    public String getWinner() { return winner; }
}
