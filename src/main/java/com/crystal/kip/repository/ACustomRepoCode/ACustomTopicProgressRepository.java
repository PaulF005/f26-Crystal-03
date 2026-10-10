package com.crystal.kip.repository.ACustomRepoCode;

import com.crystal.kip.domain.TopicProgress;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TopicProgress entity.
 * ACustomTopicProgressRepository
 */
@Repository
public interface ACustomTopicProgressRepository extends JpaRepository<TopicProgress, Long> {
    //READ functions
    @Query(
        "SELECT tp.competency FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getCompetencyDBTP(@Param("username") String username);

    /**
     * Gets Competency
     * @param username tableholder's username
     * @return competency value or -1.0 for error
     */
    default double getCurCompetencyTP(String username) {
        return getCompetencyDBTP(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT tp.improvement FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getImprovementDBTP(@Param("username") String username);

    /**
     * Gets Improvement
     * @param username tableholder's username
     * @return improvement value or -1.0 as an error
     */
    default double getImprovementTP(String username) {
        return getImprovementDBTP(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT tp.lastPracticedAt FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Instant> getLastPracticedAtDBTP(@Param("username") String username);

    /**
     * Gets LastPraticedAt
     * @param username tableholder's username
     * @return Instant value or EPOCH for error
     */
    default Instant getLastPracticedAtTP(String username) {
        return getLastPracticedAtDBTP(username).orElse(Instant.EPOCH);
    }

    @Query(
        "SELECT CASE WHEN COUNT(up) > 0 Then (" +
            "SELECT COUNT(c) FROM Concept c " +
            "JOIN c.topic t " +
            "JOIN TopicProgress tp ON tp.topic = t " +
            "WHERE tp.userProfile = up " +
            ") ELSE NULL END " +
            "FROM UserProfile up " +
            "JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Integer> getAllTopicNumTPDB(@Param("username") String username);

    /**
     * Gets number of topics associated with this topicProgress
     * @param username tableholder's username
     * @return Int value or -1 for error
     */
    default int getAllTopicNumTP(String username) {
        return getAllTopicNumTPDB(username).map(Integer::intValue).orElse(-1);
    }

    //UPDATE functions
    @Modifying
    @Query(
        "UPDATE TopicProgress tp SET tp.competency = :competency, tp.improvement =:improvement, tp.lastPracticedAt = :lastPracticedAt " +
            "WHERE tp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateTopicProgressToDB(
        @Param("competency") Float competency,
        @Param("improvement") Float improvement,
        @Param("lastPracticedAt") Instant lastPracticedAt
    );
}
