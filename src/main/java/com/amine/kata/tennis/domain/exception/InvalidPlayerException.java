package com.amine.kata.tennis.domain.exception;

public class InvalidPlayerException extends IllegalArgumentException {
    
    public InvalidPlayerException(String player) {
        super(String.format("Joueur invalide: '%s'. Seuls 'A' et 'B' sont autorisés.", player));
    }
}
