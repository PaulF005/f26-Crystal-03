package com.crystal.kip.repository.ACustomRepoCode;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Contains functions that allows application code to access an user's ConceptProgress attributes and update them also
 * ACustomConceptProgressRepository
 */
@Repository
public interface ACustomConceptProgressRepository {
    //READ functions
    @Query(
        "SELECT cp.competency FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getCompetencyDBCP(@Param("username") String username);

    /**
     * Gets Competency
     * @param username tableholder's username
     * @return  competency value or -1.0 as an error
     */
    default double getCurCompentencyCP(String username) {
        return getCompetencyDBCP(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT cp.improvement FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getImprovementDBCP(@Param("username") String username);

    /**
     * Gets Improvement
     * @param username tableholder's username
     * @return improvement value or -1.0 as an error
     */
    default double getImprovementCP(String username) {
        return getImprovementDBCP(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT cp.lastPracticedAt FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Instant> getLastPracticedAtDBCP(@Param("username") String username);

    /**
     * Gets LastPraticedAt
     * @param username tableholder's username
     * @return Instant value or EPOCH for error
     */
    default Instant getLastPracticedAtCP(String username) {
        return getLastPracticedAtDBCP(username).orElse(Instant.EPOCH);
    }

    @Query(
        "SELECT cp.maxQuestions FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Integer> getmaxQuestionsDBCP(@Param("username") String username);

    /**
     * Gets MaxQuestions
     * @param username tableholder's username
     * @return max questions or -1 for error
     */
    default int getMaxQuestionsForRepo(String username) {
        return getmaxQuestionsDBCP(username).map(Integer::intValue).orElse(-1);
    }

    //UPDATE functions
    @Modifying
    @Query(
        "UPDATE ConceptProgress cp SET cp.competency = :competency, cp.improvement = :improvement, cp.lastPracticedAt = :lastPracticedAt " +
            "WHERE cp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )

    /**
     * Updates a tableholder's table
     * @param competency The updated value for competency
     * @param improvement How much the competency was updated by
     * @param lastPracticedAt Instant value of when table was updated
     */
    void updateConceptProgressToDBCP(
        @Param("competency") Float competency,
        @Param("improvement") Float improvement,
        @Param("lastPracticedAt") Instant lastPracticedAt
    );
}
