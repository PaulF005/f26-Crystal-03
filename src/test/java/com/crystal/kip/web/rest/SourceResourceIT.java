package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.SourceAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.Source;
import com.crystal.kip.repository.SourceRepository;
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
 * Integration tests for the {@link SourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SourceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_DATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSourceMockMvc;

    private Source source;

    private Source insertedSource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Source createEntity() {
        return new Source().name(DEFAULT_NAME).url(DEFAULT_URL).date(DEFAULT_DATE).data(DEFAULT_DATA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Source createUpdatedEntity() {
        return new Source().name(UPDATED_NAME).url(UPDATED_URL).date(UPDATED_DATE).data(UPDATED_DATA);
    }

    @BeforeEach
    void initTest() {
        source = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSource != null) {
            sourceRepository.delete(insertedSource);
            insertedSource = null;
        }
    }

    @Test
    @Transactional
    void createSource() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Source
        var returnedSource = om.readValue(
            restSourceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Source.class
        );

        // Validate the Source in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSourceUpdatableFieldsEquals(returnedSource, getPersistedSource(returnedSource));

        insertedSource = returnedSource;
    }

    @Test
    @Transactional
    void createSourceWithExistingId() throws Exception {
        // Create the Source with an existing ID
        source.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        source.setName(null);

        // Create the Source, which fails.

        restSourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        source.setData(null);

        // Create the Source, which fails.

        restSourceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSources() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        // Get all the sourceList
        restSourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)));
    }

    @Test
    @Transactional
    void getSource() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        // Get the source
        restSourceMockMvc
            .perform(get(ENTITY_API_URL_ID, source.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(source.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA));
    }

    @Test
    @Transactional
    void getNonExistingSource() throws Exception {
        // Get the source
        restSourceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSource() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the source
        Source updatedSource = sourceRepository.findById(source.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSource are not directly saved in db
        em.detach(updatedSource);
        updatedSource.name(UPDATED_NAME).url(UPDATED_URL).date(UPDATED_DATE).data(UPDATED_DATA);

        restSourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSource.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSource))
            )
            .andExpect(status().isOk());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSourceToMatchAllProperties(updatedSource);
    }

    @Test
    @Transactional
    void putNonExistingSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(put(ENTITY_API_URL_ID, source.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(source))
            )
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(source)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSourceWithPatch() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the source using partial update
        Source partialUpdatedSource = new Source();
        partialUpdatedSource.setId(source.getId());

        partialUpdatedSource.name(UPDATED_NAME);

        restSourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSource.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSource))
            )
            .andExpect(status().isOk());

        // Validate the Source in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSourceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSource, source), getPersistedSource(source));
    }

    @Test
    @Transactional
    void fullUpdateSourceWithPatch() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the source using partial update
        Source partialUpdatedSource = new Source();
        partialUpdatedSource.setId(source.getId());

        partialUpdatedSource.name(UPDATED_NAME).url(UPDATED_URL).date(UPDATED_DATE).data(UPDATED_DATA);

        restSourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSource.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSource))
            )
            .andExpect(status().isOk());

        // Validate the Source in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSourceUpdatableFieldsEquals(partialUpdatedSource, getPersistedSource(partialUpdatedSource));
    }

    @Test
    @Transactional
    void patchNonExistingSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, source.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(source))
            )
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(source))
            )
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        source.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSourceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(source)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Source in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSource() throws Exception {
        // Initialize the database
        insertedSource = sourceRepository.saveAndFlush(source);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the source
        restSourceMockMvc
            .perform(delete(ENTITY_API_URL_ID, source.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sourceRepository.count();
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

    protected Source getPersistedSource(Source source) {
        return sourceRepository.findById(source.getId()).orElseThrow();
    }

    protected void assertPersistedSourceToMatchAllProperties(Source expectedSource) {
        assertSourceAllPropertiesEquals(expectedSource, getPersistedSource(expectedSource));
    }

    protected void assertPersistedSourceToMatchUpdatableProperties(Source expectedSource) {
        assertSourceAllUpdatablePropertiesEquals(expectedSource, getPersistedSource(expectedSource));
    }
}
