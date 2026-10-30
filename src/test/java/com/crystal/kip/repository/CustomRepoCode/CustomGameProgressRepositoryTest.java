package com.crystal.kip.repository.CustomRepoCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.domain.User;
import com.crystal.kip.domain.UserProfile;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * SHOULD HAVE DOCKER ENGINE ON TO TEST, can take 1-4 minutes to complete as it mimics a sql database
 * CustomGameProgressRepositoryTest
 */
@IntegrationTest
@Transactional
public class CustomGameProgressRepositoryTest {

    @Autowired
    private CustomGameProgressRepository aCustomGameProgressRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String TESTUSERNAME = "testUser";
    private static final String FAILUSERNAME = "failUser";
    private Long gameId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setLogin(TESTUSERNAME);
        user.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
        user.setEmail("testUser@localhost.com");
        user.setActivated(true);
        entityManager.persist(user);

        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(TESTUSERNAME);
        userProfile.setEmail("testUser@localhost.com");
        userProfile.setDataUser(user);
        entityManager.persist(userProfile);

        Game game1 = new Game();
        game1.setName("Game1");
        entityManager.persist(game1);
        entityManager.flush();
        gameId = game1.getId();

        GameProgress gameProgress = new GameProgress();
        gameProgress.setUserProfile(userProfile);
        gameProgress.setSessionsPlayed(7);
        gameProgress.setPerformance(0.526f);
        gameProgress.setLastPlayedAt(Instant.parse("2026-04-01T11:11:00Z"));
        gameProgress.setGame(game1);
        entityManager.persist(gameProgress);

        entityManager.flush();
    }

    //READ Functions
    //Get Sessions Played Functions
    @Test
    public void getSessionsDBGPTest() {
        Optional<Integer> currentSessionsPlayed = aCustomGameProgressRepository.getSessionsDBGP(TESTUSERNAME, gameId);
        assertTrue(currentSessionsPlayed.isPresent());
        assertEquals(7, currentSessionsPlayed.get().intValue());
    }

    @Test
    public void getSessionsPlayedGPTest() {
        int sessionsPlayed = aCustomGameProgressRepository.getSessionsPlayedGP(TESTUSERNAME, gameId);
        assertEquals(7, sessionsPlayed);
    }

    @Test
    public void getSessionsPlayedGPTestFail() {
        int sessionsPlayed = aCustomGameProgressRepository.getSessionsPlayedGP(FAILUSERNAME, gameId);
        assertEquals(-1, sessionsPlayed);
    }

    //Get Performance Functions
    @Test
    void getgetPerformanceDBGPTest() {
        Optional<Float> currentPerformance = aCustomGameProgressRepository.getPerformanceDBGP(TESTUSERNAME, gameId);
        assertTrue(currentPerformance.isPresent());
        assertEquals(0.526f, currentPerformance.get(), 0.0001);
    }

    @Test
    void getPerformanceGPTest() {
        double currentPerformance = aCustomGameProgressRepository.getPerformanceGP(TESTUSERNAME, gameId);
        assertEquals(0.526d, currentPerformance, 0.0001);
    }

    @Test
    void getPerformanceGPTestFail() {
        double currentPerformance = aCustomGameProgressRepository.getPerformanceGP(FAILUSERNAME, gameId);
        assertEquals(-1.0d, currentPerformance, 0.0001);
    }

    //Get LastPraticedAt Functions
    @Test
    void getLastPlayedAtDBGPTest() {
        Optional<Instant> now = aCustomGameProgressRepository.getLastPlayedAtDBGP(TESTUSERNAME, gameId);
        assertTrue(now.isPresent());
        assertEquals("2026-04-01T11:11:00Z", now.get().toString());
    }

    @Test
    void getLastPlayedAtGPTest() {
        Instant now = aCustomGameProgressRepository.getLastPlayedAtGP(TESTUSERNAME, gameId);
        assertEquals("2026-04-01T11:11:00Z", now.toString());
    }

    @Test
    void getLastPlayedAtGPTestFail() {
        Instant now = aCustomGameProgressRepository.getLastPlayedAtGP(FAILUSERNAME, gameId);
        assertEquals(Instant.EPOCH.toString(), now.toString());
    }

    //Update Functions
    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateGameProgressToDBTest() {
        int sessionsPlayed = 8;
        Float performance = 0.6f;
        Instant now = Instant.parse("2026-04-01T12:00:00Z");

        aCustomGameProgressRepository.updateGameProgressToDb(gameId, sessionsPlayed, performance, now);
        entityManager.clear();

        Optional<Integer> newSessionsPlayed = aCustomGameProgressRepository.getSessionsDBGP(TESTUSERNAME, gameId);
        Optional<Float> newPerformance = aCustomGameProgressRepository.getPerformanceDBGP(TESTUSERNAME, gameId);
        Optional<Instant> newNow = aCustomGameProgressRepository.getLastPlayedAtDBGP(TESTUSERNAME, gameId);
        assertEquals(sessionsPlayed, newSessionsPlayed.get());
        assertEquals(performance, newPerformance.get(), 0.01);
        assertEquals(now.toString(), newNow.get().toString());
    }
}
