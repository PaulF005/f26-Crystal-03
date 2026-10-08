package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.ConceptAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Concept;
import com.crystal.kip.repository.ConceptRepository;
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
 * Integration tests for the {@link ConceptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConceptResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXPLANATION = "AAAAAAAAAA";
    private static final String UPDATED_EXPLANATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/concepts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConceptMockMvc;

    private Concept concept;

    private Concept insertedConcept;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Concept createEntity() {
        return new Concept().name(DEFAULT_NAME).explanation(DEFAULT_EXPLANATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Concept createUpdatedEntity() {
        return new Concept().name(UPDATED_NAME).explanation(UPDATED_EXPLANATION);
    }

    @BeforeEach
    void initTest() {
        concept = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedConcept != null) {
            conceptRepository.delete(insertedConcept);
            insertedConcept = null;
        }
    }

    @Test
    @Transactional
    void createConcept() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Concept
        var returnedConcept = om.readValue(
            restConceptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(concept)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Concept.class
        );

        // Validate the Concept in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertConceptUpdatableFieldsEquals(returnedConcept, getPersistedConcept(returnedConcept));

        insertedConcept = returnedConcept;
    }

    @Test
    @Transactional
    void createConceptWithExistingId() throws Exception {
        // Create the Concept with an existing ID
        concept.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConceptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(concept)))
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        concept.setName(null);

        // Create the Concept, which fails.

        restConceptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(concept)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConcepts() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        // Get all the conceptList
        restConceptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(concept.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].explanation").value(hasItem(DEFAULT_EXPLANATION)));
    }

    @Test
    @Transactional
    void getConcept() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        // Get the concept
        restConceptMockMvc
            .perform(get(ENTITY_API_URL_ID, concept.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(concept.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.explanation").value(DEFAULT_EXPLANATION));
    }

    @Test
    @Transactional
    void getNonExistingConcept() throws Exception {
        // Get the concept
        restConceptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConcept() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the concept
        Concept updatedConcept = conceptRepository.findById(concept.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConcept are not directly saved in db
        em.detach(updatedConcept);
        updatedConcept.name(UPDATED_NAME).explanation(UPDATED_EXPLANATION);

        restConceptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConcept.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedConcept))
            )
            .andExpect(status().isOk());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConceptToMatchAllProperties(updatedConcept);
    }

    @Test
    @Transactional
    void putNonExistingConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(put(ENTITY_API_URL_ID, concept.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(concept)))
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(concept))
            )
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(concept)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConceptWithPatch() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the concept using partial update
        Concept partialUpdatedConcept = new Concept();
        partialUpdatedConcept.setId(concept.getId());

        restConceptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConcept.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConcept))
            )
            .andExpect(status().isOk());

        // Validate the Concept in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConceptUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedConcept, concept), getPersistedConcept(concept));
    }

    @Test
    @Transactional
    void fullUpdateConceptWithPatch() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the concept using partial update
        Concept partialUpdatedConcept = new Concept();
        partialUpdatedConcept.setId(concept.getId());

        partialUpdatedConcept.name(UPDATED_NAME).explanation(UPDATED_EXPLANATION);

        restConceptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConcept.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConcept))
            )
            .andExpect(status().isOk());

        // Validate the Concept in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConceptUpdatableFieldsEquals(partialUpdatedConcept, getPersistedConcept(partialUpdatedConcept));
    }

    @Test
    @Transactional
    void patchNonExistingConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, concept.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(concept))
            )
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(concept))
            )
            .andExpect(status().isBadRequest());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConcept() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        concept.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConceptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(concept)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Concept in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConcept() throws Exception {
        // Initialize the database
        insertedConcept = conceptRepository.saveAndFlush(concept);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the concept
        restConceptMockMvc
            .perform(delete(ENTITY_API_URL_ID, concept.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return conceptRepository.count();
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

    protected Concept getPersistedConcept(Concept concept) {
        return conceptRepository.findById(concept.getId()).orElseThrow();
    }

    protected void assertPersistedConceptToMatchAllProperties(Concept expectedConcept) {
        assertConceptAllPropertiesEquals(expectedConcept, getPersistedConcept(expectedConcept));
    }

    protected void assertPersistedConceptToMatchUpdatableProperties(Concept expectedConcept) {
        assertConceptAllUpdatablePropertiesEquals(expectedConcept, getPersistedConcept(expectedConcept));
    }
}
