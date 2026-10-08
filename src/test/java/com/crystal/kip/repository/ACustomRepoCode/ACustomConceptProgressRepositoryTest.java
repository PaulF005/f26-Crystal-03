package com.crystal.kip.repository.ACustomRepoCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
 * SHOULD HAVE DOCKER ENGINE ON TO TEST
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
        conceptProgress.setEvidenceCount(0);
        conceptProgress.setLastPracticedAt(Instant.parse("2026-04-01T11:11:00Z"));
        conceptProgress.setMaxQuestions(10);
        entityManager.persist(conceptProgress);

        entityManager.flush();
    }

    @Test
    void getCompantencyDBCPTest() {
        Optional<Float> competency = aCustomConceptProgressRepository.getCompantencyDBCP(TESTUSERNAME);
        assertEquals(0.3f, competency.orElseThrow(() -> new AssertionError("Record not found for user")), 0.001f);
    }
}
