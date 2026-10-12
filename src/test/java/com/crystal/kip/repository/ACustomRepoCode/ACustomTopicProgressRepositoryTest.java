package com.crystal.kip.repository.ACustomRepoCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Concept;
import com.crystal.kip.domain.ConceptProgress;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.TopicProgress;
import com.crystal.kip.domain.User;
import com.crystal.kip.domain.UserProfile;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * SHOULD HAVE DOCKER ENGINE ON TO TEST, can take 1-4 minutes to complete as it mimics a sql database
 * ACustomTopicProgressRepositoryTest
 */
@IntegrationTest
@Transactional
public class ACustomTopicProgressRepositoryTest {

    @Autowired
    private ACustomTopicProgressRepository aCustomTopicProgressRepository;

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

        Concept concept1 = new Concept();
        concept1.setName("fakeConcept1");
        entityManager.persist(concept1);

        Concept concept2 = new Concept();
        concept2.setName("fakeConcept2");
        entityManager.persist(concept2);

        Set<Concept> concepts = Set.of(concept1, concept2);

        Topic topic = new Topic();
        topic.setName("fakeTopic1");
        topic.setConcepts(concepts);
        entityManager.persist(topic);

        ConceptProgress conceptProgress = new ConceptProgress();
        conceptProgress.setUserProfile(userProfile);
        conceptProgress.setCompetency(0.5f);
        conceptProgress.setImprovement(0.10f);
        conceptProgress.setLastPracticedAt(Instant.parse("2026-04-01T11:11:00Z"));
        conceptProgress.setMaxQuestions(10);
        entityManager.persist(conceptProgress);

        TopicProgress topicProgress = new TopicProgress();
        topicProgress.setUserProfile(userProfile);
        topicProgress.setCompetency(0.25f);
        topicProgress.setImprovement(0.05f);
        topicProgress.setLastPracticedAt(Instant.parse("2026-04-01T11:11:00Z"));
        topicProgress.setTopic(topic);
        entityManager.persist(topicProgress);

        entityManager.flush();
    }

    //READ Functions
    //Get Competency Functions
    @Test
    void getCompetencyDBTPTest() {
        Optional<Float> currentCompetency = aCustomTopicProgressRepository.getCompetencyDBTP(TESTUSERNAME);
        assertTrue(currentCompetency.isPresent());
        assertEquals(0.25f, currentCompetency.get(), 0.0001);
    }

    @Test
    void getCurCompentencyTPTest() {
        double currentCompetency = aCustomTopicProgressRepository.getCurCompetencyTP(TESTUSERNAME);
        assertEquals(0.25d, currentCompetency, 0.0001);
    }

    @Test
    void getCurCompentencyTPTestFail() {
        double currentCompetency = aCustomTopicProgressRepository.getCurCompetencyTP(FAILUSERNAME);
        assertEquals(-1.0d, currentCompetency, 0.001);
    }

    //Get Improvement Functions
    @Test
    void getImprovementDBTPTest() {
        Optional<Float> improvement = aCustomTopicProgressRepository.getImprovementDBTP(TESTUSERNAME);
        assertTrue(improvement.isPresent());
        assertEquals(0.05f, improvement.get(), 0.0001);
    }

    @Test
    void getImprovementTPTest() {
        double improvement = aCustomTopicProgressRepository.getImprovementTP(TESTUSERNAME);
        assertEquals(0.05d, improvement, 0.0001);
    }

    @Test
    void getImprovementTPTestFail() {
        double improvement = aCustomTopicProgressRepository.getImprovementTP(FAILUSERNAME);
        assertEquals(-1.0d, improvement, 0.001);
    }

    //Get LastPraticedAt Functions
    @Test
    void getLastPracticedAtDBTPTest() {
        Optional<Instant> now = aCustomTopicProgressRepository.getLastPracticedAtDBTP(TESTUSERNAME);
        assertTrue(now.isPresent());
        assertEquals("2026-04-01T11:11:00Z", now.get().toString());
    }

    @Test
    void getLastPracticedAtTPTest() {
        Instant now = aCustomTopicProgressRepository.getLastPracticedAtTP(TESTUSERNAME);
        assertEquals("2026-04-01T11:11:00Z", now.toString());
    }

    @Test
    void getLastPracticedAtTPTestFail() {
        Instant now = aCustomTopicProgressRepository.getLastPracticedAtTP(FAILUSERNAME);
        assertEquals(Instant.EPOCH.toString(), now.toString());
    }

    //Get TopicNumber Functions
    @Test
    void getAllTopicNumTPDBTest() {
        Optional<Integer> numTopics = aCustomTopicProgressRepository.getAllTopicNumTPDB(TESTUSERNAME);
        assertTrue(numTopics.isPresent());
        assertEquals(2, numTopics.get());
    }

    @Test
    void getAllTopicNumTPTest() {
        int numTopics = aCustomTopicProgressRepository.getAllTopicNumTP(TESTUSERNAME);
        assertEquals(2, numTopics);
    }

    @Test
    void getAllTopicNumTPTestFail() {
        int numTopics = aCustomTopicProgressRepository.getAllTopicNumTP(FAILUSERNAME);
        assertEquals(-1, numTopics);
    }

    //Update Functions
    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateTopicProgressToDBTest() {
        Float competency = 0.6f;
        Float improvement = 0.2f;
        Instant now = Instant.parse("2026-04-01T12:00:00Z");

        aCustomTopicProgressRepository.updateTopicProgressToDB(competency, improvement, now);
        entityManager.clear();

        Optional<Float> newCompetency = aCustomTopicProgressRepository.getCompetencyDBTP(TESTUSERNAME);
        Optional<Float> newImprovement = aCustomTopicProgressRepository.getImprovementDBTP(TESTUSERNAME);
        Optional<Instant> newNow = aCustomTopicProgressRepository.getLastPracticedAtDBTP(TESTUSERNAME);
        assertTrue(newCompetency.isPresent());
        assertEquals(competency, newCompetency.get(), 0.001);
        assertEquals(improvement, newImprovement.get(), 0.01);
        assertEquals(now.toString(), newNow.get().toString());
    }
}
