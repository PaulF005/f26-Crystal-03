package com.crystal.kip.repository;

import com.crystal.kip.domain.GameSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {}
