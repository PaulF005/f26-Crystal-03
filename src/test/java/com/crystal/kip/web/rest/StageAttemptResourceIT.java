package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.StageAttemptAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.GameSession;
import com.crystal.kip.domain.StageAttempt;
import com.crystal.kip.repository.StageAttemptRepository;
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
 * Integration tests for the {@link StageAttemptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StageAttemptResourceIT {

    private static final Instant DEFAULT_ANSWERED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ANSWERED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final Boolean DEFAULT_CORRECT = false;
    private static final Boolean UPDATED_CORRECT = true;

    private static final String ENTITY_API_URL = "/api/stage-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StageAttemptRepository stageAttemptRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStageAttemptMockMvc;

    private StageAttempt stageAttempt;

    private StageAttempt insertedStageAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StageAttempt createEntity(EntityManager em) {
        StageAttempt stageAttempt = new StageAttempt().answeredAt(DEFAULT_ANSWERED_AT).correct(DEFAULT_CORRECT);
        // Add required entity
        GameSession gameSession;
        if (TestUtil.findAll(em, GameSession.class).isEmpty()) {
            gameSession = GameSessionResourceIT.createEntity();
            em.persist(gameSession);
            em.flush();
        } else {
            gameSession = TestUtil.findAll(em, GameSession.class).get(0);
        }
        stageAttempt.setGameSession(gameSession);
        return stageAttempt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StageAttempt createUpdatedEntity(EntityManager em) {
        StageAttempt updatedStageAttempt = new StageAttempt().answeredAt(UPDATED_ANSWERED_AT).correct(UPDATED_CORRECT);
        // Add required entity
        GameSession gameSession;
        if (TestUtil.findAll(em, GameSession.class).isEmpty()) {
            gameSession = GameSessionResourceIT.createUpdatedEntity();
            em.persist(gameSession);
            em.flush();
        } else {
            gameSession = TestUtil.findAll(em, GameSession.class).get(0);
        }
        updatedStageAttempt.setGameSession(gameSession);
        return updatedStageAttempt;
    }

    @BeforeEach
    void initTest() {
        stageAttempt = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStageAttempt != null) {
            stageAttemptRepository.delete(insertedStageAttempt);
            insertedStageAttempt = null;
        }
    }

    @Test
    @Transactional
    void createStageAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StageAttempt
        var returnedStageAttempt = om.readValue(
            restStageAttemptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stageAttempt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StageAttempt.class
        );

        // Validate the StageAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStageAttemptUpdatableFieldsEquals(returnedStageAttempt, getPersistedStageAttempt(returnedStageAttempt));

        insertedStageAttempt = returnedStageAttempt;
    }

    @Test
    @Transactional
    void createStageAttemptWithExistingId() throws Exception {
        // Create the StageAttempt with an existing ID
        stageAttempt.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStageAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stageAttempt)))
            .andExpect(status().isBadRequest());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAnsweredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stageAttempt.setAnsweredAt(null);

        // Create the StageAttempt, which fails.

        restStageAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stageAttempt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCorrectIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stageAttempt.setCorrect(null);

        // Create the StageAttempt, which fails.

        restStageAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stageAttempt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStageAttempts() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        // Get all the stageAttemptList
        restStageAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stageAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].answeredAt").value(hasItem(DEFAULT_ANSWERED_AT.toString())))
            .andExpect(jsonPath("$.[*].correct").value(hasItem(DEFAULT_CORRECT)));
    }

    @Test
    @Transactional
    void getStageAttempt() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        // Get the stageAttempt
        restStageAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, stageAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stageAttempt.getId().intValue()))
            .andExpect(jsonPath("$.answeredAt").value(DEFAULT_ANSWERED_AT.toString()))
            .andExpect(jsonPath("$.correct").value(DEFAULT_CORRECT));
    }

    @Test
    @Transactional
    void getNonExistingStageAttempt() throws Exception {
        // Get the stageAttempt
        restStageAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStageAttempt() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stageAttempt
        StageAttempt updatedStageAttempt = stageAttemptRepository.findById(stageAttempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStageAttempt are not directly saved in db
        em.detach(updatedStageAttempt);
        updatedStageAttempt.answeredAt(UPDATED_ANSWERED_AT).correct(UPDATED_CORRECT);

        restStageAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStageAttempt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStageAttempt))
            )
            .andExpect(status().isOk());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStageAttemptToMatchAllProperties(updatedStageAttempt);
    }

    @Test
    @Transactional
    void putNonExistingStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stageAttempt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stageAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stageAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stageAttempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStageAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stageAttempt using partial update
        StageAttempt partialUpdatedStageAttempt = new StageAttempt();
        partialUpdatedStageAttempt.setId(stageAttempt.getId());

        partialUpdatedStageAttempt.answeredAt(UPDATED_ANSWERED_AT);

        restStageAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStageAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStageAttempt))
            )
            .andExpect(status().isOk());

        // Validate the StageAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStageAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStageAttempt, stageAttempt),
            getPersistedStageAttempt(stageAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateStageAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stageAttempt using partial update
        StageAttempt partialUpdatedStageAttempt = new StageAttempt();
        partialUpdatedStageAttempt.setId(stageAttempt.getId());

        partialUpdatedStageAttempt.answeredAt(UPDATED_ANSWERED_AT).correct(UPDATED_CORRECT);

        restStageAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStageAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStageAttempt))
            )
            .andExpect(status().isOk());

        // Validate the StageAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStageAttemptUpdatableFieldsEquals(partialUpdatedStageAttempt, getPersistedStageAttempt(partialUpdatedStageAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stageAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stageAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stageAttempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStageAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stageAttempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageAttemptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stageAttempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StageAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStageAttempt() throws Exception {
        // Initialize the database
        insertedStageAttempt = stageAttemptRepository.saveAndFlush(stageAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stageAttempt
        restStageAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, stageAttempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stageAttemptRepository.count();
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

    protected StageAttempt getPersistedStageAttempt(StageAttempt stageAttempt) {
        return stageAttemptRepository.findById(stageAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedStageAttemptToMatchAllProperties(StageAttempt expectedStageAttempt) {
        assertStageAttemptAllPropertiesEquals(expectedStageAttempt, getPersistedStageAttempt(expectedStageAttempt));
    }

    protected void assertPersistedStageAttemptToMatchUpdatableProperties(StageAttempt expectedStageAttempt) {
        assertStageAttemptAllUpdatablePropertiesEquals(expectedStageAttempt, getPersistedStageAttempt(expectedStageAttempt));
    }
}
