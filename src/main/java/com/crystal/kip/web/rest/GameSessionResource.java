package com.crystal.kip.web.rest;

import com.crystal.kip.domain.GameSession;
import com.crystal.kip.repository.GameSessionRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.GameSession}.
 */
@RestController
@RequestMapping("/api/game-sessions")
@Transactional
public class GameSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(GameSessionResource.class);

    private static final String ENTITY_NAME = "gameSession";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final GameSessionRepository gameSessionRepository;

    public GameSessionResource(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    /**
     * {@code POST  /game-sessions} : Create a new gameSession.
     *
     * @param gameSession the gameSession to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gameSession, or with status {@code 400 (Bad Request)} if the gameSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GameSession> createGameSession(@Valid @RequestBody GameSession gameSession) throws URISyntaxException {
        LOG.debug("REST request to save GameSession : {}", gameSession);
        if (gameSession.getId() != null) {
            throw new BadRequestAlertException("A new gameSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        gameSession = gameSessionRepository.save(gameSession);
        return ResponseEntity.created(new URI("/api/game-sessions/" + gameSession.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, gameSession.getId().toString()))
            .body(gameSession);
    }

    /**
     * {@code PUT  /game-sessions/:id} : Updates an existing gameSession.
     *
     * @param id the id of the gameSession to save.
     * @param gameSession the gameSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameSession,
     * or with status {@code 400 (Bad Request)} if the gameSession is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gameSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GameSession> updateGameSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GameSession gameSession
    ) throws URISyntaxException {
        LOG.debug("REST request to update GameSession : {}, {}", id, gameSession);
        if (gameSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        gameSession = gameSessionRepository.save(gameSession);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gameSession.getId().toString()))
            .body(gameSession);
    }

    /**
     * {@code PATCH  /game-sessions/:id} : Partial updates given fields of an existing gameSession, field will ignore if it is null
     *
     * @param id the id of the gameSession to save.
     * @param gameSession the gameSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameSession,
     * or with status {@code 400 (Bad Request)} if the gameSession is not valid,
     * or with status {@code 404 (Not Found)} if the gameSession is not found,
     * or with status {@code 500 (Internal Server Error)} if the gameSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GameSession> partialUpdateGameSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GameSession gameSession
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GameSession partially : {}, {}", id, gameSession);
        if (gameSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GameSession> result = gameSessionRepository
            .findById(gameSession.getId())
            .map(existingGameSession -> {
                updateIfPresent(existingGameSession::setStartedAt, gameSession.getStartedAt());
                updateIfPresent(existingGameSession::setCompletedAt, gameSession.getCompletedAt());

                return existingGameSession;
            })
            .map(gameSessionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, gameSession.getId().toString())
        );
    }

    /**
     * {@code GET  /game-sessions} : get all the Game Sessions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Game Sessions in body.
     */
    @GetMapping("")
    public List<GameSession> getAllGameSessions() {
        LOG.debug("REST request to get all GameSessions");
        return gameSessionRepository.findAll();
    }

    /**
     * {@code GET  /game-sessions/:id} : get the "id" gameSession.
     *
     * @param id the id of the gameSession to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gameSession, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameSession> getGameSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GameSession : {}", id);
        Optional<GameSession> gameSession = gameSessionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(gameSession);
    }

    /**
     * {@code DELETE  /game-sessions/:id} : delete the "id" gameSession.
     *
     * @param id the id of the gameSession to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GameSession : {}", id);
        gameSessionRepository.deleteById(id);
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
