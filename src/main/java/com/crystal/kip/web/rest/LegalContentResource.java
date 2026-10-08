package com.crystal.kip.web.rest;

import com.crystal.kip.domain.LegalContent;
import com.crystal.kip.repository.LegalContentRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.LegalContent}.
 */
@RestController
@RequestMapping("/api/legal-contents")
@Transactional
public class LegalContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(LegalContentResource.class);

    private static final String ENTITY_NAME = "legalContent";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final LegalContentRepository legalContentRepository;

    public LegalContentResource(LegalContentRepository legalContentRepository) {
        this.legalContentRepository = legalContentRepository;
    }

    /**
     * {@code POST  /legal-contents} : Create a new legalContent.
     *
     * @param legalContent the legalContent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new legalContent, or with status {@code 400 (Bad Request)} if the legalContent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LegalContent> createLegalContent(@Valid @RequestBody LegalContent legalContent) throws URISyntaxException {
        LOG.debug("REST request to save LegalContent : {}", legalContent);
        if (legalContent.getId() != null) {
            throw new BadRequestAlertException("A new legalContent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        legalContent = legalContentRepository.save(legalContent);
        return ResponseEntity.created(new URI("/api/legal-contents/" + legalContent.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, legalContent.getId().toString()))
            .body(legalContent);
    }

    /**
     * {@code PUT  /legal-contents/:id} : Updates an existing legalContent.
     *
     * @param id the id of the legalContent to save.
     * @param legalContent the legalContent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated legalContent,
     * or with status {@code 400 (Bad Request)} if the legalContent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the legalContent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LegalContent> updateLegalContent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LegalContent legalContent
    ) throws URISyntaxException {
        LOG.debug("REST request to update LegalContent : {}, {}", id, legalContent);
        if (legalContent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, legalContent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!legalContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        legalContent = legalContentRepository.save(legalContent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, legalContent.getId().toString()))
            .body(legalContent);
    }

    /**
     * {@code PATCH  /legal-contents/:id} : Partial updates given fields of an existing legalContent, field will ignore if it is null
     *
     * @param id the id of the legalContent to save.
     * @param legalContent the legalContent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated legalContent,
     * or with status {@code 400 (Bad Request)} if the legalContent is not valid,
     * or with status {@code 404 (Not Found)} if the legalContent is not found,
     * or with status {@code 500 (Internal Server Error)} if the legalContent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LegalContent> partialUpdateLegalContent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LegalContent legalContent
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LegalContent partially : {}, {}", id, legalContent);
        if (legalContent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, legalContent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!legalContentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LegalContent> result = legalContentRepository
            .findById(legalContent.getId())
            .map(existingLegalContent -> {
                updateIfPresent(existingLegalContent::setName, legalContent.getName());

                return existingLegalContent;
            })
            .map(legalContentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, legalContent.getId().toString())
        );
    }

    /**
     * {@code GET  /legal-contents} : get all the Legal Contents.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Legal Contents in body.
     */
    @GetMapping("")
    public List<LegalContent> getAllLegalContents() {
        LOG.debug("REST request to get all LegalContents");
        return legalContentRepository.findAll();
    }

    /**
     * {@code GET  /legal-contents/:id} : get the "id" legalContent.
     *
     * @param id the id of the legalContent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the legalContent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LegalContent> getLegalContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LegalContent : {}", id);
        Optional<LegalContent> legalContent = legalContentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(legalContent);
    }

    /**
     * {@code DELETE  /legal-contents/:id} : delete the "id" legalContent.
     *
     * @param id the id of the legalContent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalContent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LegalContent : {}", id);
        legalContentRepository.deleteById(id);
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
