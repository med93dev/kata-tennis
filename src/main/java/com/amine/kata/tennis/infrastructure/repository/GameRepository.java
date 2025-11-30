package com.amine.kata.tennis.infrastructure.repository;

import com.amine.kata.tennis.infrastructure.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {
}
