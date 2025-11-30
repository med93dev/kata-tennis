package com.amine.kata.tennis.infrastructure.repository;

import com.amine.kata.tennis.infrastructure.entity.GameHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistoryEntity, Long> {}
