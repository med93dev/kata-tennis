package com.amine.kata.tennis.domain.exception;

public class InvalidSequenceException extends IllegalArgumentException {
    
    public InvalidSequenceException(String sequence) {
        super(String.format("Séquence invalide: '%s'. La séquence ne peut contenir que les caractères 'A' et 'B'.", sequence));
    }
    
    public InvalidSequenceException(String sequence, int position) {
        super(String.format("Séquence invalide: '%s'. Caractère invalide à la position %d. Seuls 'A' et 'B' sont autorisés.", 
                sequence, position + 1));
    }
}
