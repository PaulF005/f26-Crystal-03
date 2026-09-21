package com.crystal.kip.repository;

import com.crystal.kip.domain.ConceptProgress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ConceptProgress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptProgressRepository extends JpaRepository<ConceptProgress, Long> {}
