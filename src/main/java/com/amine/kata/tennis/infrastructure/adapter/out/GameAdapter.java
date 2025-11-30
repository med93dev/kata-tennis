package com.amine.kata.tennis.infrastructure.adapter.out;

import com.amine.kata.tennis.application.port.out.GamePort;
import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import com.amine.kata.tennis.infrastructure.repository.GameRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GameAdapter implements GamePort {

    private final GameRepository repository;

    public GameAdapter(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public GameEntity save(GameEntity game) {
        return repository.save(game);
    }

    @Override
    public List<GameEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public GameEntity findById(Long id) {
        Optional<GameEntity> game = repository.findById(id);
        return game.orElse(null);
    }
}
