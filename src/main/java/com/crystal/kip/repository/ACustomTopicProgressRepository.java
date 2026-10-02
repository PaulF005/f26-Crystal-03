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
public interface ACustomTopicProgressRepository extends JpaRepository<TopicProgress, Long> {
    //READ functions
    @Query(
        "SELECT tp.competency FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getCompantencyDB(@Param("username") String username);

    default double getCurCompantency(String username) {
        return getCompantencyDB(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT tp.maxQuestions FROM TopicProgress tp " +
            "INNER JOIN tp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Integer> getmaxQuestionsDB(@Param("username") String username);

    default int getMaxQuestionsForRepo(String username) {
        return getmaxQuestionsDB(username).map(Integer::intValue).orElse(-1);
    }

    //UPDATE functions
    @Modifying
    @Query(
        "UPDATE TopicProgress tp SET tp.competency = :competency, tp.lastPracticedAt = :lastPracticedAt " +
            "WHERE tp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateTopicProgressToDB(@Param("competency") Float competency, @Param("lastPracticedAt") Instant lastPracticedAt);
}
