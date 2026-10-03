package com.crystal.kip.repository.ACustomRepoCode;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ACustomGameProgressRepository {
    //READ functions
    @Query(
        "SELECT gp.sessionsPlayed FROM GameProgress gp " +
            "INNER JOIN gp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Integer> getSessionsDBGP(@Param("username") String username);

    default int getSessionsPlayed(String username) {
        return getSessionsDBGP(username).map(Integer::intValue).orElse(-1);
    }

    @Query(
        "SELECT gp.performance FROM GameProgress gp " +
            "INNER JOIN gp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username"
    )
    Optional<Float> getPerformanceDBGP(@Param("username") String username);

    default double getPerformance(String username) {
        return getPerformanceDBGP(username).map(Float::doubleValue).orElse(-1.0);
    }

    //UPDATE functions
}
