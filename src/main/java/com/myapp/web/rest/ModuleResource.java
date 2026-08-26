package com.myapp.web.rest;

import com.myapp.domain.Module;
import com.myapp.repository.ModuleRepository;
import com.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.myapp.domain.Module}.
 */
@RestController
@RequestMapping("/api/modules")
@Transactional
public class ModuleResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleResource.class);

    private static final String ENTITY_NAME = "module";

    @Value("${jhipster.clientApp.name:myApp}")
    private String applicationName;

    private final ModuleRepository moduleRepository;

    public ModuleResource(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    /**
     * {@code POST  /modules} : Create a new module.
     *
     * @param module the module to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new module, or with status {@code 400 (Bad Request)} if the module has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Module> createModule(@Valid @RequestBody Module module) throws URISyntaxException {
        LOG.debug("REST request to save Module : {}", module);
        if (module.getId() != null) {
            throw new BadRequestAlertException("A new module cannot already have an ID", ENTITY_NAME, "idexists");
        }
        module = moduleRepository.save(module);
        return ResponseEntity.created(new URI("/api/modules/" + module.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, module.getId().toString()))
            .body(module);
    }

    /**
     * {@code PUT  /modules/:id} : Updates an existing module.
     *
     * @param id the id of the module to save.
     * @param module the module to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated module,
     * or with status {@code 400 (Bad Request)} if the module is not valid,
     * or with status {@code 500 (Internal Server Error)} if the module couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Module module
    ) throws URISyntaxException {
        LOG.debug("REST request to update Module : {}, {}", id, module);
        if (module.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, module.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        module = moduleRepository.save(module);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, module.getId().toString()))
            .body(module);
    }

    /**
     * {@code PATCH  /modules/:id} : Partial updates given fields of an existing module, field will ignore if it is null
     *
     * @param id the id of the module to save.
     * @param module the module to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated module,
     * or with status {@code 400 (Bad Request)} if the module is not valid,
     * or with status {@code 404 (Not Found)} if the module is not found,
     * or with status {@code 500 (Internal Server Error)} if the module couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Module> partialUpdateModule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Module module
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Module partially : {}, {}", id, module);
        if (module.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, module.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Module> result = moduleRepository
            .findById(module.getId())
            .map(existingModule -> {
                updateIfPresent(existingModule::setName, module.getName());

                return existingModule;
            })
            .map(moduleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, module.getId().toString())
        );
    }

    /**
     * {@code GET  /modules} : get all the Modules.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Modules in body.
     */
    @GetMapping("")
    public List<Module> getAllModules() {
        LOG.debug("REST request to get all Modules");
        return moduleRepository.findAll();
    }

    /**
     * {@code GET  /modules/:id} : get the "id" module.
     *
     * @param id the id of the module to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the module, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Module> getModule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Module : {}", id);
        Optional<Module> module = moduleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(module);
    }

    /**
     * {@code DELETE  /modules/:id} : delete the "id" module.
     *
     * @param id the id of the module to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Module : {}", id);
        moduleRepository.deleteById(id);
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
