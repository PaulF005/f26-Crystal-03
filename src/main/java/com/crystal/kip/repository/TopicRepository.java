package com.crystal.kip.repository;

import com.crystal.kip.domain.Topic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Topic entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query(
        """
        SELECT DISTINCT scenario.topic
        FROM Scenario scenario
        WHERE scenario.game.id = :gameId
        ORDER BY scenario.topic.name
        """
    )
    List<Topic> findTopicsForGame(@Param("gameId") Long gameId);
}
