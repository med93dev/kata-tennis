package com.amine.kata.tennis.domain;

import com.amine.kata.tennis.domain.state.GameState;
import com.amine.kata.tennis.domain.state.NormalState;


public class Game {

    private int scoreA = 0;
    private int scoreB = 0;
    private GameState state = new NormalState();

    public void pointWonBy(String player) { state.pointWonBy(this, player); }
    public String getScore() { return state.getScore(this); }

    public void reset() {
        this.scoreA = 0;
        this.scoreB = 0;
        this.state = new NormalState();
    }
    public int getScoreA() { return scoreA; }
    public void setScoreA(int scoreA) { this.scoreA = scoreA; }
    public int getScoreB() { return scoreB; }
    public void setScoreB(int scoreB) { this.scoreB = scoreB; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
}
