package com.crystal.kip.web.rest;

import com.crystal.kip.domain.TopicProgress;
import com.crystal.kip.repository.LinkRepo.LinkTopicProgressRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.TopicProgress}.
 */
@RestController
@RequestMapping("/api/topic-progresses")
@Transactional(rollbackFor = Exception.class)
public class TopicProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(TopicProgressResource.class);

    private static final String ENTITY_NAME = "topicProgress";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    // Single reference pointer to inject primary repository layer
    private final LinkTopicProgressRepository topicProgressRepository;

    public TopicProgressResource(LinkTopicProgressRepository topicProgressRepository) {
        this.topicProgressRepository = topicProgressRepository;
    }

    /**
     * {@code POST  /topic-progresses} : Create a new topicProgress.
     *
     * @param topicProgress the topicProgress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new topicProgress, or with status {@code 400 (Bad Request)} if the topicProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TopicProgress> createTopicProgress(@Valid @RequestBody TopicProgress topicProgress) throws URISyntaxException {
        LOG.debug("REST request to save TopicProgress : {}", topicProgress);
        if (topicProgress.getId() != null) {
            throw new BadRequestAlertException("A new topicProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        topicProgress = topicProgressRepository.save(topicProgress);
        return ResponseEntity.created(new URI("/api/topic-progresses/" + topicProgress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, topicProgress.getId().toString()))
            .body(topicProgress);
    }

    /**
     * {@code PUT  /topic-progresses/:id} : Updates an existing topicProgress.
     *
     * @param id the id of the topicProgress to save.
     * @param topicProgress the topicProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicProgress,
     * or with status {@code 400 (Bad Request)} if the topicProgress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the topicProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicProgress> updateTopicProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TopicProgress topicProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to update TopicProgress : {}, {}", id, topicProgress);
        if (topicProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!topicProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        topicProgress = topicProgressRepository.save(topicProgress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, topicProgress.getId().toString()))
            .body(topicProgress);
    }

    /**
     * {@code PATCH  /topic-progresses/:id} : Partial updates given fields of an existing topicProgress, field will ignore if it is null
     *
     * @param id the id of the topicProgress to save.
     * @param topicProgress the topicProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicProgress,
     * or with status {@code 400 (Bad Request)} if the topicProgress is not valid,
     * or with status {@code 404 (Not Found)} if the topicProgress is not found,
     * or with status {@code 500 (Internal Server Error)} if the topicProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TopicProgress> partialUpdateTopicProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TopicProgress topicProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TopicProgress partially : {}, {}", id, topicProgress);
        if (topicProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!topicProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TopicProgress> result = topicProgressRepository
            .findById(topicProgress.getId())
            .map(existingTopicProgress -> {
                if (topicProgress.getCompetency() != null) {
                    existingTopicProgress.setCompetency(topicProgress.getCompetency());
                }
                if (topicProgress.getImprovement() != null) {
                    existingTopicProgress.setImprovement(topicProgress.getImprovement());
                }
                if (topicProgress.getLastPracticedAt() != null) {
                    existingTopicProgress.setLastPracticedAt(topicProgress.getLastPracticedAt());
                }
                return existingTopicProgress;
            })
            .map(topicProgressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, topicProgress.getId().toString())
        );
    }

    /**
     * {@code GET  /topic-progresses} : get all the Topic Progresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Topic Progresses in body.
     */
    @GetMapping("")
    public List<TopicProgress> getAllTopicProgresses() {
        LOG.debug("REST request to get all TopicProgresses");
        return topicProgressRepository.findAll();
    }

    /**
     * {@code GET  /topic-progresses/:id} : get the "id" topicProgress.
     *
     * @param id the id of the topicProgress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the topicProgress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicProgress> getTopicProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TopicProgress : {}", id);
        Optional<TopicProgress> topicProgress = topicProgressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(topicProgress);
    }

    /**
     * {@code DELETE  /topic-progresses/:id} : delete the "id" topicProgress.
     *
     * @param id the id of the topicProgress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopicProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TopicProgress : {}", id);
        topicProgressRepository.deleteById(id);
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
