package com.crystal.kip.web.rest;

import com.crystal.kip.domain.ConceptProgress;
import com.crystal.kip.repository.ConceptProgressRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.ConceptProgress}.
 */
@RestController
@RequestMapping("/api/concept-progresses")
@Transactional
public class ConceptProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConceptProgressResource.class);

    private static final String ENTITY_NAME = "conceptProgress";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final ConceptProgressRepository conceptProgressRepository;

    public ConceptProgressResource(ConceptProgressRepository conceptProgressRepository) {
        this.conceptProgressRepository = conceptProgressRepository;
    }

    /**
     * {@code POST  /concept-progresses} : Create a new conceptProgress.
     *
     * @param conceptProgress the conceptProgress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conceptProgress, or with status {@code 400 (Bad Request)} if the conceptProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConceptProgress> createConceptProgress(@Valid @RequestBody ConceptProgress conceptProgress)
        throws URISyntaxException {
        LOG.debug("REST request to save ConceptProgress : {}", conceptProgress);
        if (conceptProgress.getId() != null) {
            throw new BadRequestAlertException("A new conceptProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        conceptProgress = conceptProgressRepository.save(conceptProgress);
        return ResponseEntity.created(new URI("/api/concept-progresses/" + conceptProgress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, conceptProgress.getId().toString()))
            .body(conceptProgress);
    }

    /**
     * {@code PUT  /concept-progresses/:id} : Updates an existing conceptProgress.
     *
     * @param id the id of the conceptProgress to save.
     * @param conceptProgress the conceptProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conceptProgress,
     * or with status {@code 400 (Bad Request)} if the conceptProgress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conceptProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConceptProgress> updateConceptProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ConceptProgress conceptProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to update ConceptProgress : {}, {}", id, conceptProgress);
        if (conceptProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conceptProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conceptProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        conceptProgress = conceptProgressRepository.save(conceptProgress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, conceptProgress.getId().toString()))
            .body(conceptProgress);
    }

    /**
     * {@code PATCH  /concept-progresses/:id} : Partial updates given fields of an existing conceptProgress, field will ignore if it is null
     *
     * @param id the id of the conceptProgress to save.
     * @param conceptProgress the conceptProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conceptProgress,
     * or with status {@code 400 (Bad Request)} if the conceptProgress is not valid,
     * or with status {@code 404 (Not Found)} if the conceptProgress is not found,
     * or with status {@code 500 (Internal Server Error)} if the conceptProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConceptProgress> partialUpdateConceptProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ConceptProgress conceptProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ConceptProgress partially : {}, {}", id, conceptProgress);
        if (conceptProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conceptProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conceptProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConceptProgress> result = conceptProgressRepository
            .findById(conceptProgress.getId())
            .map(existingConceptProgress -> {
                updateIfPresent(existingConceptProgress::setCompetency, conceptProgress.getCompetency());
                updateIfPresent(existingConceptProgress::setImprovement, conceptProgress.getImprovement());
                updateIfPresent(existingConceptProgress::setEvidenceCount, conceptProgress.getEvidenceCount());
                updateIfPresent(existingConceptProgress::setLastPracticedAt, conceptProgress.getLastPracticedAt());
                updateIfPresent(existingConceptProgress::setMaxQuestions, conceptProgress.getMaxQuestions());

                return existingConceptProgress;
            })
            .map(conceptProgressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, conceptProgress.getId().toString())
        );
    }

    /**
     * {@code GET  /concept-progresses} : get all the Concept Progresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Concept Progresses in body.
     */
    @GetMapping("")
    public List<ConceptProgress> getAllConceptProgresses() {
        LOG.debug("REST request to get all ConceptProgresses");
        return conceptProgressRepository.findAll();
    }

    /**
     * {@code GET  /concept-progresses/:id} : get the "id" conceptProgress.
     *
     * @param id the id of the conceptProgress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conceptProgress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConceptProgress> getConceptProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ConceptProgress : {}", id);
        Optional<ConceptProgress> conceptProgress = conceptProgressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(conceptProgress);
    }

    /**
     * {@code DELETE  /concept-progresses/:id} : delete the "id" conceptProgress.
     *
     * @param id the id of the conceptProgress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConceptProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ConceptProgress : {}", id);
        conceptProgressRepository.deleteById(id);
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
