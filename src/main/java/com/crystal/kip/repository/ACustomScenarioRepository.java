package com.crystal.kip.repository;

import com.crystal.kip.domain.Scenario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Scenario entity.
 */
@Repository
public interface ACustomScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByGameIdAndTopicId(Long gameId, Long topicId);
}
