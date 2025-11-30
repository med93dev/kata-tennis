package com.amine.kata.tennis.infrastructure.adapter.out;

import com.amine.kata.tennis.infrastructure.entity.GameHistoryEntity;
import com.amine.kata.tennis.infrastructure.repository.GameHistoryRepository;
import com.amine.kata.tennis.application.port.out.GameHistoryPort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GameHistoryAdapter implements GameHistoryPort {

    private final GameHistoryRepository repository;

    public GameHistoryAdapter(GameHistoryRepository repository) { this.repository = repository; }

    @Override
    public void save(GameHistoryEntity entity) { repository.save(entity); }

    @Override
    public List<GameHistoryEntity> findAll() { return repository.findAll(); }
}
