package com.crystal.kip.repository.CustomRepoCode;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Contains functions that allows application code to access an user's ConceptProgress attributes and update them also
 * CustomConceptProgressRepository
 */
@Repository
public interface CustomConceptProgressRepository {
    // READ functions
    @Query(
        "SELECT cp.competency FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND cp.concept.id = :conceptId"
    )
    Optional<Float> getCompetencyDBCP(@Param("username") String username, @Param("conceptId") Long conceptId);

    /**
     * Gets Competency
     * @param username tableholder's username
     * @param conceptId Concept being queried
     * @return competency value or -1.0 as an error
     */
    default double getCurCompetencyCP(String username, Long conceptId) {
        return getCompetencyDBCP(username, conceptId).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT cp.improvement FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND cp.concept.id = :conceptId"
    )
    Optional<Float> getImprovementDBCP(@Param("username") String username, @Param("conceptId") Long conceptId);

    /**
     * Gets Improvement
     * @param username tableholder's username
     * @param conceptId Concept being queried
     * @return improvement value or -1.0 as an error
     */
    default double getImprovementCP(String username, Long conceptId) {
        return getImprovementDBCP(username, conceptId).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT cp.lastPracticedAt FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND cp.concept.id = :conceptId"
    )
    Optional<Instant> getLastPracticedAtDBCP(@Param("username") String username, @Param("conceptId") Long conceptId);

    /**
     * Gets LastPracticedAt
     * @param username tableholder's username
     * @param conceptId Concept being queried
     * @return Instant value or EPOCH for error
     */
    default Instant getLastPracticedAtCP(String username, Long conceptId) {
        return getLastPracticedAtDBCP(username, conceptId).orElse(Instant.EPOCH);
    }

    @Query(
        "SELECT cp.maxQuestions FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND cp.concept.id = :conceptId"
    )
    Optional<Integer> getMaxQuestionsDBCP(@Param("username") String username, @Param("conceptId") Long conceptId);

    /**
     * Gets MaxQuestions
     * @param username tableholder's username
     * @param conceptId Concept being queried
     * @return max questions or -1 for error
     */
    default int getMaxQuestionsForRepo(String username, Long conceptId) {
        return getMaxQuestionsDBCP(username, conceptId).map(Integer::intValue).orElse(-1);
    }

    // UPDATE functions
    @Modifying
    @Query(
        "UPDATE ConceptProgress cp " +
            "SET cp.competency = :competency, cp.improvement = :improvement, cp.lastPracticedAt = :lastPracticedAt " +
            "WHERE cp.concept.id = :conceptId " +
            "AND cp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateConceptProgressToDBCP(
        @Param("conceptId") Long conceptId,
        @Param("competency") Float competency,
        @Param("improvement") Float improvement,
        @Param("lastPracticedAt") Instant lastPracticedAt
    );
}
