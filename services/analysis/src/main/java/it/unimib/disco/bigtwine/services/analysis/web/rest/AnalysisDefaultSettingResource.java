package it.unimib.disco.bigtwine.services.analysis.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisDefaultSetting;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisDefaultSettingRepository;
import it.unimib.disco.bigtwine.services.analysis.web.rest.errors.BadRequestAlertException;
import it.unimib.disco.bigtwine.services.analysis.web.rest.util.HeaderUtil;
import it.unimib.disco.bigtwine.services.analysis.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing AnalysisDefaultSetting.
 */
@RestController
@RequestMapping("/api")
public class AnalysisDefaultSettingResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisDefaultSettingResource.class);

    private static final String ENTITY_NAME = "analysisAnalysisDefaultSetting";

    private final AnalysisDefaultSettingRepository analysisDefaultSettingRepository;

    public AnalysisDefaultSettingResource(AnalysisDefaultSettingRepository analysisDefaultSettingRepository) {
        this.analysisDefaultSettingRepository = analysisDefaultSettingRepository;
    }

    /**
     * POST  /analysis-default-settings : Create a new analysisDefaultSetting.
     *
     * @param analysisDefaultSetting the analysisDefaultSetting to create
     * @return the ResponseEntity with status 201 (Created) and with body the new analysisDefaultSetting, or with status 400 (Bad Request) if the analysisDefaultSetting has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/analysis-default-settings")
    @Timed
    public ResponseEntity<AnalysisDefaultSetting> createAnalysisDefaultSetting(@Valid @RequestBody AnalysisDefaultSetting analysisDefaultSetting) throws URISyntaxException {
        log.debug("REST request to save AnalysisDefaultSetting : {}", analysisDefaultSetting);
        if (analysisDefaultSetting.getId() != null) {
            throw new BadRequestAlertException("A new analysisDefaultSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnalysisDefaultSetting result = analysisDefaultSettingRepository.save(analysisDefaultSetting);
        return ResponseEntity.created(new URI("/api/analysis-default-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /analysis-default-settings : Updates an existing analysisDefaultSetting.
     *
     * @param analysisDefaultSetting the analysisDefaultSetting to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated analysisDefaultSetting,
     * or with status 400 (Bad Request) if the analysisDefaultSetting is not valid,
     * or with status 500 (Internal Server Error) if the analysisDefaultSetting couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/analysis-default-settings")
    @Timed
    public ResponseEntity<AnalysisDefaultSetting> updateAnalysisDefaultSetting(@Valid @RequestBody AnalysisDefaultSetting analysisDefaultSetting) throws URISyntaxException {
        log.debug("REST request to update AnalysisDefaultSetting : {}", analysisDefaultSetting);
        if (analysisDefaultSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnalysisDefaultSetting result = analysisDefaultSettingRepository.save(analysisDefaultSetting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, analysisDefaultSetting.getId().toString()))
            .body(result);
    }

    /**
     * GET  /analysis-default-settings : get all the analysisDefaultSettings.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of analysisDefaultSettings in body
     */
    @GetMapping("/analysis-default-settings")
    @Timed
    public ResponseEntity<List<AnalysisDefaultSetting>> getAllAnalysisDefaultSettings(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of AnalysisDefaultSettings");
        Page<AnalysisDefaultSetting> page;
        if (eagerload) {
            page = analysisDefaultSettingRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = analysisDefaultSettingRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/analysis-default-settings?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /analysis-default-settings/:id : get the "id" analysisDefaultSetting.
     *
     * @param id the id of the analysisDefaultSetting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the analysisDefaultSetting, or with status 404 (Not Found)
     */
    @GetMapping("/analysis-default-settings/{id}")
    @Timed
    public ResponseEntity<AnalysisDefaultSetting> getAnalysisDefaultSetting(@PathVariable String id) {
        log.debug("REST request to get AnalysisDefaultSetting : {}", id);
        Optional<AnalysisDefaultSetting> analysisDefaultSetting = analysisDefaultSettingRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(analysisDefaultSetting);
    }

    /**
     * DELETE  /analysis-default-settings/:id : delete the "id" analysisDefaultSetting.
     *
     * @param id the id of the analysisDefaultSetting to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/analysis-default-settings/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnalysisDefaultSetting(@PathVariable String id) {
        log.debug("REST request to delete AnalysisDefaultSetting : {}", id);

        analysisDefaultSettingRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
