package com.crystal.kip.repository.CustomRepoCode;

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
 * CustomTopicProgressRepositoryTest
 */
@IntegrationTest
@Transactional
public class CustomTopicProgressRepositoryTest {

    @Autowired
    private CustomTopicProgressRepository aCustomTopicProgressRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String TESTUSERNAME = "testUser";
    private static final String FAILUSERNAME = "failUser";
    private Long topicId;

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
        entityManager.flush();
        topicId = topic.getId();

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
        Optional<Float> currentCompetency = aCustomTopicProgressRepository.getCompetencyDBTP(TESTUSERNAME, topicId);
        assertTrue(currentCompetency.isPresent());
        assertEquals(0.25f, currentCompetency.get(), 0.0001);
    }

    @Test
    void getCurCompentencyTPTest() {
        double currentCompetency = aCustomTopicProgressRepository.getCurCompetencyTP(TESTUSERNAME, topicId);
        assertEquals(0.25d, currentCompetency, 0.0001);
    }

    @Test
    void getCurCompentencyTPTestFail() {
        double currentCompetency = aCustomTopicProgressRepository.getCurCompetencyTP(FAILUSERNAME, topicId);
        assertEquals(-1.0d, currentCompetency, 0.001);
    }

    //Get Improvement Functions
    @Test
    void getImprovementDBTPTest() {
        Optional<Float> improvement = aCustomTopicProgressRepository.getImprovementDBTP(TESTUSERNAME, topicId);
        assertTrue(improvement.isPresent());
        assertEquals(0.05f, improvement.get(), 0.0001);
    }

    @Test
    void getImprovementTPTest() {
        double improvement = aCustomTopicProgressRepository.getImprovementTP(TESTUSERNAME, topicId);
        assertEquals(0.05d, improvement, 0.0001);
    }

    @Test
    void getImprovementTPTestFail() {
        double improvement = aCustomTopicProgressRepository.getImprovementTP(FAILUSERNAME, topicId);
        assertEquals(-1.0d, improvement, 0.001);
    }

    //Get LastPraticedAt Functions
    @Test
    void getLastPracticedAtDBTPTest() {
        Optional<Instant> now = aCustomTopicProgressRepository.getLastPracticedAtDBTP(TESTUSERNAME, topicId);
        assertTrue(now.isPresent());
        assertEquals("2026-04-01T11:11:00Z", now.get().toString());
    }

    @Test
    void getLastPracticedAtTPTest() {
        Instant now = aCustomTopicProgressRepository.getLastPracticedAtTP(TESTUSERNAME, topicId);
        assertEquals("2026-04-01T11:11:00Z", now.toString());
    }

    @Test
    void getLastPracticedAtTPTestFail() {
        Instant now = aCustomTopicProgressRepository.getLastPracticedAtTP(FAILUSERNAME, topicId);
        assertEquals(Instant.EPOCH.toString(), now.toString());
    }

    //Get TopicNumber Functions
    @Test
    void getAllTopicNumTPDBTest() {
        Long numTopics = aCustomTopicProgressRepository.getAllTopicNumTPDB(topicId);
        assertEquals(2L, numTopics);
    }

    @Test
    void getAllTopicNumTPTest() {
        int numTopics = aCustomTopicProgressRepository.getAllTopicNumTP(topicId);
        assertEquals(2, numTopics);
    }

    @Test
    void getAllTopicNumTPTestFail() {
        int numTopics = aCustomTopicProgressRepository.getAllTopicNumTP(Long.MAX_VALUE);
        assertEquals(0, numTopics);
    }

    //Update Functions
    @Test
    @WithMockUser(username = TESTUSERNAME)
    void updateTopicProgressToDBTest() {
        Float competency = 0.6f;
        Float improvement = 0.2f;
        Instant now = Instant.parse("2026-04-01T12:00:00Z");

        aCustomTopicProgressRepository.updateTopicProgressToDB(topicId, competency, improvement, now);
        entityManager.clear();

        Optional<Float> newCompetency = aCustomTopicProgressRepository.getCompetencyDBTP(TESTUSERNAME, topicId);
        Optional<Float> newImprovement = aCustomTopicProgressRepository.getImprovementDBTP(TESTUSERNAME, topicId);
        Optional<Instant> newNow = aCustomTopicProgressRepository.getLastPracticedAtDBTP(TESTUSERNAME, topicId);
        assertTrue(newCompetency.isPresent());
        assertEquals(competency, newCompetency.get(), 0.001);
        assertEquals(improvement, newImprovement.get(), 0.01);
        assertEquals(now.toString(), newNow.get().toString());
    }
}
