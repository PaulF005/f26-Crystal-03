package com.crystal.kip.repository.CustomRepoCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Concept;
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
 * CustomConceptProgressRepositoryTest
 */
@IntegrationTest
@Transactional
public class CustomConceptProgressRepositoryTest {

    @Autowired
    private CustomConceptProgressRepository customConceptProgressRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String TESTUSERNAME = "testUser";
    private static final String FAILUSERNAME = "failUser";

    private Long conceptOneId;
    private Long conceptTwoId;

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

        Concept conceptOne = new Concept();
        conceptOne.setName("fakeConcept1");
        entityManager.persist(conceptOne);

        Concept conceptTwo = new Concept();
        conceptTwo.setName("fakeConcept2");
        entityManager.persist(conceptTwo);

        entityManager.flush();
        conceptOneId = conceptOne.getId();
        conceptTwoId = conceptTwo.getId();

        ConceptProgress conceptProgressOne = new ConceptProgress();
        conceptProgressOne.setUserProfile(userProfile);
        conceptProgressOne.setConcept(conceptOne);
        conceptProgressOne.setCompetency(0.3f);
        conceptProgressOne.setImprovement(0.10f);
        conceptProgressOne.setLastPracticedAt(Instant.parse("2026-04-01T11:11:00Z"));
        conceptProgressOne.setMaxQuestions(10);
        entityManager.persist(conceptProgressOne);

        ConceptProgress conceptProgressTwo = new ConceptProgress();
        conceptProgressTwo.setUserProfile(userProfile);
        conceptProgressTwo.setConcept(conceptTwo);
        conceptProgressTwo.setCompetency(0.7f);
        conceptProgressTwo.setImprovement(0.20f);
        conceptProgressTwo.setLastPracticedAt(Instant.parse("2026-04-02T11:11:00Z"));
        conceptProgressTwo.setMaxQuestions(20);
        entityManager.persist(conceptProgressTwo);

