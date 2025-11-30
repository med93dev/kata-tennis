package com.amine.kata.tennis.application.port.out;

import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import java.util.List;

public interface GamePort {
    GameEntity save(GameEntity game);
    List<GameEntity> findAll();
    GameEntity findById(Long id);
}
