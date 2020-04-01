package it.unimib.disco.bigtwine.services.analysis.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisSettingService;
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
 * REST controller for managing AnalysisSetting.
 */
@RestController
@RequestMapping("/api")
public class AnalysisSettingResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisSettingResource.class);

    private static final String ENTITY_NAME = "analysisAnalysisSetting";

    private final AnalysisSettingService analysisSettingService;

    public AnalysisSettingResource(AnalysisSettingService analysisSettingService) {
        this.analysisSettingService = analysisSettingService;
    }

    /**
     * POST  /analysis-settings : Create a new analysisSetting.
     *
     * @param analysisSetting the analysisSetting to create
     * @return the ResponseEntity with status 201 (Created) and with body the new analysisSetting, or with status 400 (Bad Request) if the analysisSetting has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/analysis-settings")
    @Timed
    public ResponseEntity<AnalysisSetting> createAnalysisSetting(@Valid @RequestBody AnalysisSetting analysisSetting) throws URISyntaxException {
        log.debug("REST request to save AnalysisSetting : {}", analysisSetting);
        if (analysisSetting.getId() != null) {
            throw new BadRequestAlertException("A new analysisSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnalysisSetting result = analysisSettingService.save(analysisSetting);
        return ResponseEntity.created(new URI("/api/analysis-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /analysis-settings : Updates an existing analysisSetting.
     *
     * @param analysisSetting the analysisSetting to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated analysisSetting,
     * or with status 400 (Bad Request) if the analysisSetting is not valid,
     * or with status 500 (Internal Server Error) if the analysisSetting couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/analysis-settings")
    @Timed
    public ResponseEntity<AnalysisSetting> updateAnalysisSetting(@Valid @RequestBody AnalysisSetting analysisSetting) throws URISyntaxException {
        log.debug("REST request to update AnalysisSetting : {}", analysisSetting);
        if (analysisSetting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnalysisSetting result = analysisSettingService.save(analysisSetting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, analysisSetting.getId().toString()))
            .body(result);
    }

    /**
     * GET  /analysis-settings : get all the analysisSettings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of analysisSettings in body
     */
    @GetMapping("/analysis-settings")
    @Timed
    public ResponseEntity<List<AnalysisSetting>> getAllAnalysisSettings(Pageable pageable) {
        log.debug("REST request to get a page of AnalysisSettings");
        Page<AnalysisSetting> page = analysisSettingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/analysis-settings");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /analysis-settings/:id : get the "id" analysisSetting.
     *
     * @param id the id of the analysisSetting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the analysisSetting, or with status 404 (Not Found)
     */
    @GetMapping("/analysis-settings/{id}")
    @Timed
    public ResponseEntity<AnalysisSetting> getAnalysisSetting(@PathVariable String id) {
        log.debug("REST request to get AnalysisSetting : {}", id);
        Optional<AnalysisSetting> analysisSetting = analysisSettingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(analysisSetting);
    }

    /**
     * DELETE  /analysis-settings/:id : delete the "id" analysisSetting.
     *
     * @param id the id of the analysisSetting to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/analysis-settings/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnalysisSetting(@PathVariable String id) {
        log.debug("REST request to delete AnalysisSetting : {}", id);
        analysisSettingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
