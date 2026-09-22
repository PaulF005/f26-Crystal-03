package com.crystal.kip.web.rest;

import com.crystal.kip.domain.Progress;
import com.crystal.kip.repository.ProgressRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.Progress}.
 */
@RestController
@RequestMapping("/api/progresses")
@Transactional(rollbackFor = Exception.class)
public class ProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProgressResource.class);

    private static final String ENTITY_NAME = "progress";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final ProgressRepository progressRepository;

    public ProgressResource(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    /**
     * {@code POST  /progresses} : Create a new progress.
     *
     * @param progress the progress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new progress, or with status {@code 400 (Bad Request)} if the progress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Progress> createProgress(@Valid @RequestBody Progress progress) throws URISyntaxException {
        LOG.debug("REST request to save Progress : {}", progress);
        if (progress.getId() != null) {
            throw new BadRequestAlertException("A new progress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        progress = progressRepository.save(progress);
        return ResponseEntity.created(new URI("/api/progresses/" + progress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, progress.getId().toString()))
            .body(progress);
    }

    /**
     * {@code PUT  /progresses/:id} : Updates an existing progress.
     *
     * @param id the id of the progress to save.
     * @param progress the progress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progress,
     * or with status {@code 400 (Bad Request)} if the progress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the progress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Progress> updateProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Progress progress
    ) throws URISyntaxException {
        LOG.debug("REST request to update Progress : {}, {}", id, progress);
        if (progress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        progress = progressRepository.save(progress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, progress.getId().toString()))
            .body(progress);
    }

    /**
     * {@code PATCH  /progresses/:id} : Partial updates given fields of an existing progress, field will ignore if it is null
     *
     * @param id the id of the progress to save.
     * @param progress the progress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated progress,
     * or with status {@code 400 (Bad Request)} if the progress is not valid,
     * or with status {@code 404 (Not Found)} if the progress is not found,
     * or with status {@code 500 (Internal Server Error)} if the progress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Progress> partialUpdateProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Progress progress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Progress partially : {}, {}", id, progress);
        if (progress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, progress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!progressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Progress> result = progressRepository
            .findById(progress.getId())
            .map(existingProgress -> {
                updateIfPresent(existingProgress::setModuleCompletion, progress.getModuleCompletion());

                return existingProgress;
            })
            .map(progressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, progress.getId().toString())
        );
    }

    /**
     * {@code GET  /progresses} : get all the Progresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Progresses in body.
     */
    @GetMapping("")
    public List<Progress> getAllProgresses() {
        LOG.debug("REST request to get all Progresses");
        return progressRepository.findAll();
    }

    /**
     * {@code GET  /progresses/:id} : get the "id" progress.
     *
     * @param id the id of the progress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the progress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Progress> getProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Progress : {}", id);
        Optional<Progress> progress = progressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(progress);
    }

    /**
     * {@code DELETE  /progresses/:id} : delete the "id" progress.
     *
     * @param id the id of the progress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Progress : {}", id);
        progressRepository.deleteById(id);
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
