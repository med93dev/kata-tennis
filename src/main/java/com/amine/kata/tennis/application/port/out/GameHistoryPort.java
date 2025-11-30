package com.amine.kata.tennis.application.port.out;

import com.amine.kata.tennis.infrastructure.entity.GameHistoryEntity;
import java.util.List;

public interface GameHistoryPort {
    void save(GameHistoryEntity entity);
    List<GameHistoryEntity> findAll();
}
