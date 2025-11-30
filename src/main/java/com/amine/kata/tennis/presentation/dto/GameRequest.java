package com.amine.kata.tennis.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requête pour jouer une séquence de points de tennis")
public record GameRequest(
        @NotBlank(message="Sequence must not be empty") 
        @Schema(description = "Séquence de points (A pour joueur A, B pour joueur B)", 
                example = "ABABAA", 
                required = true) 
        String sequence
) {}

