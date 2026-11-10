package com.crystal.kip.repository;

import com.crystal.kip.domain.StageAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Reads completed-session stage attempts for curation.
 */
public interface StageAttemptCurationRepository extends Repository<StageAttempt, Long> {
    @EntityGraph(attributePaths = { "stage" })
    @Query(
        """
        SELECT attempt
        FROM StageAttempt attempt
        WHERE attempt.user.id = :userId
          AND attempt.stage.scenario.game.id = :gameId
          AND attempt.stage.scenario.topic.id = :topicId
          AND attempt.gameSession.completedAt IS NOT NULL
        """
    )
    List<StageAttempt> findCurationHistory(@Param("userId") Long userId, @Param("gameId") Long gameId, @Param("topicId") Long topicId);
}
