package com.crystal.kip.repository.ACustomRepoCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.ConceptProgress;
import com.crystal.kip.domain.User;
import com.crystal.kip.domain.UserProfile;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * SHOULD HAVE DOCKER ENGINE ON TO TEST, can take 1-4 minutes to complete as it mimics a sql database
 * ACustomConceptProgressRepositoryTest
 */
@IntegrationTest
@Transactional
public class ACustomConceptProgressRepositoryTest {

    @Autowired
    private ACustomConceptProgressRepository aCustomConceptProgressRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String TESTUSERNAME = "testUser";
    private static final String FAILUSERNAME = "failUser";

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

        ConceptProgress conceptProgress = new ConceptProgress();
        conceptProgress.setUserProfile(userProfile);
        conceptProgress.setCompetency(0.3f);
        conceptProgress.setImprovement(0.10f);
        conceptProgress.setLastPracticedAt(Instant.parse("2026-04-01T11:11:00Z"));
        conceptProgress.setMaxQuestions(10);
        entityManager.persist(conceptProgress);

        entityManager.flush();
    }

    //READ Functions
    //Get Competency Functions
    @Test
    void getCompetencyDBCPTest() {
        Optional<Float> currentCompetency = aCustomConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME);
        assertTrue(currentCompetency.isPresent());
        assertEquals(0.3f, currentCompetency.get(), 0.001);
    }

    @Test
    void getCurCompentencyCPTest() {
        double currentCompetency = aCustomConceptProgressRepository.getCurCompetencyCP(TESTUSERNAME);
        assertEquals(0.3d, currentCompetency, 0.001);
    }

    @Test
    void getCurCompentencyCPTestFail() {
        double currentCompetency = aCustomConceptProgressRepository.getCurCompetencyCP(FAILUSERNAME);
        assertEquals(-1.0d, currentCompetency, 0.001);
    }

    //Get Max Questions Functions
    @Test
    void getmaxQuestionsDBTest() {
        Optional<Integer> maxQuestions = aCustomConceptProgressRepository.getmaxQuestionsDBCP(TESTUSERNAME);
        assertTrue(maxQuestions.isPresent());
        assertEquals(10, maxQuestions.get());
    }

    @Test
    void getMaxQuestionsForRepoTest() {
        int maxQuestions = aCustomConceptProgressRepository.getMaxQuestionsForRepo(TESTUSERNAME);
        assertEquals(10, maxQuestions);
    }

    @Test
    void getMaxQuestionsForRepoTestFail() {
        double maxQuestions = aCustomConceptProgressRepository.getMaxQuestionsForRepo(FAILUSERNAME);
        assertEquals(-1, maxQuestions);
    }

    //Get Improvement Functions
    @Test
    void getImprovementDBCPTest() {
        Optional<Float> improvement = aCustomConceptProgressRepository.getImprovementDBCP(TESTUSERNAME);
        assertTrue(improvement.isPresent());
        assertEquals(0.1f, improvement.get(), 0.001);
    }

    @Test
    void getImprovementTest() {
        double improvement = aCustomConceptProgressRepository.getImprovementCP(TESTUSERNAME);
        assertEquals(0.1d, improvement, 0.001);
    }

    @Test
    void getImprovementTestFail() {
        double improvement = aCustomConceptProgressRepository.getImprovementCP(FAILUSERNAME);
        assertEquals(-1.0d, improvement, 0.001);
    }

    //Get LastPraticedAt Functions
    @Test
    void getLastPracticedAtDBCPTest() {
        Optional<Instant> now = aCustomConceptProgressRepository.getLastPracticedAtDBCP(TESTUSERNAME);
        assertTrue(now.isPresent());
        assertEquals("2026-04-01T11:11:00Z", now.get().toString());
    }

    @Test
    void getLastPracticedAtCPTest() {
        Instant now = aCustomConceptProgressRepository.getLastPracticedAtCP(TESTUSERNAME);
        assertEquals("2026-04-01T11:11:00Z", now.toString());
    }

    @Test
    void getLastPracticedAtCPFail() {
        Instant now = aCustomConceptProgressRepository.getLastPracticedAtCP(FAILUSERNAME);
        assertEquals(Instant.EPOCH.toString(), now.toString());
    }

    //Update Functions
    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateConceptProgressToDBTest() {
        Float competency = 0.6f;
        Float improvement = 0.2f;
        Instant now = Instant.parse("2026-04-01T12:00:00Z");

        aCustomConceptProgressRepository.updateConceptProgressToDBCP(competency, improvement, now);
        entityManager.clear();

        Optional<Float> newCompetency = aCustomConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME);
        Optional<Float> newImprovement = aCustomConceptProgressRepository.getImprovementDBCP(TESTUSERNAME);
        Optional<Instant> newNow = aCustomConceptProgressRepository.getLastPracticedAtDBCP(TESTUSERNAME);
        assertTrue(newCompetency.isPresent());
        assertEquals(competency, newCompetency.get(), 0.001);
        assertEquals(improvement, newImprovement.get(), 0.01);
        assertEquals(now.toString(), newNow.get().toString());
    }
}
