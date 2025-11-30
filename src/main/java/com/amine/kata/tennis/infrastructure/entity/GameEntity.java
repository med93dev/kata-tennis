package com.amine.kata.tennis.infrastructure.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "games")
@Schema(description = "Entité représentant une partie de tennis complète")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la partie", example = "1")
    private Long id;

    @Schema(description = "Séquence de points jouée", example = "ABABAA")
    private String sequence;

    @Schema(description = "Gagnant de la partie", example = "A")
    private String winner;

    @Schema(description = "Score final de la partie", example = "A wins")
    private String finalScore;

    @Schema(description = "Date de création de la partie", example = "2025-09-05T02:30:21.442431400Z")
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "game_messages", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "message", length = 1000)
    @OrderColumn(name = "message_order")
    @Schema(description = "Liste des messages de la partie")
    private List<String> messages;

    public GameEntity() {}

    public GameEntity(String sequence, String winner, String finalScore, Instant createdAt) {
        this.sequence = sequence;
        this.winner = winner;
        this.finalScore = finalScore;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getSequence() { return sequence; }
    public String getWinner() { return winner; }
    public String getFinalScore() { return finalScore; }
    public Instant getCreatedAt() { return createdAt; }
    public List<String> getMessages() { return messages; }

    public void setId(Long id) { this.id = id; }
    public void setSequence(String sequence) { this.sequence = sequence; }
    public void setWinner(String winner) { this.winner = winner; }
    public void setFinalScore(String finalScore) { this.finalScore = finalScore; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setMessages(List<String> messages) { this.messages = messages; }
}
