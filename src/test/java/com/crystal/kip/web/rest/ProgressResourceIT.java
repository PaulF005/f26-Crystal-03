package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.ProgressAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Progress;
import com.crystal.kip.repository.ProgressRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ProgressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProgressResourceIT {

    private static final Float DEFAULT_MODULE_COMPLETION = 1F;
    private static final Float UPDATED_MODULE_COMPLETION = 2F;

    private static final String ENTITY_API_URL = "/api/progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProgressMockMvc;

    private Progress progress;

    private Progress insertedProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Progress createEntity() {
        return new Progress().moduleCompletion(DEFAULT_MODULE_COMPLETION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Progress createUpdatedEntity() {
        return new Progress().moduleCompletion(UPDATED_MODULE_COMPLETION);
    }

    @BeforeEach
    void initTest() {
        progress = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProgress != null) {
            progressRepository.delete(insertedProgress);
            insertedProgress = null;
        }
    }

    @Test
    @Transactional
    void createProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Progress
        var returnedProgress = om.readValue(
            restProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(progress)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Progress.class
        );

        // Validate the Progress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProgressUpdatableFieldsEquals(returnedProgress, getPersistedProgress(returnedProgress));

        insertedProgress = returnedProgress;
    }

    @Test
    @Transactional
    void createProgressWithExistingId() throws Exception {
        // Create the Progress with an existing ID
        progress.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(progress)))
            .andExpect(status().isBadRequest());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkModuleCompletionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        progress.setModuleCompletion(null);

        // Create the Progress, which fails.

        restProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(progress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProgresses() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        // Get all the progressList
        restProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(progress.getId().intValue())))
            .andExpect(jsonPath("$.[*].moduleCompletion").value(hasItem(DEFAULT_MODULE_COMPLETION.doubleValue())));
    }

    @Test
    @Transactional
    void getProgress() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        // Get the progress
        restProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, progress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(progress.getId().intValue()))
            .andExpect(jsonPath("$.moduleCompletion").value(DEFAULT_MODULE_COMPLETION.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingProgress() throws Exception {
        // Get the progress
        restProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProgress() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the progress
        Progress updatedProgress = progressRepository.findById(progress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProgress are not directly saved in db
        em.detach(updatedProgress);
        updatedProgress.moduleCompletion(UPDATED_MODULE_COMPLETION);

        restProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProgress))
            )
            .andExpect(status().isOk());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProgressToMatchAllProperties(updatedProgress);
    }

    @Test
    @Transactional
    void putNonExistingProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, progress.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(progress))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(progress))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(progress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProgressWithPatch() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the progress using partial update
        Progress partialUpdatedProgress = new Progress();
        partialUpdatedProgress.setId(progress.getId());

        partialUpdatedProgress.moduleCompletion(UPDATED_MODULE_COMPLETION);

        restProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProgress))
            )
            .andExpect(status().isOk());

        // Validate the Progress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgressUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProgress, progress), getPersistedProgress(progress));
    }

    @Test
    @Transactional
    void fullUpdateProgressWithPatch() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the progress using partial update
        Progress partialUpdatedProgress = new Progress();
        partialUpdatedProgress.setId(progress.getId());

        partialUpdatedProgress.moduleCompletion(UPDATED_MODULE_COMPLETION);

        restProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProgress))
            )
            .andExpect(status().isOk());

        // Validate the Progress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgressUpdatableFieldsEquals(partialUpdatedProgress, getPersistedProgress(partialUpdatedProgress));
    }

    @Test
    @Transactional
    void patchNonExistingProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, progress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(progress))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(progress))
            )
            .andExpect(status().isBadRequest());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        progress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(progress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Progress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProgress() throws Exception {
        // Initialize the database
        insertedProgress = progressRepository.saveAndFlush(progress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the progress
        restProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, progress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return progressRepository.count();
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

    protected Progress getPersistedProgress(Progress progress) {
        return progressRepository.findById(progress.getId()).orElseThrow();
    }

    protected void assertPersistedProgressToMatchAllProperties(Progress expectedProgress) {
        assertProgressAllPropertiesEquals(expectedProgress, getPersistedProgress(expectedProgress));
    }

    protected void assertPersistedProgressToMatchUpdatableProperties(Progress expectedProgress) {
        assertProgressAllUpdatablePropertiesEquals(expectedProgress, getPersistedProgress(expectedProgress));
    }
}
