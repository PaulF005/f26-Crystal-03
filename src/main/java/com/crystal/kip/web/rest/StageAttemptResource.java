package com.crystal.kip.web.rest;

import com.crystal.kip.domain.StageAttempt;
import com.crystal.kip.repository.StageAttemptRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.StageAttempt}.
 */
@RestController
@RequestMapping("/api/stage-attempts")
@Transactional(rollbackFor = Exception.class)
public class StageAttemptResource {

    private static final Logger LOG = LoggerFactory.getLogger(StageAttemptResource.class);

    private static final String ENTITY_NAME = "stageAttempt";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final StageAttemptRepository stageAttemptRepository;

    public StageAttemptResource(StageAttemptRepository stageAttemptRepository) {
        this.stageAttemptRepository = stageAttemptRepository;
    }

    /**
     * {@code POST  /stage-attempts} : Create a new stageAttempt.
     *
     * @param stageAttempt the stageAttempt to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stageAttempt, or with status {@code 400 (Bad Request)} if the stageAttempt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StageAttempt> createStageAttempt(@Valid @RequestBody StageAttempt stageAttempt) throws URISyntaxException {
        LOG.debug("REST request to save StageAttempt : {}", stageAttempt);
        if (stageAttempt.getId() != null) {
            throw new BadRequestAlertException("A new stageAttempt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stageAttempt = stageAttemptRepository.save(stageAttempt);
        return ResponseEntity.created(new URI("/api/stage-attempts/" + stageAttempt.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, stageAttempt.getId().toString()))
            .body(stageAttempt);
    }

    /**
     * {@code PUT  /stage-attempts/:id} : Updates an existing stageAttempt.
     *
     * @param id the id of the stageAttempt to save.
     * @param stageAttempt the stageAttempt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stageAttempt,
     * or with status {@code 400 (Bad Request)} if the stageAttempt is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stageAttempt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StageAttempt> updateStageAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StageAttempt stageAttempt
    ) throws URISyntaxException {
        LOG.debug("REST request to update StageAttempt : {}, {}", id, stageAttempt);
        if (stageAttempt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stageAttempt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stageAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stageAttempt = stageAttemptRepository.save(stageAttempt);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stageAttempt.getId().toString()))
            .body(stageAttempt);
    }

    /**
     * {@code PATCH  /stage-attempts/:id} : Partial updates given fields of an existing stageAttempt, field will ignore if it is null
     *
     * @param id the id of the stageAttempt to save.
     * @param stageAttempt the stageAttempt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stageAttempt,
     * or with status {@code 400 (Bad Request)} if the stageAttempt is not valid,
     * or with status {@code 404 (Not Found)} if the stageAttempt is not found,
     * or with status {@code 500 (Internal Server Error)} if the stageAttempt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StageAttempt> partialUpdateStageAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StageAttempt stageAttempt
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StageAttempt partially : {}, {}", id, stageAttempt);
        if (stageAttempt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stageAttempt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stageAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StageAttempt> result = stageAttemptRepository
            .findById(stageAttempt.getId())
            .map(existingStageAttempt -> {
                updateIfPresent(existingStageAttempt::setAnsweredAt, stageAttempt.getAnsweredAt());
                updateIfPresent(existingStageAttempt::setCorrect, stageAttempt.getCorrect());

                return existingStageAttempt;
            })
            .map(stageAttemptRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stageAttempt.getId().toString())
        );
    }

    /**
     * {@code GET  /stage-attempts} : get all the Stage Attempts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Stage Attempts in body.
     */
    @GetMapping("")
    public List<StageAttempt> getAllStageAttempts() {
        LOG.debug("REST request to get all StageAttempts");
        return stageAttemptRepository.findAll();
    }

    /**
     * {@code GET  /stage-attempts/:id} : get the "id" stageAttempt.
     *
     * @param id the id of the stageAttempt to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stageAttempt, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StageAttempt> getStageAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StageAttempt : {}", id);
        Optional<StageAttempt> stageAttempt = stageAttemptRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stageAttempt);
    }

    /**
     * {@code DELETE  /stage-attempts/:id} : delete the "id" stageAttempt.
     *
     * @param id the id of the stageAttempt to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStageAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StageAttempt : {}", id);
        stageAttemptRepository.deleteById(id);
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