        entityManager.flush();
    }

    // READ Functions
    // Get Competency Functions
    @Test
    void getCompetencyDBCPTest() {
        Optional<Float> currentCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptOneId);
        assertTrue(currentCompetency.isPresent());
        assertEquals(0.3f, currentCompetency.get(), 0.001);
    }

    @Test
    void getCompetencyDBCPSelectsSpecificConcept() {
        Optional<Float> conceptOneCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptOneId);
        Optional<Float> conceptTwoCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptTwoId);

        assertTrue(conceptOneCompetency.isPresent());
        assertTrue(conceptTwoCompetency.isPresent());
        assertEquals(0.3f, conceptOneCompetency.get(), 0.001);
        assertEquals(0.7f, conceptTwoCompetency.get(), 0.001);
    }

    @Test
    void getCurCompentencyCPTest() {
        double currentCompetency = customConceptProgressRepository.getCurCompetencyCP(TESTUSERNAME, conceptOneId);
        assertEquals(0.3d, currentCompetency, 0.001);
    }

    @Test
    void getCurCompentencyCPTestFailUser() {
        double currentCompetency = customConceptProgressRepository.getCurCompetencyCP(FAILUSERNAME, conceptOneId);
        assertEquals(-1.0d, currentCompetency, 0.001);
    }

    @Test
    void getCurCompentencyCPTestFailConcept() {
        double currentCompetency = customConceptProgressRepository.getCurCompetencyCP(TESTUSERNAME, Long.MAX_VALUE);
        assertEquals(-1.0d, currentCompetency, 0.001);
    }

    // Get Max Questions Functions
    @Test
    void getMaxQuestionsDBTest() {
        Optional<Integer> maxQuestions = customConceptProgressRepository.getMaxQuestionsDBCP(TESTUSERNAME, conceptOneId);
        assertTrue(maxQuestions.isPresent());
        assertEquals(10, maxQuestions.get());
    }

    @Test
    void getMaxQuestionsForRepoTest() {
        int maxQuestions = customConceptProgressRepository.getMaxQuestionsForRepo(TESTUSERNAME, conceptOneId);
        assertEquals(10, maxQuestions);
    }

    @Test
    void getMaxQuestionsForRepoTestFail() {
        int maxQuestions = customConceptProgressRepository.getMaxQuestionsForRepo(FAILUSERNAME, conceptOneId);
        assertEquals(-1, maxQuestions);
    }

    // Get Improvement Functions
    @Test
    void getImprovementDBCPTest() {
        Optional<Float> improvement = customConceptProgressRepository.getImprovementDBCP(TESTUSERNAME, conceptOneId);
        assertTrue(improvement.isPresent());
        assertEquals(0.1f, improvement.get(), 0.001);
    }

    @Test
    void getImprovementTest() {
        double improvement = customConceptProgressRepository.getImprovementCP(TESTUSERNAME, conceptOneId);
        assertEquals(0.1d, improvement, 0.001);
    }

    @Test
    void getImprovementTestFail() {
        double improvement = customConceptProgressRepository.getImprovementCP(FAILUSERNAME, conceptOneId);
        assertEquals(-1.0d, improvement, 0.001);
    }

    // Get LastPracticedAt Functions
    @Test
    void getLastPracticedAtDBCPTest() {
        Optional<Instant> now = customConceptProgressRepository.getLastPracticedAtDBCP(TESTUSERNAME, conceptOneId);
        assertTrue(now.isPresent());
        assertEquals("2026-04-01T11:11:00Z", now.get().toString());
    }

    @Test
    void getLastPracticedAtCPTest() {
        Instant now = customConceptProgressRepository.getLastPracticedAtCP(TESTUSERNAME, conceptOneId);
        assertEquals("2026-04-01T11:11:00Z", now.toString());
    }

    @Test
    void getLastPracticedAtCPFail() {
        Instant now = customConceptProgressRepository.getLastPracticedAtCP(FAILUSERNAME, conceptOneId);
        assertEquals(Instant.EPOCH.toString(), now.toString());
    }

    // Update Functions
    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateConceptProgressToDBTest() {
        Float competency = 0.6f;
        Float improvement = 0.2f;
        Instant now = Instant.parse("2026-04-01T12:00:00Z");

        customConceptProgressRepository.updateConceptProgressToDBCP(conceptOneId, competency, improvement, now);
        entityManager.clear();

        Optional<Float> newCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptOneId);
        Optional<Float> newImprovement = customConceptProgressRepository.getImprovementDBCP(TESTUSERNAME, conceptOneId);
        Optional<Instant> newNow = customConceptProgressRepository.getLastPracticedAtDBCP(TESTUSERNAME, conceptOneId);

        assertTrue(newCompetency.isPresent());
        assertEquals(competency, newCompetency.get(), 0.001);
        assertEquals(improvement, newImprovement.get(), 0.01);
        assertEquals(now.toString(), newNow.get().toString());
    }

    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateConceptProgressToDBOnlyUpdatesRequestedConcept() {
        customConceptProgressRepository.updateConceptProgressToDBCP(conceptOneId, 0.9f, 0.4f, Instant.parse("2026-04-03T12:00:00Z"));
        entityManager.clear();

        Optional<Float> conceptOneCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptOneId);
        Optional<Float> conceptTwoCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, conceptTwoId);
        Optional<Float> conceptTwoImprovement = customConceptProgressRepository.getImprovementDBCP(TESTUSERNAME, conceptTwoId);
        Optional<Instant> conceptTwoLastPracticed = customConceptProgressRepository.getLastPracticedAtDBCP(TESTUSERNAME, conceptTwoId);

        assertTrue(conceptOneCompetency.isPresent());
        assertEquals(0.9f, conceptOneCompetency.get(), 0.001);

        assertTrue(conceptTwoCompetency.isPresent());
        assertEquals(0.7f, conceptTwoCompetency.get(), 0.001);
        assertEquals(0.2f, conceptTwoImprovement.orElseThrow(), 0.001);
        assertEquals("2026-04-02T11:11:00Z", conceptTwoLastPracticed.orElseThrow().toString());
    }

    @Test
    void getCompetencyDBCPReturnsEmptyForUnknownConcept() {
        Optional<Float> currentCompetency = customConceptProgressRepository.getCompetencyDBCP(TESTUSERNAME, Long.MAX_VALUE);
        assertFalse(currentCompetency.isPresent());
    }
}
