package com.crystal.kip.repository;

import com.crystal.kip.domain.GameProgress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameProgress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameProgressRepository extends JpaRepository<GameProgress, Long> {}
