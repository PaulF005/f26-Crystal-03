package com.crystal.kip.web.rest;

import com.crystal.kip.domain.Concept;
import com.crystal.kip.repository.ConceptRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.Concept}.
 */
@RestController
@RequestMapping("/api/concepts")
@Transactional
public class ConceptResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConceptResource.class);

    private static final String ENTITY_NAME = "concept";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final ConceptRepository conceptRepository;

    public ConceptResource(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    /**
     * {@code POST  /concepts} : Create a new concept.
     *
     * @param concept the concept to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new concept, or with status {@code 400 (Bad Request)} if the concept has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Concept> createConcept(@Valid @RequestBody Concept concept) throws URISyntaxException {
        LOG.debug("REST request to save Concept : {}", concept);
        if (concept.getId() != null) {
            throw new BadRequestAlertException("A new concept cannot already have an ID", ENTITY_NAME, "idexists");
        }
        concept = conceptRepository.save(concept);
        return ResponseEntity.created(new URI("/api/concepts/" + concept.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, concept.getId().toString()))
            .body(concept);
    }

    /**
     * {@code PUT  /concepts/:id} : Updates an existing concept.
     *
     * @param id the id of the concept to save.
     * @param concept the concept to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated concept,
     * or with status {@code 400 (Bad Request)} if the concept is not valid,
     * or with status {@code 500 (Internal Server Error)} if the concept couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Concept> updateConcept(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Concept concept
    ) throws URISyntaxException {
        LOG.debug("REST request to update Concept : {}, {}", id, concept);
        if (concept.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, concept.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conceptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        concept = conceptRepository.save(concept);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, concept.getId().toString()))
            .body(concept);
    }

    /**
     * {@code PATCH  /concepts/:id} : Partial updates given fields of an existing concept, field will ignore if it is null
     *
     * @param id the id of the concept to save.
     * @param concept the concept to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated concept,
     * or with status {@code 400 (Bad Request)} if the concept is not valid,
     * or with status {@code 404 (Not Found)} if the concept is not found,
     * or with status {@code 500 (Internal Server Error)} if the concept couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Concept> partialUpdateConcept(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Concept concept
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Concept partially : {}, {}", id, concept);
        if (concept.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, concept.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conceptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Concept> result = conceptRepository
            .findById(concept.getId())
            .map(existingConcept -> {
                updateIfPresent(existingConcept::setName, concept.getName());
                updateIfPresent(existingConcept::setExplanation, concept.getExplanation());

                return existingConcept;
            })
            .map(conceptRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, concept.getId().toString())
        );
    }

    /**
     * {@code GET  /concepts} : get all the Concepts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Concepts in body.
     */
    @GetMapping("")
    public List<Concept> getAllConcepts() {
        LOG.debug("REST request to get all Concepts");
        return conceptRepository.findAll();
    }

    /**
     * {@code GET  /concepts/:id} : get the "id" concept.
     *
     * @param id the id of the concept to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the concept, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Concept> getConcept(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Concept : {}", id);
        Optional<Concept> concept = conceptRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(concept);
    }

    /**
     * {@code DELETE  /concepts/:id} : delete the "id" concept.
     *
     * @param id the id of the concept to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcept(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Concept : {}", id);
        conceptRepository.deleteById(id);
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
