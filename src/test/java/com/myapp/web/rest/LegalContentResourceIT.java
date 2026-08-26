package com.myapp.web.rest;

import static com.myapp.domain.LegalContentAsserts.*;
import static com.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.IntegrationTest;
import com.myapp.domain.LegalContent;
import com.myapp.repository.LegalContentRepository;
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

/**
 * Integration tests for the {@link LegalContentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LegalContentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/legal-contents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LegalContentRepository legalContentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLegalContentMockMvc;

    private LegalContent legalContent;

    private LegalContent insertedLegalContent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LegalContent createEntity() {
        return new LegalContent().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LegalContent createUpdatedEntity() {
        return new LegalContent().name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        legalContent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLegalContent != null) {
            legalContentRepository.delete(insertedLegalContent);
            insertedLegalContent = null;
        }
    }

    @Test
    @Transactional
    void createLegalContent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LegalContent
        var returnedLegalContent = om.readValue(
            restLegalContentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(legalContent)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LegalContent.class
        );

        // Validate the LegalContent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLegalContentUpdatableFieldsEquals(returnedLegalContent, getPersistedLegalContent(returnedLegalContent));

        insertedLegalContent = returnedLegalContent;
    }

    @Test
    @Transactional
    void createLegalContentWithExistingId() throws Exception {
        // Create the LegalContent with an existing ID
        legalContent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLegalContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(legalContent)))
            .andExpect(status().isBadRequest());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        legalContent.setName(null);

        // Create the LegalContent, which fails.

        restLegalContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(legalContent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLegalContents() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        // Get all the legalContentList
        restLegalContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(legalContent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getLegalContent() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        // Get the legalContent
        restLegalContentMockMvc
            .perform(get(ENTITY_API_URL_ID, legalContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(legalContent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingLegalContent() throws Exception {
        // Get the legalContent
        restLegalContentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLegalContent() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the legalContent
        LegalContent updatedLegalContent = legalContentRepository.findById(legalContent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLegalContent are not directly saved in db
        em.detach(updatedLegalContent);
        updatedLegalContent.name(UPDATED_NAME);

        restLegalContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLegalContent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLegalContent))
            )
            .andExpect(status().isOk());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLegalContentToMatchAllProperties(updatedLegalContent);
    }

    @Test
    @Transactional
    void putNonExistingLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, legalContent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(legalContent))
            )
            .andExpect(status().isBadRequest());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(legalContent))
            )
            .andExpect(status().isBadRequest());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(legalContent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLegalContentWithPatch() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the legalContent using partial update
        LegalContent partialUpdatedLegalContent = new LegalContent();
        partialUpdatedLegalContent.setId(legalContent.getId());

        restLegalContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLegalContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLegalContent))
            )
            .andExpect(status().isOk());

        // Validate the LegalContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLegalContentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLegalContent, legalContent),
            getPersistedLegalContent(legalContent)
        );
    }

    @Test
    @Transactional
    void fullUpdateLegalContentWithPatch() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the legalContent using partial update
        LegalContent partialUpdatedLegalContent = new LegalContent();
        partialUpdatedLegalContent.setId(legalContent.getId());

        partialUpdatedLegalContent.name(UPDATED_NAME);

        restLegalContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLegalContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLegalContent))
            )
            .andExpect(status().isOk());

        // Validate the LegalContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLegalContentUpdatableFieldsEquals(partialUpdatedLegalContent, getPersistedLegalContent(partialUpdatedLegalContent));
    }

    @Test
    @Transactional
    void patchNonExistingLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, legalContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(legalContent))
            )
            .andExpect(status().isBadRequest());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(legalContent))
            )
            .andExpect(status().isBadRequest());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLegalContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        legalContent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLegalContentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(legalContent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LegalContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLegalContent() throws Exception {
        // Initialize the database
        insertedLegalContent = legalContentRepository.saveAndFlush(legalContent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the legalContent
        restLegalContentMockMvc
            .perform(delete(ENTITY_API_URL_ID, legalContent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return legalContentRepository.count();
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

    protected LegalContent getPersistedLegalContent(LegalContent legalContent) {
        return legalContentRepository.findById(legalContent.getId()).orElseThrow();
    }

    protected void assertPersistedLegalContentToMatchAllProperties(LegalContent expectedLegalContent) {
        assertLegalContentAllPropertiesEquals(expectedLegalContent, getPersistedLegalContent(expectedLegalContent));
    }

    protected void assertPersistedLegalContentToMatchUpdatableProperties(LegalContent expectedLegalContent) {
        assertLegalContentAllUpdatablePropertiesEquals(expectedLegalContent, getPersistedLegalContent(expectedLegalContent));
    }
}
