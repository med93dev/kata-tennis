package com.amine.kata.tennis.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record GameResponse(
        @Schema(description = "Score actuel du jeu",
                examples = {"Player A: 40 / Player B: 30", "0-0", "Deuce", "A wins"})
        String score,

        @Schema(description = "État actuel du jeu",
                examples = {"Normal", "Deuce", "Advantage", "GameWon"},
                allowableValues = {"Normal", "Deuce", "Advantage", "GameWon"})
        String state,

        @Schema(description = "Message descriptif de l'action",
                examples = {"A scored a point!", "Game has been reset", "It's deuce!", "A wins!"})
        String message,

        @Schema(description = "Horodatage de l'action", example = "2025-09-05T02:30:21.442431400Z")
        Instant timestamp
) {
    public GameResponse(String score, String state, String message) {
        this(score, state, message, Instant.now());
    }
}
