package it.unimib.disco.bigtwine.services.analysis.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingCollection;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisSettingCollectionRepository;
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
 * REST controller for managing AnalysisSettingCollection.
 */
@RestController
@RequestMapping("/api")
public class AnalysisSettingCollectionResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisSettingCollectionResource.class);

    private static final String ENTITY_NAME = "analysisAnalysisSettingCollection";

    private final AnalysisSettingCollectionRepository analysisSettingCollectionRepository;

    public AnalysisSettingCollectionResource(AnalysisSettingCollectionRepository analysisSettingCollectionRepository) {
        this.analysisSettingCollectionRepository = analysisSettingCollectionRepository;
    }

    /**
     * POST  /analysis-setting-collections : Create a new analysisSettingCollection.
     *
     * @param analysisSettingCollection the analysisSettingCollection to create
     * @return the ResponseEntity with status 201 (Created) and with body the new analysisSettingCollection, or with status 400 (Bad Request) if the analysisSettingCollection has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/analysis-setting-collections")
    @Timed
    public ResponseEntity<AnalysisSettingCollection> createAnalysisSettingCollection(@Valid @RequestBody AnalysisSettingCollection analysisSettingCollection) throws URISyntaxException {
        log.debug("REST request to save AnalysisSettingCollection : {}", analysisSettingCollection);
        if (analysisSettingCollection.getId() != null) {
            throw new BadRequestAlertException("A new analysisSettingCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnalysisSettingCollection result = analysisSettingCollectionRepository.save(analysisSettingCollection);
        return ResponseEntity.created(new URI("/api/analysis-setting-collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /analysis-setting-collections : Updates an existing analysisSettingCollection.
     *
     * @param analysisSettingCollection the analysisSettingCollection to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated analysisSettingCollection,
     * or with status 400 (Bad Request) if the analysisSettingCollection is not valid,
     * or with status 500 (Internal Server Error) if the analysisSettingCollection couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/analysis-setting-collections")
    @Timed
    public ResponseEntity<AnalysisSettingCollection> updateAnalysisSettingCollection(@Valid @RequestBody AnalysisSettingCollection analysisSettingCollection) throws URISyntaxException {
        log.debug("REST request to update AnalysisSettingCollection : {}", analysisSettingCollection);
        if (analysisSettingCollection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnalysisSettingCollection result = analysisSettingCollectionRepository.save(analysisSettingCollection);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, analysisSettingCollection.getId().toString()))
            .body(result);
    }

    /**
     * GET  /analysis-setting-collections : get all the analysisSettingCollections.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of analysisSettingCollections in body
     */
    @GetMapping("/analysis-setting-collections")
    @Timed
    public ResponseEntity<List<AnalysisSettingCollection>> getAllAnalysisSettingCollections(Pageable pageable) {
        log.debug("REST request to get a page of AnalysisSettingCollections");
        Page<AnalysisSettingCollection> page = analysisSettingCollectionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/analysis-setting-collections");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /analysis-setting-collections/:id : get the "id" analysisSettingCollection.
     *
     * @param id the id of the analysisSettingCollection to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the analysisSettingCollection, or with status 404 (Not Found)
     */
    @GetMapping("/analysis-setting-collections/{id}")
    @Timed
    public ResponseEntity<AnalysisSettingCollection> getAnalysisSettingCollection(@PathVariable String id) {
        log.debug("REST request to get AnalysisSettingCollection : {}", id);
        Optional<AnalysisSettingCollection> analysisSettingCollection = analysisSettingCollectionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(analysisSettingCollection);
    }

    /**
     * DELETE  /analysis-setting-collections/:id : delete the "id" analysisSettingCollection.
     *
     * @param id the id of the analysisSettingCollection to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/analysis-setting-collections/{id}")
    @Timed
    public ResponseEntity<Void> deleteAnalysisSettingCollection(@PathVariable String id) {
        log.debug("REST request to delete AnalysisSettingCollection : {}", id);

        analysisSettingCollectionRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
