package com.crystal.kip.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crystal.kip.domain.Scenario;

/**
 * Spring Data JPA repository for the Scenario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByGameAndTopic(
        Long gameId,
        Long topicId
    );
}