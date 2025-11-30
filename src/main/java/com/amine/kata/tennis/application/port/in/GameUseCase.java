package com.amine.kata.tennis.application.port.in;

import com.amine.kata.tennis.presentation.dto.GameRequest;
import com.amine.kata.tennis.presentation.dto.GameResponse;

import java.util.List;

public interface GameUseCase {

    List<GameResponse> playSequence(GameRequest request);

    GameResponse resetGame();
}
