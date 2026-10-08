package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.GameProgressAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.repository.GameProgressRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Integration tests for the {@link GameProgressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GameProgressResourceIT {

    private static final Integer DEFAULT_SESSIONS_PLAYED = 0;
    private static final Integer UPDATED_SESSIONS_PLAYED = 1;

    private static final Float DEFAULT_PERFORMANCE = 0F;
    private static final Float UPDATED_PERFORMANCE = 1F;

    private static final Integer DEFAULT_EVIDENCE_COUNT = 0;
    private static final Integer UPDATED_EVIDENCE_COUNT = 1;

    private static final Instant DEFAULT_LAST_PLAYED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_PLAYED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final String ENTITY_API_URL = "/api/game-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GameProgressRepository gameProgressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGameProgressMockMvc;

    private GameProgress gameProgress;

    private GameProgress insertedGameProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameProgress createEntity(EntityManager em) {
        GameProgress gameProgress = new GameProgress()
            .sessionsPlayed(DEFAULT_SESSIONS_PLAYED)
            .performance(DEFAULT_PERFORMANCE)
            .evidenceCount(DEFAULT_EVIDENCE_COUNT)
            .lastPlayedAt(DEFAULT_LAST_PLAYED_AT);
        // Add required entity
        Game game;
        if (TestUtil.findAll(em, Game.class).isEmpty()) {
            game = GameResourceIT.createEntity();
            em.persist(game);
            em.flush();
        } else {
            game = TestUtil.findAll(em, Game.class).get(0);
        }
        gameProgress.setGame(game);
        // Add required entity
        UserProfile userProfile;
        if (TestUtil.findAll(em, UserProfile.class).isEmpty()) {
            userProfile = UserProfileResourceIT.createEntity();
            em.persist(userProfile);
            em.flush();
        } else {
            userProfile = TestUtil.findAll(em, UserProfile.class).get(0);
        }
        gameProgress.setUserProfile(userProfile);
        return gameProgress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameProgress createUpdatedEntity(EntityManager em) {
        GameProgress updatedGameProgress = new GameProgress()
            .sessionsPlayed(UPDATED_SESSIONS_PLAYED)
            .performance(UPDATED_PERFORMANCE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPlayedAt(UPDATED_LAST_PLAYED_AT);
        // Add required entity
        Game game;
        if (TestUtil.findAll(em, Game.class).isEmpty()) {
            game = GameResourceIT.createUpdatedEntity();
            em.persist(game);
            em.flush();
        } else {
            game = TestUtil.findAll(em, Game.class).get(0);
        }
        updatedGameProgress.setGame(game);
        // Add required entity
        UserProfile userProfile;
        if (TestUtil.findAll(em, UserProfile.class).isEmpty()) {
            userProfile = UserProfileResourceIT.createUpdatedEntity();
            em.persist(userProfile);
            em.flush();
        } else {
            userProfile = TestUtil.findAll(em, UserProfile.class).get(0);
        }
        updatedGameProgress.setUserProfile(userProfile);
        return updatedGameProgress;
    }

    @BeforeEach
    void initTest() {
        gameProgress = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedGameProgress != null) {
            gameProgressRepository.delete(insertedGameProgress);
            insertedGameProgress = null;
        }
    }

    @Test
    @Transactional
    void createGameProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GameProgress
        var returnedGameProgress = om.readValue(
            restGameProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GameProgress.class
        );

        // Validate the GameProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGameProgressUpdatableFieldsEquals(returnedGameProgress, getPersistedGameProgress(returnedGameProgress));

        insertedGameProgress = returnedGameProgress;
    }

    @Test
    @Transactional
    void createGameProgressWithExistingId() throws Exception {
        // Create the GameProgress with an existing ID
        gameProgress.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGameProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isBadRequest());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSessionsPlayedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameProgress.setSessionsPlayed(null);

        // Create the GameProgress, which fails.

        restGameProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPerformanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameProgress.setPerformance(null);

        // Create the GameProgress, which fails.

        restGameProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEvidenceCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameProgress.setEvidenceCount(null);

        // Create the GameProgress, which fails.

        restGameProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastPlayedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameProgress.setLastPlayedAt(null);

        // Create the GameProgress, which fails.

        restGameProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGameProgresses() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        // Get all the gameProgressList
        restGameProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gameProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionsPlayed").value(hasItem(DEFAULT_SESSIONS_PLAYED)))
            .andExpect(jsonPath("$.[*].performance").value(hasItem(DEFAULT_PERFORMANCE.doubleValue())))
            .andExpect(jsonPath("$.[*].evidenceCount").value(hasItem(DEFAULT_EVIDENCE_COUNT)))
            .andExpect(jsonPath("$.[*].lastPlayedAt").value(hasItem(DEFAULT_LAST_PLAYED_AT.toString())));
    }

    @Test
    @Transactional
    void getGameProgress() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        // Get the gameProgress
        restGameProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, gameProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gameProgress.getId().intValue()))
            .andExpect(jsonPath("$.sessionsPlayed").value(DEFAULT_SESSIONS_PLAYED))
            .andExpect(jsonPath("$.performance").value(DEFAULT_PERFORMANCE.doubleValue()))
            .andExpect(jsonPath("$.evidenceCount").value(DEFAULT_EVIDENCE_COUNT))
            .andExpect(jsonPath("$.lastPlayedAt").value(DEFAULT_LAST_PLAYED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGameProgress() throws Exception {
        // Get the gameProgress
        restGameProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGameProgress() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameProgress
        GameProgress updatedGameProgress = gameProgressRepository.findById(gameProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGameProgress are not directly saved in db
        em.detach(updatedGameProgress);
        updatedGameProgress
            .sessionsPlayed(UPDATED_SESSIONS_PLAYED)
            .performance(UPDATED_PERFORMANCE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPlayedAt(UPDATED_LAST_PLAYED_AT);

        restGameProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGameProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedGameProgress))
            )
            .andExpect(status().isOk());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGameProgressToMatchAllProperties(updatedGameProgress);
    }

    @Test
    @Transactional
    void putNonExistingGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gameProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gameProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gameProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGameProgressWithPatch() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameProgress using partial update
        GameProgress partialUpdatedGameProgress = new GameProgress();
        partialUpdatedGameProgress.setId(gameProgress.getId());

        partialUpdatedGameProgress
            .sessionsPlayed(UPDATED_SESSIONS_PLAYED)
            .performance(UPDATED_PERFORMANCE)
            .lastPlayedAt(UPDATED_LAST_PLAYED_AT);

        restGameProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGameProgress))
            )
            .andExpect(status().isOk());

        // Validate the GameProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGameProgress, gameProgress),
            getPersistedGameProgress(gameProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateGameProgressWithPatch() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameProgress using partial update
        GameProgress partialUpdatedGameProgress = new GameProgress();
        partialUpdatedGameProgress.setId(gameProgress.getId());

        partialUpdatedGameProgress
            .sessionsPlayed(UPDATED_SESSIONS_PLAYED)
            .performance(UPDATED_PERFORMANCE)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPlayedAt(UPDATED_LAST_PLAYED_AT);

        restGameProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGameProgress))
            )
            .andExpect(status().isOk());

        // Validate the GameProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameProgressUpdatableFieldsEquals(partialUpdatedGameProgress, getPersistedGameProgress(partialUpdatedGameProgress));
    }

    @Test
    @Transactional
    void patchNonExistingGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gameProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gameProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gameProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGameProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gameProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGameProgress() throws Exception {
        // Initialize the database
        insertedGameProgress = gameProgressRepository.saveAndFlush(gameProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gameProgress
        restGameProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, gameProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gameProgressRepository.count();
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

    protected GameProgress getPersistedGameProgress(GameProgress gameProgress) {
        return gameProgressRepository.findById(gameProgress.getId()).orElseThrow();
    }

    protected void assertPersistedGameProgressToMatchAllProperties(GameProgress expectedGameProgress) {
        assertGameProgressAllPropertiesEquals(expectedGameProgress, getPersistedGameProgress(expectedGameProgress));
    }

    protected void assertPersistedGameProgressToMatchUpdatableProperties(GameProgress expectedGameProgress) {
        assertGameProgressAllUpdatablePropertiesEquals(expectedGameProgress, getPersistedGameProgress(expectedGameProgress));
    }
}
