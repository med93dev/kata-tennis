package com.amine.kata.tennis.infrastructure.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game_history")
@Schema(description = "Entité représentant l'historique d'une partie de tennis terminée")
public class GameHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de l'historique", example = "1")
    private Long id;

    @Schema(description = "Séquence de points jouée", example = "ABABAA")
    private String sequence;
    
    @Schema(description = "Score au moment de l'action", example = "Player A: 40 / Player B: 30")
    private String score;
    
    @Schema(description = "État du jeu", example = "Normal", allowableValues = {"Normal", "Deuce", "Advantage", "GameWon"})
    private String state;
    
    @Schema(description = "Message descriptif", example = "A scored a point!")
    private String message;
    
    @Schema(description = "Horodatage de l'action", example = "2025-09-05T02:30:21.442431400Z")
    private Instant timestamp;

    // Constructeurs
    public GameHistoryEntity() {}
    
    public GameHistoryEntity(String sequence, String score, String state, String message, Instant timestamp) {
        this.sequence = sequence;
        this.score = score;
        this.state = state;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getSequence() { return sequence; }
    public String getScore() { return score; }
    public String getState() { return state; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSequence(String sequence) { this.sequence = sequence; }
    public void setScore(String score) { this.score = score; }
    public void setState(String state) { this.state = state; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
