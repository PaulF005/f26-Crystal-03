package com.crystal.kip.repository;

import com.crystal.kip.domain.TopicProgress;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TopicProgress entity.
 */
@Repository
public interface TopicProgressRepository extends JpaRepository<TopicProgress, Long> {}
