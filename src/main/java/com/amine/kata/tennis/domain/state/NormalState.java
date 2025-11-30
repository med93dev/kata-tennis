package com.amine.kata.tennis.domain.state;

import com.amine.kata.tennis.domain.Game;

public final class NormalState implements GameState {

    @Override
    public void pointWonBy(Game game, String player) {
        int currentScoreA = game.getScoreA();
        int currentScoreB = game.getScoreB();

        if (player.equals("A")) {
            game.setScoreA(nextScore(currentScoreA));
        } else {
            game.setScoreB(nextScore(currentScoreB));
        }

        int newScoreA = game.getScoreA();
        int newScoreB = game.getScoreB();

        if (newScoreA == 40 && newScoreB == 40) {
            game.setState(new DeuceState());
            return;
        }

        if (player.equals("A")) {
            if (newScoreA == 40 && newScoreB < 30) {
                game.setState(new GameWonState("A"));
            } else if (currentScoreA == 40 && newScoreB < 40) {
                game.setState(new GameWonState("A"));
            }
        } else {
            if (newScoreB == 40 && newScoreA < 30) {
                game.setState(new GameWonState("B"));
            } else if (currentScoreB == 40 && newScoreA < 40) {
                game.setState(new GameWonState("B"));
            }
        }
    }

    private int nextScore(int current) {
        return switch (current) {
            case 0 -> 15;
            case 15 -> 30;
            case 30 -> 40;
            case 40 -> 40;
            default -> current;
        };
    }

    @Override
    public String getScore(Game game) {
        if (game.getState() instanceof GameWonState) {
            return ((GameWonState) game.getState()).getWinner() + " wins";
        }
        return "Player A: " + displayScore(game.getScoreA()) +
                " / Player B: " + displayScore(game.getScoreB());
    }

    private String displayScore(int score) {
        return switch (score) {
            case 0, 15, 30, 40 -> String.valueOf(score);
            default -> "Game";
        };
    }
}
