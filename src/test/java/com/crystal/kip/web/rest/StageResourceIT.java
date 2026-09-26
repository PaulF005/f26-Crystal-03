package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.StageAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.repository.StageRepository;
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
 * Integration tests for the {@link StageResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@AutoConfigureMockMvc
@WithMockUser
class StageResourceIT {

    private static final String ENTITY_API_URL = "/api/stages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStageMockMvc;

    private Stage stage;

    private Stage insertedStage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stage createEntity(EntityManager em) {
        Stage stage = new Stage();
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        stage.setQuestion(question);
        return stage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stage createUpdatedEntity(EntityManager em) {
        Stage updatedStage = new Stage();
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createUpdatedEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        updatedStage.setQuestion(question);
        return updatedStage;
    }

    @BeforeEach
    void initTest() {
        stage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStage != null) {
            stageRepository.delete(insertedStage);
            insertedStage = null;
        }
    }

    @Test
    @Transactional
    void createStage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Stage
        var returnedStage = om.readValue(
            restStageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stage)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Stage.class
        );

        // Validate the Stage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStageUpdatableFieldsEquals(returnedStage, getPersistedStage(returnedStage));

        insertedStage = returnedStage;
    }

    @Test
    @Transactional
    void createStageWithExistingId() throws Exception {
        // Create the Stage with an existing ID
        stage.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stage)))
            .andExpect(status().isBadRequest());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStages() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        // Get all the stageList
        restStageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stage.getId().intValue())));
    }

    @Test
    @Transactional
    void getStage() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        // Get the stage
        restStageMockMvc
            .perform(get(ENTITY_API_URL_ID, stage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stage.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingStage() throws Exception {
        // Get the stage
        restStageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStage() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stage
        Stage updatedStage = stageRepository.findById(stage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStage are not directly saved in db
        em.detach(updatedStage);

        restStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStage))
            )
            .andExpect(status().isOk());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStageToMatchAllProperties(updatedStage);
    }

    @Test
    @Transactional
    void putNonExistingStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(put(ENTITY_API_URL_ID, stage.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stage)))
            .andExpect(status().isBadRequest());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStageWithPatch() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stage using partial update
        Stage partialUpdatedStage = new Stage();
        partialUpdatedStage.setId(stage.getId());

        restStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStage))
            )
            .andExpect(status().isOk());

        // Validate the Stage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStageUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStage, stage), getPersistedStage(stage));
    }

    @Test
    @Transactional
    void fullUpdateStageWithPatch() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stage using partial update
        Stage partialUpdatedStage = new Stage();
        partialUpdatedStage.setId(stage.getId());

        restStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStage))
            )
            .andExpect(status().isOk());

        // Validate the Stage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStageUpdatableFieldsEquals(partialUpdatedStage, getPersistedStage(partialUpdatedStage));
    }

    @Test
    @Transactional
    void patchNonExistingStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stage.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStage() throws Exception {
        // Initialize the database
        insertedStage = stageRepository.saveAndFlush(stage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stage
        restStageMockMvc
            .perform(delete(ENTITY_API_URL_ID, stage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stageRepository.count();
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

    protected Stage getPersistedStage(Stage stage) {
        return stageRepository.findById(stage.getId()).orElseThrow();
    }

    protected void assertPersistedStageToMatchAllProperties(Stage expectedStage) {
        assertStageAllPropertiesEquals(expectedStage, getPersistedStage(expectedStage));
    }

    protected void assertPersistedStageToMatchUpdatableProperties(Stage expectedStage) {
        assertStageAllUpdatablePropertiesEquals(expectedStage, getPersistedStage(expectedStage));
    }
}
