package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.ConceptProgressAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.ConceptProgress;
import com.crystal.kip.repository.ConceptProgressRepository;
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
 * Integration tests for the {@link ConceptProgressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConceptProgressResourceIT {

    private static final Float DEFAULT_COMPETENCY = 0F;
    private static final Float UPDATED_COMPETENCY = 1F;

    private static final Float DEFAULT_IMPROVEMENT = 1F;
    private static final Float UPDATED_IMPROVEMENT = 2F;

    private static final Integer DEFAULT_EVIDENCE_COUNT = 0;
    private static final Integer UPDATED_EVIDENCE_COUNT = 1;

    private static final Instant DEFAULT_LAST_PRACTICED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_PRACTICED_AT = Instant.ofEpochMilli(1787767274668L);

    private static final String ENTITY_API_URL = "/api/concept-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConceptProgressRepository conceptProgressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConceptProgressMockMvc;

    private ConceptProgress conceptProgress;

    private ConceptProgress insertedConceptProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConceptProgress createEntity() {
        return new ConceptProgress()
            .competency(DEFAULT_COMPETENCY)
            .improvement(DEFAULT_IMPROVEMENT)
            .evidenceCount(DEFAULT_EVIDENCE_COUNT)
            .lastPracticedAt(DEFAULT_LAST_PRACTICED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConceptProgress createUpdatedEntity() {
        return new ConceptProgress()
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);
    }

    @BeforeEach
    void initTest() {
        conceptProgress = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedConceptProgress != null) {
            conceptProgressRepository.delete(insertedConceptProgress);
            insertedConceptProgress = null;
        }
    }

    @Test
    @Transactional
    void createConceptProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ConceptProgress
        var returnedConceptProgress = om.readValue(
            restConceptProgressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConceptProgress.class
        );

        // Validate the ConceptProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertConceptProgressUpdatableFieldsEquals(returnedConceptProgress, getPersistedConceptProgress(returnedConceptProgress));

        insertedConceptProgress = returnedConceptProgress;
    }

    @Test
    @Transactional
    void createConceptProgressWithExistingId() throws Exception {
        // Create the ConceptProgress with an existing ID
        conceptProgress.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConceptProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isBadRequest());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCompetencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        conceptProgress.setCompetency(null);

        // Create the ConceptProgress, which fails.

        restConceptProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImprovementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        conceptProgress.setImprovement(null);

        // Create the ConceptProgress, which fails.

        restConceptProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEvidenceCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        conceptProgress.setEvidenceCount(null);

        // Create the ConceptProgress, which fails.

        restConceptProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastPracticedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        conceptProgress.setLastPracticedAt(null);

        // Create the ConceptProgress, which fails.

        restConceptProgressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConceptProgresses() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        // Get all the conceptProgressList
        restConceptProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conceptProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].competency").value(hasItem(DEFAULT_COMPETENCY.doubleValue())))
            .andExpect(jsonPath("$.[*].improvement").value(hasItem(DEFAULT_IMPROVEMENT.doubleValue())))
            .andExpect(jsonPath("$.[*].evidenceCount").value(hasItem(DEFAULT_EVIDENCE_COUNT)))
            .andExpect(jsonPath("$.[*].lastPracticedAt").value(hasItem(DEFAULT_LAST_PRACTICED_AT.toString())));
    }

    @Test
    @Transactional
    void getConceptProgress() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        // Get the conceptProgress
        restConceptProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, conceptProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conceptProgress.getId().intValue()))
            .andExpect(jsonPath("$.competency").value(DEFAULT_COMPETENCY.doubleValue()))
            .andExpect(jsonPath("$.improvement").value(DEFAULT_IMPROVEMENT.doubleValue()))
            .andExpect(jsonPath("$.evidenceCount").value(DEFAULT_EVIDENCE_COUNT))
            .andExpect(jsonPath("$.lastPracticedAt").value(DEFAULT_LAST_PRACTICED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingConceptProgress() throws Exception {
        // Get the conceptProgress
        restConceptProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConceptProgress() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conceptProgress
        ConceptProgress updatedConceptProgress = conceptProgressRepository.findById(conceptProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConceptProgress are not directly saved in db
        em.detach(updatedConceptProgress);
        updatedConceptProgress
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);

        restConceptProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConceptProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedConceptProgress))
            )
            .andExpect(status().isOk());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConceptProgressToMatchAllProperties(updatedConceptProgress);
    }

    @Test
    @Transactional
    void putNonExistingConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conceptProgress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conceptProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conceptProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConceptProgressWithPatch() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conceptProgress using partial update
        ConceptProgress partialUpdatedConceptProgress = new ConceptProgress();
        partialUpdatedConceptProgress.setId(conceptProgress.getId());

        partialUpdatedConceptProgress.competency(UPDATED_COMPETENCY);

        restConceptProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConceptProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConceptProgress))
            )
            .andExpect(status().isOk());

        // Validate the ConceptProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConceptProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConceptProgress, conceptProgress),
            getPersistedConceptProgress(conceptProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateConceptProgressWithPatch() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conceptProgress using partial update
        ConceptProgress partialUpdatedConceptProgress = new ConceptProgress();
        partialUpdatedConceptProgress.setId(conceptProgress.getId());

        partialUpdatedConceptProgress
            .competency(UPDATED_COMPETENCY)
            .improvement(UPDATED_IMPROVEMENT)
            .evidenceCount(UPDATED_EVIDENCE_COUNT)
            .lastPracticedAt(UPDATED_LAST_PRACTICED_AT);

        restConceptProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConceptProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConceptProgress))
            )
            .andExpect(status().isOk());

        // Validate the ConceptProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConceptProgressUpdatableFieldsEquals(
            partialUpdatedConceptProgress,
            getPersistedConceptProgress(partialUpdatedConceptProgress)
        );
    }

    @Test
    @Transactional
    void patchNonExistingConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conceptProgress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conceptProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conceptProgress))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConceptProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conceptProgress.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptProgressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(conceptProgress)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConceptProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConceptProgress() throws Exception {
        // Initialize the database
        insertedConceptProgress = conceptProgressRepository.saveAndFlush(conceptProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the conceptProgress
        restConceptProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, conceptProgress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return conceptProgressRepository.count();
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

    protected ConceptProgress getPersistedConceptProgress(ConceptProgress conceptProgress) {
        return conceptProgressRepository.findById(conceptProgress.getId()).orElseThrow();
    }

    protected void assertPersistedConceptProgressToMatchAllProperties(ConceptProgress expectedConceptProgress) {
        assertConceptProgressAllPropertiesEquals(expectedConceptProgress, getPersistedConceptProgress(expectedConceptProgress));
    }

    protected void assertPersistedConceptProgressToMatchUpdatableProperties(ConceptProgress expectedConceptProgress) {
        assertConceptProgressAllUpdatablePropertiesEquals(expectedConceptProgress, getPersistedConceptProgress(expectedConceptProgress));
    }
}
