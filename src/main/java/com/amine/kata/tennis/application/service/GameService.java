package com.amine.kata.tennis.application.service;

import com.amine.kata.tennis.domain.Game;
import com.amine.kata.tennis.domain.exception.GameAlreadyFinishedException;
import com.amine.kata.tennis.domain.exception.InvalidPlayerException;
import com.amine.kata.tennis.domain.exception.InvalidSequenceException;
import com.amine.kata.tennis.domain.state.GameWonState;
import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import com.amine.kata.tennis.infrastructure.logging.StructuredLogger;
import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.application.port.in.GameUseCase;
import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService implements GameUseCase {

    private final Game game = new Game();
    private final GamePort gamePort;
    private final StructuredLogger logger;

    public GameService(GamePort gamePort) {
        this.gamePort = gamePort;
        this.logger = new StructuredLogger(GameService.class);
    }

    @Override
    public List<GameResponse> playSequence(GameRequest request) {
        Map<String, Object> input = new HashMap<>();
        input.put("sequence", request.sequence());
        input.put("sequenceLength", request.sequence().length());

        logger.logOperationStart("PLAY_SEQUENCE", input);

        try {
            validateSequence(request.sequence());

            List<GameResponse> result = new ArrayList<>();

        for (int i = 0; i < request.sequence().length(); i++) {
            char c = request.sequence().charAt(i);

            if (game.getState() instanceof GameWonState won) {
                throw new GameAlreadyFinishedException(won.getWinner());
            }

            String player = String.valueOf(c);
            validatePlayer(player);

            boolean wasGameWon = game.getState() instanceof GameWonState;

            game.pointWonBy(player);

            if (!wasGameWon && game.getState() instanceof GameWonState) {
                String normalScore = "Player A: " + getDisplayScore(game.getScoreA()) +
                                   " / Player B: " + getDisplayScore(game.getScoreB());
                result.add(new GameResponse(normalScore, "Normal", player + " scored a point!"));

                String winner = ((GameWonState) game.getState()).getWinner();
                result.add(new GameResponse("The Player "+ winner + " wins the game", "GameWon", "The Player "+ winner + " wins the game"));
            } else {
                String score = game.getScore();
                String state = score.contains("wins") ? "GameWon" :
                        score.equals("Deuce") ? "Deuce" :
                                score.contains("Advantage") ? "Advantage" : "Normal";

                String message = switch (state) {
                    case "Deuce" -> "It's deuce!";
                    case "Advantage", "GameWon" -> score + "!";
                    default -> player + " scored a point!";
                };

                GameResponse response = new GameResponse(score, state, message);
                result.add(response);
            }

            if (game.getState() instanceof GameWonState) {
                Map<String, Object> gameEndData = new HashMap<>();
                gameEndData.put("winner", extractWinner(result));
                gameEndData.put("totalSteps", result.size());
                gameEndData.put("finalScore", result.isEmpty() ? "N/A" : result.get(result.size() - 1).score());
                logger.logBusinessEvent("GAME_ENDED", "Partie terminée", gameEndData);

                saveGameHistory(request.sequence(), result);
                break;
            }
        }

        Map<String, Object> output = new HashMap<>();
        output.put("totalSteps", result.size());
        output.put("finalState", result.isEmpty() ? "N/A" : result.get(result.size() - 1).state());
        output.put("finalScore", result.isEmpty() ? "N/A" : result.get(result.size() - 1).score());

        logger.logOperationSuccess("PLAY_SEQUENCE", output);

        return result;
        } catch (Exception e) {
            logger.logOperationError("PLAY_SEQUENCE", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public GameResponse resetGame() {
        logger.logOperationStart("RESET_GAME", new HashMap<>());

        try {
            game.reset();
            GameResponse response = new GameResponse("0-0", "Normal", "Remet le score à zéro et réinitialise l'état du jeu.");

            Map<String, Object> output = new HashMap<>();
            output.put("score", response.score());
            output.put("state", response.state());
            output.put("message", response.message());

            logger.logOperationSuccess("RESET_GAME", output);

            return response;
        } catch (Exception e) {
            logger.logOperationError("RESET_GAME", e.getMessage(), e);
            throw e;
        }
    }

    private String getDisplayScore(int score) {
        return switch (score) {
            case 0, 15, 30, 40 -> String.valueOf(score);
            default -> "Game";
        };
    }

    private void validateSequence(String sequence) {
        if (sequence == null || sequence.trim().isEmpty()) {
            throw new InvalidSequenceException("");
        }

        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c != 'A' && c != 'B') {
                throw new InvalidSequenceException(sequence, i);
            }
        }
    }

    private void validatePlayer(String player) {
        if (!"A".equals(player) && !"B".equals(player)) {
            throw new InvalidPlayerException(player);
        }
    }

    private void saveGameHistory(String sequence, List<GameResponse> gameHistory) {
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("sequence", sequence);
        saveData.put("messageCount", gameHistory.size());

        logger.logOperationStart("SAVE_GAME_HISTORY", saveData);

        try {
            String winner = extractWinner(gameHistory);
            String finalScore = gameHistory.get(gameHistory.size() - 1).score();
            GameEntity gameEntity = new GameEntity(sequence, winner, finalScore, java.time.Instant.now());

            List<String> messages = new ArrayList<>();
            for (GameResponse response : gameHistory) {
                messages.add(response.message());
            }

            gameEntity.setMessages(messages);

            gamePort.save(gameEntity);

            Map<String, Object> output = new HashMap<>();
            output.put("gameId", gameEntity.getId());
            output.put("winner", winner);
            output.put("finalScore", finalScore);
            output.put("messageCount", messages.size());

            logger.logOperationSuccess("SAVE_GAME_HISTORY", output);
        } catch (Exception e) {
            logger.logOperationError("SAVE_GAME_HISTORY", e.getMessage(), e);
            throw e;
        }
    }

    private String extractWinner(List<GameResponse> gameHistory) {
        for (GameResponse response : gameHistory) {
            if ("GameWon".equals(response.state()) && response.score().contains("wins")) {
                return response.score().substring(0, 1);
            }
        }
        return "Unknown";
    }
}
