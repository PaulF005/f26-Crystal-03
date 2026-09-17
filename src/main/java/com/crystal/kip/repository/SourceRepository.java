package com.crystal.kip.repository;

import com.crystal.kip.domain.Source;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Source entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {}
