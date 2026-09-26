package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.ScenarioAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.repository.ScenarioRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link ScenarioResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@AutoConfigureMockMvc
@WithMockUser
class ScenarioResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/scenarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScenarioMockMvc;

    private Scenario scenario;

    private Scenario insertedScenario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createEntity(EntityManager em) {
        Scenario scenario = new Scenario().name(DEFAULT_NAME);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createEntity();
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        scenario.setTopic(topic);
        // Add required entity
        Game game;
        if (TestUtil.findAll(em, Game.class).isEmpty()) {
            game = GameResourceIT.createEntity();
            em.persist(game);
            em.flush();
        } else {
            game = TestUtil.findAll(em, Game.class).get(0);
        }
        scenario.setGame(game);
        return scenario;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createUpdatedEntity(EntityManager em) {
        Scenario updatedScenario = new Scenario().name(UPDATED_NAME);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createUpdatedEntity();
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        updatedScenario.setTopic(topic);
        // Add required entity
        Game game;
        if (TestUtil.findAll(em, Game.class).isEmpty()) {
            game = GameResourceIT.createUpdatedEntity();
            em.persist(game);
            em.flush();
        } else {
            game = TestUtil.findAll(em, Game.class).get(0);
        }
        updatedScenario.setGame(game);
        return updatedScenario;
    }

    @BeforeEach
    void initTest() {
        scenario = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedScenario != null) {
            scenarioRepository.delete(insertedScenario);
            insertedScenario = null;
        }
    }

    @Test
    @Transactional
    void createScenario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Scenario
        var returnedScenario = om.readValue(
            restScenarioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scenario)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Scenario.class
        );

        // Validate the Scenario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertScenarioUpdatableFieldsEquals(returnedScenario, getPersistedScenario(returnedScenario));

        insertedScenario = returnedScenario;
    }

    @Test
    @Transactional
    void createScenarioWithExistingId() throws Exception {
        // Create the Scenario with an existing ID
        scenario.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scenario)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scenario.setName(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scenario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScenarios() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getScenario() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        // Get the scenario
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL_ID, scenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scenario.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingScenario() throws Exception {
        // Get the scenario
        restScenarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScenario() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario.name(UPDATED_NAME);

        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedScenario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScenarioToMatchAllProperties(updatedScenario);
    }

    @Test
    @Transactional
    void putNonExistingScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scenario.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scenario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setId(scenario.getId());

        partialUpdatedScenario.name(UPDATED_NAME);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScenarioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedScenario, scenario), getPersistedScenario(scenario));
    }

    @Test
    @Transactional
    void fullUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setId(scenario.getId());

        partialUpdatedScenario.name(UPDATED_NAME);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScenarioUpdatableFieldsEquals(partialUpdatedScenario, getPersistedScenario(partialUpdatedScenario));
    }

    @Test
    @Transactional
    void patchNonExistingScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scenario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScenario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scenario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scenario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScenario() throws Exception {
        // Initialize the database
        insertedScenario = scenarioRepository.saveAndFlush(scenario);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scenario
        restScenarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, scenario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scenarioRepository.count();
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

    protected Scenario getPersistedScenario(Scenario scenario) {
        return scenarioRepository.findById(scenario.getId()).orElseThrow();
    }

    protected void assertPersistedScenarioToMatchAllProperties(Scenario expectedScenario) {
        assertScenarioAllPropertiesEquals(expectedScenario, getPersistedScenario(expectedScenario));
    }

    protected void assertPersistedScenarioToMatchUpdatableProperties(Scenario expectedScenario) {
        assertScenarioAllUpdatablePropertiesEquals(expectedScenario, getPersistedScenario(expectedScenario));
    }
}
