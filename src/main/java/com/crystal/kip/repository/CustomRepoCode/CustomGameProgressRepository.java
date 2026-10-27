package com.crystal.kip.repository.CustomRepoCode;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomGameProgressRepository {
    // READ functions
    @Query(
        "SELECT gp.sessionsPlayed FROM GameProgress gp " +
            "INNER JOIN gp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND gp.game.id = :gameId"
    )
    Optional<Integer> getSessionsDBGP(@Param("username") String username, @Param("gameId") Long gameId);

    default int getSessionsPlayedGP(String username, Long gameId) {
        return getSessionsDBGP(username, gameId).map(Integer::intValue).orElse(-1);
    }

    @Query(
        "SELECT gp.performance FROM GameProgress gp " +
            "INNER JOIN gp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND gp.game.id = :gameId"
    )
    Optional<Float> getPerformanceDBGP(@Param("username") String username, @Param("gameId") Long gameId);

    default double getPerformanceGP(String username, Long gameId) {
        return getPerformanceDBGP(username, gameId).map(Float::doubleValue).orElse(-1.0);
    }

    @Query(
        "SELECT gp.lastPlayedAt FROM GameProgress gp " +
            "INNER JOIN gp.userProfile up " +
            "INNER JOIN up.dataUser u " +
            "WHERE u.login = :username " +
            "AND gp.game.id = :gameId"
    )
    Optional<Instant> getLastPlayedAtDBGP(@Param("username") String username, @Param("gameId") Long gameId);

    default Instant getLastPlayedAtGP(String username, Long gameId) {
        return getLastPlayedAtDBGP(username, gameId).orElse(Instant.EPOCH);
    }

    // UPDATE functions
    @Modifying
    @Query(
        "UPDATE GameProgress gp " +
            "SET gp.sessionsPlayed = :sessionsPlayed, gp.performance = :performance, gp.lastPlayedAt = :lastPlayedAt " +
            "WHERE gp.game.id = :gameId " +
            "AND gp.userProfile IN (" +
            "  SELECT up FROM UserProfile up JOIN up.dataUser u WHERE u.login = ?#{principal.username}" +
            ")"
    )
    void updateGameProgressToDb(
        @Param("gameId") Long gameId,
        @Param("sessionsPlayed") int sessionsPlayed,
        @Param("performance") Float performance,
        @Param("lastPlayedAt") Instant lastPlayedAt
    );
}
