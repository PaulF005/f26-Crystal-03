package com.crystal.kip.web.rest;

import com.crystal.kip.domain.Stage;
import com.crystal.kip.repository.StageRepository;
import com.crystal.kip.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.crystal.kip.domain.Stage}.
 */
@RestController
@RequestMapping("/api/stages")
@Transactional
public class StageResource {

    private static final Logger LOG = LoggerFactory.getLogger(StageResource.class);

    private static final String ENTITY_NAME = "stage";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final StageRepository stageRepository;

    public StageResource(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    /**
     * {@code POST  /stages} : Create a new stage.
     *
     * @param stage the stage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stage, or with status {@code 400 (Bad Request)} if the stage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Stage> createStage(@Valid @RequestBody Stage stage) throws URISyntaxException {
        LOG.debug("REST request to save Stage : {}", stage);
        if (stage.getId() != null) {
            throw new BadRequestAlertException("A new stage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stage = stageRepository.save(stage);
        return ResponseEntity.created(new URI("/api/stages/" + stage.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, stage.getId().toString()))
            .body(stage);
    }

    /**
     * {@code PUT  /stages/:id} : Updates an existing stage.
     *
     * @param id the id of the stage to save.
     * @param stage the stage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stage,
     * or with status {@code 400 (Bad Request)} if the stage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Stage> updateStage(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Stage stage)
        throws URISyntaxException {
        LOG.debug("REST request to update Stage : {}, {}", id, stage);
        if (stage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stage = stageRepository.save(stage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stage.getId().toString()))
            .body(stage);
    }

    /**
     * {@code PATCH  /stages/:id} : Partial updates given fields of an existing stage, field will ignore if it is null
     *
     * @param id the id of the stage to save.
     * @param stage the stage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stage,
     * or with status {@code 400 (Bad Request)} if the stage is not valid,
     * or with status {@code 404 (Not Found)} if the stage is not found,
     * or with status {@code 500 (Internal Server Error)} if the stage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Stage> partialUpdateStage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Stage stage
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Stage partially : {}, {}", id, stage);
        if (stage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stage> result = stageRepository.findById(stage.getId()).map(stageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stage.getId().toString())
        );
    }

    /**
     * {@code GET  /stages} : get all the Stages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Stages in body.
     */
    @GetMapping("")
    public List<Stage> getAllStages() {
        LOG.debug("REST request to get all Stages");
        return stageRepository.findAll();
    }

    /**
     * {@code GET  /stages/:id} : get the "id" stage.
     *
     * @param id the id of the stage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Stage> getStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Stage : {}", id);
        Optional<Stage> stage = stageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stage);
    }

    /**
     * {@code DELETE  /stages/:id} : delete the "id" stage.
     *
     * @param id the id of the stage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Stage : {}", id);
        stageRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
