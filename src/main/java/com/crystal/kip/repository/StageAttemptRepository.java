package com.crystal.kip.repository;

import com.crystal.kip.domain.StageAttempt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StageAttempt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StageAttemptRepository extends JpaRepository<StageAttempt, Long> {}
