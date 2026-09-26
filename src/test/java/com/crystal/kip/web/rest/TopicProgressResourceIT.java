package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.TopicProgressAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.TopicProgress;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.repository.TopicProgressRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link TopicProgressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TopicProgressResourceIT {

    private static final Float DEFAULT_COMPETENCY = 0F;
    private static final Float UPDATED_COMPETENCY = 1F;

    private static final Float DEFAULT_IMPROVEMENT = 1F;
    private static final Float UPDATED_IMPROVEMENT = 2F;

    private static final Integer DEFAULT_EVIDENCE_COUNT = 0;
    private static final Integer UPDATED_EVIDENCE_COUNT = 1;

    private static final Instant DEFAULT_LAST_PRACTICED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_PRACTICED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final String ENTITY_API_URL = "/api/topic-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TopicProgressRepository topicProgressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTopicProgressMockMvc;

    private TopicProgress topicProgress;

    private TopicProgress insertedTopicProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TopicProgress createEntity(EntityManager em) {
        TopicProgress topicProgress = new TopicProgress()
            .competency(DEFAULT_COMPETENCY)
            .improvement(DEFAULT_IMPROVEMENT)
            .evidenceCount(DEFAULT_EVIDENCE_COUNT)
            .lastPracticedAt(DEFAULT_LAST_PRACTICED_AT);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createEntity();
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        topicProgress.setTopic(topic);
        // Add required entity
        UserProfile userProfile;
        if (TestUtil.findAll(em, UserProfile.class).isEmpty()) {
            userProfile = UserProfileResourceIT.createEntity();
            em.persist(userProfile);
            em.flush();
        } else {
            userProfile = TestUtil.findAll(em, UserProfile.class).get(0);
        }
        topicProgress.setUserProfile(userProfile);
        return topicProgress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TopicProgress createUpdatedEntity(EntityManager em) {
        TopicProgress updatedTopicProgress = new TopicProgress()
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createUpdatedEntity();
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        updatedTopicProgress.setTopic(topic);
        // Add required entity
        UserProfile userProfile;
        if (TestUtil.findAll(em, UserProfile.class).isEmpty()) {
            userProfile = UserProfileResourceIT.createUpdatedEntity();
            em.persist(userProfile);
            em.flush();
        } else {
            userProfile = TestUtil.findAll(em, UserProfile.class).get(0);
        }
        updatedTopicProgress.setUserProfile(userProfile);
        return updatedTopicProgress;
    }

    @BeforeEach
    void initTest() {
        topicProgress = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTopicProgress != null) {
            topicProgressRepository.delete(insertedTopicProgress);
            insertedTopicProgress = null;
        }
    }

    @Test
    @Transactional
    void createTopicProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TopicProgress
        var returnedTopicProgress = om.readValue(
            restTopicProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TopicProgress.class
        );

        // Validate the TopicProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTopicProgressUpdatableFieldsEquals(returnedTopicProgress, getPersistedTopicProgress(returnedTopicProgress));

        insertedTopicProgress = returnedTopicProgress;
    }

    @Test
    @Transactional
    void createTopicProgressWithExistingId() throws Exception {
        // Create the TopicProgress with an existing ID
        topicProgress.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTopicProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isBadRequest());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCompetencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        topicProgress.setCompetency(null);

        // Create the TopicProgress, which fails.

        restTopicProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImprovementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        topicProgress.setImprovement(null);

        // Create the TopicProgress, which fails.

        restTopicProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEvidenceCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        topicProgress.setEvidenceCount(null);

        // Create the TopicProgress, which fails.

        restTopicProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastPracticedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        topicProgress.setLastPracticedAt(null);

        // Create the TopicProgress, which fails.

        restTopicProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTopicProgresses() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        // Get all the topicProgressList
        restTopicProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(topicProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].competency").value(hasItem(DEFAULT_COMPETENCY.doubleValue())))
            .andExpect(jsonPath("$.[*].improvement").value(hasItem(DEFAULT_IMPROVEMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].evidenceCount").value(hasItem(DEFAULT_EVIDENCE_COUNT)))
            .andExpect(jsonPath("$.[*].lastPracticedAt").value(hasItem(DEFAULT_LAST_PRACTICED_AT.toString())));
    }

    @Test
    @Transactional
    void getTopicProgress() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        // Get the topicProgress
        restTopicProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, topicProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(topicProgress.getId().intValue()))
            .andExpect(jsonPath("$.competency").value(DEFAULT_COMPETENCY.doubleValue()))
            .andExpect(jsonPath("$.improvement").value(DEFAULT_IMPROVEMENT.doubleValue()))
            .andExpect(jsonPath("$.evidenceCount").value(DEFAULT_EVIDENCE_COUNT))
            .andExpect(jsonPath("$.lastPracticedAt").value(DEFAULT_LAST_PRACTICED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTopicProgress() throws Exception {
        // Get the topicProgress
        restTopicProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTopicProgress() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the topicProgress
        TopicProgress updatedTopicProgress = topicProgressRepository.findById(topicProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTopicProgress are not directly saved in db
        em.detach(updatedTopicProgress);
        updatedTopicProgress
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);

        restTopicProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTopicProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTopicProgress))
            )
            .andExpect(status().isOk());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTopicProgressToMatchAllProperties(updatedTopicProgress);
    }

    @Test
    @Transactional
    void putNonExistingTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, topicProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(topicProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(topicProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTopicProgressWithPatch() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the topicProgress using partial update
        TopicProgress partialUpdatedTopicProgress = new TopicProgress();
        partialUpdatedTopicProgress.setId(topicProgress.getId());

        restTopicProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTopicProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTopicProgress))
            )
            .andExpect(status().isOk());

        // Validate the TopicProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTopicProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTopicProgress, topicProgress),
            getPersistedTopicProgress(topicProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateTopicProgressWithPatch() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the topicProgress using partial update
        TopicProgress partialUpdatedTopicProgress = new TopicProgress();
        partialUpdatedTopicProgress.setId(topicProgress.getId());

        partialUpdatedTopicProgress
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);

        restTopicProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTopicProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTopicProgress))
            )
            .andExpect(status().isOk());

        // Validate the TopicProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTopicProgressUpdatableFieldsEquals(partialUpdatedTopicProgress, getPersistedTopicProgress(partialUpdatedTopicProgress));
    }

    @Test
    @Transactional
    void patchNonExistingTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, topicProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(topicProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(topicProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTopicProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        topicProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(topicProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TopicProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTopicProgress() throws Exception {
        // Initialize the database
        insertedTopicProgress = topicProgressRepository.saveAndFlush(topicProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the topicProgress
        restTopicProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, topicProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return topicProgressRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TopicProgress getPersistedTopicProgress(TopicProgress topicProgress) {
        return topicProgressRepository.findById(topicProgress.getId()).orElseThrow();
    }

    protected void assertPersistedTopicProgressToMatchAllProperties(TopicProgress expectedTopicProgress) {
        assertTopicProgressAllPropertiesEquals(expectedTopicProgress, getPersistedTopicProgress(expectedTopicProgress));
    }

    protected void assertPersistedTopicProgressToMatchUpdatableProperties(TopicProgress expectedTopicProgress) {
        assertTopicProgressAllUpdatablePropertiesEquals(expectedTopicProgress, getPersistedTopicProgress(expectedTopicProgress));
    }
}
