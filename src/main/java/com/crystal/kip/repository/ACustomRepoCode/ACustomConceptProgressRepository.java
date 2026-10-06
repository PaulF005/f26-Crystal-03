package com.crystal.kip.repository.ACustomRepoCode;

import jakarta.xml.bind.annotation.XmlElement.DEFAULT;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ACustomConceptProgressRepository {
    //READ functions
    @Query(
        "SELECT cp.competency FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getCompantencyDBCP(@Param("username") String username);

    default double getCurCompantencyCP(String username) {
        return getCompantencyDBCP(username).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT cp.maxQuestions FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Integer> getmaxQuestionsDB(@Param("username") String username);

    default int getMaxQuestionsForRepo(String username) {
        return getmaxQuestionsDB(username).map(Integer::intValue).orElse(-1);
    }

    @Query(
        "SELECT cp.improvement FROM ConceptProgress cp " +
            "INNER JOIN cp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getImprovementDBCP(@Param("username") String username);

    default double getImprovement(String username) {
        return getImprovementDBCP(username).map(Float::doubleValue).orElse(-1.0);
    }

    //UPDATE functions
    @Modifying
    @Query(
        "UPDATE ConceptProgress cp SET cp.competency = :competency, cp.lastPracticedAt = :lastPracticedAt " +
            "WHERE cp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateConceptProgressToDB(@Param("competency") Float competency, @Param("lastPracticedAt") Instant lastPracticedAt);
}
