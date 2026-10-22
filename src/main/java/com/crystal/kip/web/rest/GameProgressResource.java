package com.crystal.kip.web.rest;

import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.repository.GameProgressRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.GameProgress}.
 */
@RestController
@RequestMapping("/api/game-progresses")
@Transactional(rollbackFor = Exception.class)
public class GameProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(GameProgressResource.class);

    private static final String ENTITY_NAME = "gameProgress";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final GameProgressRepository gameProgressRepository;

    public GameProgressResource(GameProgressRepository gameProgressRepository) {
        this.gameProgressRepository = gameProgressRepository;
    }

    /**
     * {@code POST  /game-progresses} : Create a new gameProgress.
     *
     * @param gameProgress the gameProgress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gameProgress, or with status {@code 400 (Bad Request)} if the gameProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GameProgress> createGameProgress(@Valid @RequestBody GameProgress gameProgress) throws URISyntaxException {
        LOG.debug("REST request to save GameProgress : {}", gameProgress);
        if (gameProgress.getId() != null) {
            throw new BadRequestAlertException("A new gameProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        gameProgress = gameProgressRepository.save(gameProgress);
        return ResponseEntity.created(new URI("/api/game-progresses/" + gameProgress.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, gameProgress.getId().toString()))
            .body(gameProgress);
    }

    /**
     * {@code PUT  /game-progresses/:id} : Updates an existing gameProgress.
     *
     * @param id the id of the gameProgress to save.
     * @param gameProgress the gameProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameProgress,
     * or with status {@code 400 (Bad Request)} if the gameProgress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gameProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GameProgress> updateGameProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GameProgress gameProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to update GameProgress : {}, {}", id, gameProgress);
        if (gameProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        gameProgress = gameProgressRepository.save(gameProgress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gameProgress.getId().toString()))
            .body(gameProgress);
    }

    /**
     * {@code PATCH  /game-progresses/:id} : Partial updates given fields of an existing gameProgress, field will ignore if it is null
     *
     * @param id the id of the gameProgress to save.
     * @param gameProgress the gameProgress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameProgress,
     * or with status {@code 400 (Bad Request)} if the gameProgress is not valid,
     * or with status {@code 404 (Not Found)} if the gameProgress is not found,
     * or with status {@code 500 (Internal Server Error)} if the gameProgress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GameProgress> partialUpdateGameProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GameProgress gameProgress
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GameProgress partially : {}, {}", id, gameProgress);
        if (gameProgress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameProgress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GameProgress> result = gameProgressRepository
            .findById(gameProgress.getId())
            .map(existingGameProgress -> {
                updateIfPresent(existingGameProgress::setSessionsPlayed, gameProgress.getSessionsPlayed());
                updateIfPresent(existingGameProgress::setPerformance, gameProgress.getPerformance());
                updateIfPresent(existingGameProgress::setLastPlayedAt, gameProgress.getLastPlayedAt());

                return existingGameProgress;
            })
            .map(gameProgressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gameProgress.getId().toString())
        );
    }

    /**
     * {@code GET  /game-progresses} : get all the Game Progresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Game Progresses in body.
     */
    @GetMapping("")
    public List<GameProgress> getAllGameProgresses() {
        LOG.debug("REST request to get all GameProgresses");
        return gameProgressRepository.findAll();
    }

    /**
     * {@code GET  /game-progresses/:id} : get the "id" gameProgress.
     *
     * @param id the id of the gameProgress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gameProgress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameProgress> getGameProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GameProgress : {}", id);
        Optional<GameProgress> gameProgress = gameProgressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(gameProgress);
    }

    /**
     * {@code DELETE  /game-progresses/:id} : delete the "id" gameProgress.
     *
     * @param id the id of the gameProgress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GameProgress : {}", id);
        gameProgressRepository.deleteById(id);
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
