package com.myapp.repository;

import com.myapp.domain.LegalContent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LegalContent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LegalContentRepository extends JpaRepository<LegalContent, Long> {}
