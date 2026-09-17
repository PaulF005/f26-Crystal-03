package com.crystal.kip.repository;

import com.crystal.kip.domain.Concept;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Concept entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {}
