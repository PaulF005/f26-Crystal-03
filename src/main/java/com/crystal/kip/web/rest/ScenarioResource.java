package com.crystal.kip.web.rest;

import com.crystal.kip.domain.Scenario;
import com.crystal.kip.repository.ScenarioRepository;
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
 * REST controller for managing {@link com.crystal.kip.domain.Scenario}.
 */
@RestController
@RequestMapping("/api/scenarios")
@Transactional(rollbackFor = Exception.class)
public class ScenarioResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioResource.class);

    private static final String ENTITY_NAME = "scenario";

    @Value("${jhipster.clientApp.name:kip}")
    private String applicationName;

    private final ScenarioRepository scenarioRepository;

    public ScenarioResource(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * {@code POST  /scenarios} : Create a new scenario.
     *
     * @param scenario the scenario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scenario, or with status {@code 400 (Bad Request)} if the scenario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Scenario> createScenario(@Valid @RequestBody Scenario scenario) throws URISyntaxException {
        LOG.debug("REST request to save Scenario : {}", scenario);
        if (scenario.getId() != null) {
            throw new BadRequestAlertException("A new scenario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scenario = scenarioRepository.save(scenario);
        return ResponseEntity.created(new URI("/api/scenarios/" + scenario.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, scenario.getId().toString()))
            .body(scenario);
    }

    /**
     * {@code PUT  /scenarios/:id} : Updates an existing scenario.
     *
     * @param id the id of the scenario to save.
     * @param scenario the scenario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenario,
     * or with status {@code 400 (Bad Request)} if the scenario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scenario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Scenario> updateScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Scenario scenario
    ) throws URISyntaxException {
        LOG.debug("REST request to update Scenario : {}, {}", id, scenario);
        if (scenario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scenario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scenario = scenarioRepository.save(scenario);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scenario.getId().toString()))
            .body(scenario);
    }

    /**
     * {@code PATCH  /scenarios/:id} : Partial updates given fields of an existing scenario, field will ignore if it is null
     *
     * @param id the id of the scenario to save.
     * @param scenario the scenario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenario,
     * or with status {@code 400 (Bad Request)} if the scenario is not valid,
     * or with status {@code 404 (Not Found)} if the scenario is not found,
     * or with status {@code 500 (Internal Server Error)} if the scenario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Scenario> partialUpdateScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Scenario scenario
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Scenario partially : {}, {}", id, scenario);
        if (scenario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scenario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Scenario> result = scenarioRepository
            .findById(scenario.getId())
            .map(existingScenario -> {
                updateIfPresent(existingScenario::setName, scenario.getName());

                return existingScenario;
            })
            .map(scenarioRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, scenario.getId().toString())
        );
    }

    /**
     * {@code GET  /scenarios} : get all the Scenarios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Scenarios in body.
     */
    @GetMapping("")
    public List<Scenario> getAllScenarios() {
        LOG.debug("REST request to get all Scenarios");
        return scenarioRepository.findAll();
    }

    /**
     * {@code GET  /scenarios/:id} : get the "id" scenario.
     *
     * @param id the id of the scenario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Scenario> getScenario(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Scenario : {}", id);
        Optional<Scenario> scenario = scenarioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(scenario);
    }

    /**
     * {@code DELETE  /scenarios/:id} : delete the "id" scenario.
     *
     * @param id the id of the scenario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Scenario : {}", id);
        scenarioRepository.deleteById(id);
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
