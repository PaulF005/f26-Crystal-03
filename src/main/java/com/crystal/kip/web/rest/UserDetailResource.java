package com.crystal.kip.web.rest;

import com.crystal.kip.domain.UserDetail;
import com.crystal.kip.repository.UserDetailRepository;
import com.crystal.kip.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.crystal.kip.domain.UserDetail}.
 */
@RestController
@RequestMapping("/api/user-details")
@Transactional
public class UserDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailResource.class);

    private static final String ENTITY_NAME = "userDetail";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final UserDetailRepository userDetailRepository;

    public UserDetailResource(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    /**
     * {@code POST  /user-details} : Create a new userDetail.
     *
     * @param userDetail the userDetail to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userDetail, or with status {@code 400 (Bad Request)} if the userDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserDetail> createUserDetail(@Valid @RequestBody UserDetail userDetail) throws URISyntaxException {
        LOG.debug("REST request to save UserDetail : {}", userDetail);
        if (userDetail.getId() != null) {
            throw new BadRequestAlertException("A new userDetail cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userDetail = userDetailRepository.save(userDetail);
        return ResponseEntity.created(new URI("/api/user-details/" + userDetail.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userDetail.getId().toString()))
            .body(userDetail);
    }

    /**
     * {@code PUT  /user-details/:id} : Updates an existing userDetail.
     *
     * @param id the id of the userDetail to save.
     * @param userDetail the userDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDetail,
     * or with status {@code 400 (Bad Request)} if the userDetail is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDetail> updateUserDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserDetail userDetail
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserDetail : {}, {}", id, userDetail);
        if (userDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userDetail = userDetailRepository.save(userDetail);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userDetail.getId().toString()))
            .body(userDetail);
    }

    /**
     * {@code PATCH  /user-details/:id} : Partial updates given fields of an existing userDetail, field will ignore if it is null
     *
     * @param id the id of the userDetail to save.
     * @param userDetail the userDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userDetail,
     * or with status {@code 400 (Bad Request)} if the userDetail is not valid,
     * or with status {@code 404 (Not Found)} if the userDetail is not found,
     * or with status {@code 500 (Internal Server Error)} if the userDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserDetail> partialUpdateUserDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserDetail userDetail
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserDetail partially : {}, {}", id, userDetail);
        if (userDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserDetail> result = userDetailRepository
            .findById(userDetail.getId())
            .map(existingUserDetail -> {
                updateIfPresent(existingUserDetail::setUsername, userDetail.getUsername());
                updateIfPresent(existingUserDetail::setEmail, userDetail.getEmail());

                return existingUserDetail;
            })
            .map(userDetailRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userDetail.getId().toString())
        );
    }

    /**
     * {@code GET  /user-details} : get all the User Details.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of User Details in body.
     */
    @GetMapping("")
    public List<UserDetail> getAllUserDetails() {
        LOG.debug("REST request to get all UserDetails");
        return userDetailRepository.findAll();
    }

    /**
     * {@code GET  /user-details/:id} : get the "id" userDetail.
     *
     * @param id the id of the userDetail to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userDetail, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetail> getUserDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserDetail : {}", id);
        Optional<UserDetail> userDetail = userDetailRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userDetail);
    }

    /**
     * {@code DELETE  /user-details/:id} : delete the "id" userDetail.
     *
     * @param id the id of the userDetail to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserDetail : {}", id);
        userDetailRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
