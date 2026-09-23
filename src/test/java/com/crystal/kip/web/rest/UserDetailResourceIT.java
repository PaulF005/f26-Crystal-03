package com.crystal.kip.web.rest;

import static com.crystal.kip.domain.UserDetailAsserts.*;
import static com.crystal.kip.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.crystal.kip.IntegrationTest;
import com.crystal.kip.domain.UserDetail;
import com.crystal.kip.repository.UserDetailRepository;
import com.crystal.kip.repository.UserRepository;
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
 * Integration tests for the {@link UserDetailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserDetailResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDetailMockMvc;

    private UserDetail userDetail;

    private UserDetail insertedUserDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDetail createEntity() {
        return new UserDetail().username(DEFAULT_USERNAME).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDetail createUpdatedEntity() {
        return new UserDetail().username(UPDATED_USERNAME).email(UPDATED_EMAIL);
    }

    @BeforeEach
    void initTest() {
        userDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserDetail != null) {
            userDetailRepository.delete(insertedUserDetail);
            insertedUserDetail = null;
        }
    }

    @Test
    @Transactional
    void createUserDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserDetail
        var returnedUserDetail = om.readValue(
            restUserDetailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserDetail.class
        );

        // Validate the UserDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserDetailUpdatableFieldsEquals(returnedUserDetail, getPersistedUserDetail(returnedUserDetail));

        insertedUserDetail = returnedUserDetail;
    }

    @Test
    @Transactional
    void createUserDetailWithExistingId() throws Exception {
        // Create the UserDetail with an existing ID
        userDetail.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail)))
            .andExpect(status().isBadRequest());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userDetail.setUsername(null);

        // Create the UserDetail, which fails.

        restUserDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userDetail.setEmail(null);

        // Create the UserDetail, which fails.

        restUserDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserDetails() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        // Get all the userDetailList
        restUserDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getUserDetail() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        // Get the userDetail
        restUserDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, userDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userDetail.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingUserDetail() throws Exception {
        // Get the userDetail
        restUserDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserDetail() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetail
        UserDetail updatedUserDetail = userDetailRepository.findById(userDetail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserDetail are not directly saved in db
        em.detach(updatedUserDetail);
        updatedUserDetail.username(UPDATED_USERNAME).email(UPDATED_EMAIL);

        restUserDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedUserDetail))
            )
            .andExpect(status().isOk());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserDetailToMatchAllProperties(updatedUserDetail);
    }

    @Test
    @Transactional
    void putNonExistingUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDetail.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDetailWithPatch() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetail using partial update
        UserDetail partialUpdatedUserDetail = new UserDetail();
        partialUpdatedUserDetail.setId(userDetail.getId());

        partialUpdatedUserDetail.email(UPDATED_EMAIL);

        restUserDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserDetail))
            )
            .andExpect(status().isOk());

        // Validate the UserDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserDetail, userDetail),
            getPersistedUserDetail(userDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserDetailWithPatch() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetail using partial update
        UserDetail partialUpdatedUserDetail = new UserDetail();
        partialUpdatedUserDetail.setId(userDetail.getId());

        partialUpdatedUserDetail.username(UPDATED_USERNAME).email(UPDATED_EMAIL);

        restUserDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserDetail))
            )
            .andExpect(status().isOk());

        // Validate the UserDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDetailUpdatableFieldsEquals(partialUpdatedUserDetail, getPersistedUserDetail(partialUpdatedUserDetail));
    }

    @Test
    @Transactional
    void patchNonExistingUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserDetail() throws Exception {
        // Initialize the database
        insertedUserDetail = userDetailRepository.saveAndFlush(userDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userDetail
        restUserDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, userDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userDetailRepository.count();
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

    protected UserDetail getPersistedUserDetail(UserDetail userDetail) {
        return userDetailRepository.findById(userDetail.getId()).orElseThrow();
    }

    protected void assertPersistedUserDetailToMatchAllProperties(UserDetail expectedUserDetail) {
        assertUserDetailAllPropertiesEquals(expectedUserDetail, getPersistedUserDetail(expectedUserDetail));
    }

    protected void assertPersistedUserDetailToMatchUpdatableProperties(UserDetail expectedUserDetail) {
        assertUserDetailAllUpdatablePropertiesEquals(expectedUserDetail, getPersistedUserDetail(expectedUserDetail));
    }
}
