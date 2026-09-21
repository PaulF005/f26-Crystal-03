package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.GameSessionAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.GameSession;
import com.crystal.kip.repository.GameSessionRepository;
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
 * Integration tests for the {@link GameSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GameSessionResourceIT {

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final String ENTITY_API_URL = "/api/game-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGameSessionMockMvc;

    private GameSession gameSession;

    private GameSession insertedGameSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameSession createEntity() {
        return new GameSession().startedAt(DEFAULT_STARTED_AT).completedAt(DEFAULT_COMPLETED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameSession createUpdatedEntity() {
        return new GameSession().startedAt(UPDATED_STARTED_AT).completedAt(UPDATED_COMPLETED_AT);
    }

    @BeforeEach
    void initTest() {
        gameSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGameSession != null) {
            gameSessionRepository.delete(insertedGameSession);
            insertedGameSession = null;
        }
    }

    @Test
    @Transactional
    void createGameSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GameSession
        var returnedGameSession = om.readValue(
            restGameSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameSession)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GameSession.class
        );

        // Validate the GameSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGameSessionUpdatableFieldsEquals(returnedGameSession, getPersistedGameSession(returnedGameSession));

        insertedGameSession = returnedGameSession;
    }

    @Test
    @Transactional
    void createGameSessionWithExistingId() throws Exception {
        // Create the GameSession with an existing ID
        gameSession.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGameSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameSession)))
            .andExpect(status().isBadRequest());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameSession.setStartedAt(null);

        // Create the GameSession, which fails.

        restGameSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameSession)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCompletedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gameSession.setCompletedAt(null);

        // Create the GameSession, which fails.

        restGameSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameSession)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGameSessions() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        // Get all the gameSessionList
        restGameSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gameSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));
    }

    @Test
    @Transactional
    void getGameSession() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        // Get the gameSession
        restGameSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, gameSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gameSession.getId().intValue()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGameSession() throws Exception {
        // Get the gameSession
        restGameSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGameSession() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameSession
        GameSession updatedGameSession = gameSessionRepository.findById(gameSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGameSession are not directly saved in db
        em.detach(updatedGameSession);
        updatedGameSession.startedAt(UPDATED_STARTED_AT).completedAt(UPDATED_COMPLETED_AT);

        restGameSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGameSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedGameSession))
            )
            .andExpect(status().isOk());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGameSessionToMatchAllProperties(updatedGameSession);
    }

    @Test
    @Transactional
    void putNonExistingGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gameSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gameSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gameSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gameSession)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGameSessionWithPatch() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameSession using partial update
        GameSession partialUpdatedGameSession = new GameSession();
        partialUpdatedGameSession.setId(gameSession.getId());

        partialUpdatedGameSession.completedAt(UPDATED_COMPLETED_AT);

        restGameSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGameSession))
            )
            .andExpect(status().isOk());

        // Validate the GameSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGameSession, gameSession),
            getPersistedGameSession(gameSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateGameSessionWithPatch() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gameSession using partial update
        GameSession partialUpdatedGameSession = new GameSession();
        partialUpdatedGameSession.setId(gameSession.getId());

        partialUpdatedGameSession.startedAt(UPDATED_STARTED_AT).completedAt(UPDATED_COMPLETED_AT);

        restGameSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGameSession))
            )
            .andExpect(status().isOk());

        // Validate the GameSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameSessionUpdatableFieldsEquals(partialUpdatedGameSession, getPersistedGameSession(partialUpdatedGameSession));
    }

    @Test
    @Transactional
    void patchNonExistingGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gameSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gameSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gameSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGameSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gameSession)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGameSession() throws Exception {
        // Initialize the database
        insertedGameSession = gameSessionRepository.saveAndFlush(gameSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gameSession
        restGameSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, gameSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gameSessionRepository.count();
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

    protected GameSession getPersistedGameSession(GameSession gameSession) {
        return gameSessionRepository.findById(gameSession.getId()).orElseThrow();
    }

    protected void assertPersistedGameSessionToMatchAllProperties(GameSession expectedGameSession) {
        assertGameSessionAllPropertiesEquals(expectedGameSession, getPersistedGameSession(expectedGameSession));
    }

    protected void assertPersistedGameSessionToMatchUpdatableProperties(GameSession expectedGameSession) {
        assertGameSessionAllUpdatablePropertiesEquals(expectedGameSession, getPersistedGameSession(expectedGameSession));
    }
}
