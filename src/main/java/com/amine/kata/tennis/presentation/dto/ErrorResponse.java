package com.amine.kata.tennis.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Réponse d'erreur standardisée")
public record ErrorResponse(
        @Schema(description = "Type d'erreur", example = "Erreur de validation")
        String error,
        
        @Schema(description = "Message d'erreur détaillé", example = "Les données fournies ne sont pas valides")
        String message,
        
        @Schema(description = "Détails des erreurs de validation (optionnel)")
        Map<String, String> details,
        
        @Schema(description = "Code de statut HTTP", example = "400")
        int status,
        
        @Schema(description = "Chemin de la requête", example = "uri=/api/v1/tennis/play")
        String path,
        
        @Schema(description = "Horodatage de l'erreur", example = "2025-09-04T23:30:21.442431400Z")
        Instant timestamp
) {}
