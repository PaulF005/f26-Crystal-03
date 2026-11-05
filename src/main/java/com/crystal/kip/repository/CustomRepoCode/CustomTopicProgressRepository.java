package com.crystal.kip.repository.CustomRepoCode;

import com.crystal.kip.domain.TopicProgress;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TopicProgress entity.
 * CustomTopicProgressRepository
 */
@Repository
public interface CustomTopicProgressRepository extends JpaRepository<TopicProgress, Long> {
    // READ functions
    @Query(
        "SELECT tp.competency FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND tp.topic.id = :topicId"
    )
    Optional<Float> getCompetencyDBTP(@Param("username") String username, @Param("topicId") Long topicId);

    default double getCurCompetencyTP(String username, Long topicId) {
        return getCompetencyDBTP(username, topicId).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT tp.improvement FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND tp.topic.id = :topicId"
    )
    Optional<Float> getImprovementDBTP(@Param("username") String username, @Param("topicId") Long topicId);

    default double getImprovementTP(String username, Long topicId) {
        return getImprovementDBTP(username, topicId).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT tp.lastPracticedAt FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND tp.topic.id = :topicId"
    )
    Optional<Instant> getLastPracticedAtDBTP(@Param("username") String username, @Param("topicId") Long topicId);

    default Instant getLastPracticedAtTP(String username, Long topicId) {
        return getLastPracticedAtDBTP(username, topicId).orElse(Instant.EPOCH);
    }

    @Query("SELECT COUNT(c) FROM Concept c " + "WHERE c.topic.id = :topicId")
    Long getAllTopicNumTPDB(@Param("topicId") Long topicId);

    /**
     * Gets number of Concepts associated with this Topic
     */
    default int getAllTopicNumTP(Long topicId) {
        Long count = getAllTopicNumTPDB(topicId);

        if (count == null) {
            return -1;
        }

        return count.intValue();
    }

    // UPDATE functions
    @Modifying
    @Query(
        "UPDATE TopicProgress tp " +
            "SET tp.competency = :competency, tp.improvement = :improvement, tp.lastPracticedAt = :lastPracticedAt " +
            "WHERE tp.topic.id = :topicId " +
            "AND tp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateTopicProgressToDB(
        @Param("topicId") Long topicId,
        @Param("competency") Float competency,
        @Param("improvement") Float improvement,
        @Param("lastPracticedAt") Instant lastPracticedAt
    );
}
