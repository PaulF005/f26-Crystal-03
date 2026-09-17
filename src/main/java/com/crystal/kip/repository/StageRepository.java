package com.crystal.kip.repository;

import com.crystal.kip.domain.Stage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Stage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {}
