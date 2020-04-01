package it.unimib.disco.bigtwine.services.analysis.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.security.AuthoritiesConstants;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisService;
import it.unimib.disco.bigtwine.services.analysis.web.rest.errors.BadRequestAlertException;
import it.unimib.disco.bigtwine.services.analysis.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import it.unimib.disco.bigtwine.services.analysis.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Analysis.
 */
@RestController
@RequestMapping("/api")
public class AnalysisResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisResource.class);

    private static final String ENTITY_NAME = "analysisAnalysis";

    private final AnalysisService analysisService;

    public AnalysisResource(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * POST  /analyses : Create a new analysis.
     *
     * @param analysis the analysis to create
     * @return the ResponseEntity with status 201 (Created) and with body the new analysis, or with status 400 (Bad Request) if the analysis has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/analyses")
    @Timed
    public ResponseEntity<Analysis> createAnalysis(@Valid @RequestBody Analysis analysis) throws URISyntaxException {
        log.debug("REST request to save Analysis : {}", analysis);
        if (analysis.getId() != null) {
            throw new BadRequestAlertException("A new analysis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Analysis result = analysisService.save(analysis);
        return ResponseEntity.created(new URI("/api/analyses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /analyses : Updates an existing analysis.
     *
     * @param analysis the analysis to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated analysis,
     * or with status 400 (Bad Request) if the analysis is not valid,
     * or with status 500 (Internal Server Error) if the analysis couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/analyses")
    @Timed
    public ResponseEntity<Analysis> updateAnalysis(@Valid @RequestBody Analysis analysis) throws URISyntaxException {
        log.debug("REST request to update Analysis : {}", analysis);
        if (analysis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Analysis result = analysisService.save(analysis);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, analysis.getId().toString()))
            .body(result);
    }

    /**
     * GET  /analyses : get all the analyses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of analyses in body
     */
    @GetMapping("/analyses")
    @Timed
    public ResponseEntity<List<Analysis>> getAllAnalyses(Pageable pageable) {
        log.debug("REST request to get all Analyses");
        Page<Analysis> page =  analysisService.findAll(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/analyses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /analyses/:id : get the "id" analysis.
     *
     * @param id the id of the analysis to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the analysis, or with status 404 (Not Found)
     */
    @GetMapping("/analyses/{id}")
    @Timed
    public ResponseEntity<Analysis> getAnalysis(@PathVariable String id) {
        log.debug("REST request to get Analysis : {}", id);
        Optional<Analysis> analysis = analysisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(analysis);
    }

    /**
     * DELETE  /analyses/:id : delete the "id" analysis.
     *
     * @param id the id of the analysis to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/analyses/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnalysis(@PathVariable String id) {
        log.debug("REST request to delete Analysis : {}", id);
        analysisService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
