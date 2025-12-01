package com.amine.kata.tennis.domain.service;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameService implements GameUseCase {

    private static final String PLAYER_A = "A";
    private static final String PLAYER_B = "B";
    private static final String STATE_NORMAL = "Normal";
    private static final String STATE_DEUCE = "Deuce";
    private static final String STATE_ADVANTAGE = "Advantage";
    private static final String STATE_GAME_WON = "GameWon";

    private final Game game;
    private final GamePort gamePort;
    private final StructuredLogger logger;
    private final ScoreFormatter scoreFormatter;
    private final GameStateAnalyzer gameStateAnalyzer;

    public GameService(GamePort gamePort) {
        this.game = new Game();
        this.gamePort = gamePort;
        this.logger = new StructuredLogger(GameService.class);
        this.scoreFormatter = new ScoreFormatter();
        this.gameStateAnalyzer = new GameStateAnalyzer();
    }

    @Override
    public List<GameResponse> playSequence(GameRequest request) {
        logOperationStart(request);

        try {
            validateSequence(request.sequence());
            List<GameResponse> responses = processGameSequence(request.sequence());
            logOperationSuccess(responses);
            return responses;
        } catch (Exception e) {
            logger.logOperationError("PLAY_SEQUENCE", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public GameResponse resetGame() {
        logger.logOperationStart("RESET_GAME", Map.of());

        try {
            game.reset();
            GameResponse response = createResetResponse();
            logResetSuccess(response);
            return response;
        } catch (Exception e) {
            logger.logOperationError("RESET_GAME", e.getMessage(), e);
            throw e;
        }
    }

    private List<GameResponse> processGameSequence(String sequence) {
        List<GameResponse> responses = new ArrayList<>();

        for (int i = 0; i < sequence.length(); i++) {
            checkGameNotFinished();

            String player = String.valueOf(sequence.charAt(i));
            validatePlayer(player);

            boolean wasGameWon = isGameWon();
            game.pointWonBy(player);

            if (shouldAddGameWonResponses(wasGameWon)) {
                addGameWonResponses(responses, player);
            } else {
                responses.add(createGameResponse(player));
            }

            if (isGameWon()) {
                finalizeGame(sequence, responses);
                break;
            }
        }

        return responses;
    }

    private void checkGameNotFinished() {
        if (game.getState() instanceof GameWonState wonState) {
            throw new GameAlreadyFinishedException(wonState.getWinner());
        }
    }

    private boolean isGameWon() {
        return game.getState() instanceof GameWonState;
    }

    private boolean shouldAddGameWonResponses(boolean wasGameWon) {
        return !wasGameWon && isGameWon();
    }

    private void addGameWonResponses(List<GameResponse> responses, String player) {
        responses.add(createNormalScoreResponse(player));
        responses.add(createWinnerResponse());
    }

    private GameResponse createNormalScoreResponse(String player) {
        String normalScore = scoreFormatter.formatPlayerScores(
                game.getScoreA(),
                game.getScoreB()
        );
        return new GameResponse(normalScore, STATE_NORMAL, player + " scored a point!");
    }

    private GameResponse createWinnerResponse() {
        String winner = ((GameWonState) game.getState()).getWinner();
        String winMessage = "The Player " + winner + " wins the game";
        return new GameResponse(winMessage, STATE_GAME_WON, winMessage);
    }

    private GameResponse createGameResponse(String player) {
        String score = game.getScore();
        String state = gameStateAnalyzer.determineState(score);
        String message = gameStateAnalyzer.createMessage(state, score, player);
        return new GameResponse(score, state, message);
    }

    private void finalizeGame(String sequence, List<GameResponse> responses) {
        logGameEnd(responses);
        saveGameHistory(sequence, responses);
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
        if (!PLAYER_A.equals(player) && !PLAYER_B.equals(player)) {
            throw new InvalidPlayerException(player);
        }
    }

    private void saveGameHistory(String sequence, List<GameResponse> gameHistory) {
        Map<String, Object> saveData = Map.of(
                "sequence", sequence,
                "messageCount", gameHistory.size()
        );

        logger.logOperationStart("SAVE_GAME_HISTORY", saveData);

        try {
            GameEntity gameEntity = createGameEntity(sequence, gameHistory);
            gamePort.save(gameEntity);
            logSaveSuccess(gameEntity);
        } catch (Exception e) {
            logger.logOperationError("SAVE_GAME_HISTORY", e.getMessage(), e);
            throw e;
        }
    }

    private GameEntity createGameEntity(String sequence, List<GameResponse> gameHistory) {
        String winner = extractWinner(gameHistory);
        String finalScore = gameHistory.get(gameHistory.size() - 1).score();

        GameEntity gameEntity = new GameEntity(sequence, winner, finalScore, Instant.now());

        List<String> messages = gameHistory.stream()
                .map(GameResponse::message)
                .collect(Collectors.toList());

        gameEntity.setMessages(messages);
        return gameEntity;
    }

    private String extractWinner(List<GameResponse> gameHistory) {
        return gameHistory.stream()
                .filter(response -> STATE_GAME_WON.equals(response.state()))
                .filter(response -> response.score().contains("wins"))
                .findFirst()
                .map(response -> response.score().substring(0, 1))
                .orElse("Unknown");
    }

    private GameResponse createResetResponse() {
        return new GameResponse(
                "0-0",
                STATE_NORMAL,
                "Remet le score à zéro et réinitialise l'état du jeu."
        );
    }

    // Logging helper methods
    private void logOperationStart(GameRequest request) {
        Map<String, Object> input = Map.of(
                "sequence", request.sequence(),
                "sequenceLength", request.sequence().length()
        );
        logger.logOperationStart("PLAY_SEQUENCE", input);
    }

    private void logOperationSuccess(List<GameResponse> result) {
        if (result.isEmpty()) {
            return;
        }

        GameResponse lastResponse = result.get(result.size() - 1);
        Map<String, Object> output = Map.of(
                "totalSteps", result.size(),
                "finalState", lastResponse.state(),
                "finalScore", lastResponse.score()
        );
        logger.logOperationSuccess("PLAY_SEQUENCE", output);
    }

    private void logGameEnd(List<GameResponse> result) {
        Map<String, Object> gameEndData = Map.of(
                "winner", extractWinner(result),
                "totalSteps", result.size(),
                "finalScore", result.get(result.size() - 1).score()
        );
        logger.logBusinessEvent("GAME_ENDED", "Partie terminée", gameEndData);
    }

    private void logSaveSuccess(GameEntity gameEntity) {
        Map<String, Object> output = Map.of(
                "gameId", gameEntity.getId(),
                "winner", gameEntity.getWinner(),
                "finalScore", gameEntity.getFinalScore(),
                "messageCount", gameEntity.getMessages().size()
        );
        logger.logOperationSuccess("SAVE_GAME_HISTORY", output);
    }

    private void logResetSuccess(GameResponse response) {
        Map<String, Object> output = Map.of(
                "score", response.score(),
                "state", response.state(),
                "message", response.message()
        );
        logger.logOperationSuccess("RESET_GAME", output);
    }

    // Inner helper classes
    private static class ScoreFormatter {
        String formatPlayerScores(int scoreA, int scoreB) {
            return "Player A: " + getDisplayScore(scoreA) +
                    " / Player B: " + getDisplayScore(scoreB);
        }

        private String getDisplayScore(int score) {
            return switch (score) {
                case 0, 15, 30, 40 -> String.valueOf(score);
                default -> "Game";
            };
        }
    }

    private static class GameStateAnalyzer {
        String determineState(String score) {
            if (score.contains("wins")) return STATE_GAME_WON;
            if (score.equals("Deuce")) return STATE_DEUCE;
            if (score.contains("Advantage")) return STATE_ADVANTAGE;
            return STATE_NORMAL;
        }

        String createMessage(String state, String score, String player) {
            return switch (state) {
                case STATE_DEUCE -> "It's deuce!";
                case STATE_ADVANTAGE, STATE_GAME_WON -> score + "!";
                default -> player + " scored a point!";
            };
        }
    }
}