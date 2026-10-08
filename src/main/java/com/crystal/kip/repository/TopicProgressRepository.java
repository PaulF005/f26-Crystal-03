package com.crystal.kip.repository;

import com.crystal.kip.domain.TopicProgress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TopicProgress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TopicProgressRepository extends JpaRepository<TopicProgress, Long> {}
