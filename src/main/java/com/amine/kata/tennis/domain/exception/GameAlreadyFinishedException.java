package com.amine.kata.tennis.domain.exception;

public class GameAlreadyFinishedException extends IllegalStateException {
    
    public GameAlreadyFinishedException() {
        super("Le jeu est déjà terminé. Impossible de jouer d'autres points.");
    }
    
    public GameAlreadyFinishedException(String winner) {
        super(String.format("Le jeu est déjà terminé. Le gagnant est: %s", winner));
    }
}
