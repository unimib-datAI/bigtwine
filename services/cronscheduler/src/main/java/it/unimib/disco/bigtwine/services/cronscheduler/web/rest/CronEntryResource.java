package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronEntry;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronEntryRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.errors.BadRequestAlertException;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.util.HeaderUtil;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.util.PaginationUtil;
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
 * REST controller for managing CronEntry.
 */
@RestController
@RequestMapping("/api")
public class CronEntryResource {

    private final Logger log = LoggerFactory.getLogger(CronEntryResource.class);

    private static final String ENTITY_NAME = "cronschedulerCronEntry";

    private final CronEntryRepository cronEntryRepository;

    public CronEntryResource(CronEntryRepository cronEntryRepository) {
        this.cronEntryRepository = cronEntryRepository;
    }

    /**
     * POST  /cron-entries : Create a new cronEntry.
     *
     * @param cronEntry the cronEntry to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cronEntry, or with status 400 (Bad Request) if the cronEntry has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cron-entries")
    @Timed
    public ResponseEntity<CronEntry> createCronEntry(@Valid @RequestBody CronEntry cronEntry) throws URISyntaxException {
        log.debug("REST request to save CronEntry : {}", cronEntry);
        if (cronEntry.getId() != null) {
            throw new BadRequestAlertException("A new cronEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CronEntry result = cronEntryRepository.save(cronEntry);
        return ResponseEntity.created(new URI("/api/cron-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cron-entries : Updates an existing cronEntry.
     *
     * @param cronEntry the cronEntry to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cronEntry,
     * or with status 400 (Bad Request) if the cronEntry is not valid,
     * or with status 500 (Internal Server Error) if the cronEntry couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cron-entries")
    @Timed
    public ResponseEntity<CronEntry> updateCronEntry(@Valid @RequestBody CronEntry cronEntry) throws URISyntaxException {
        log.debug("REST request to update CronEntry : {}", cronEntry);
        if (cronEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CronEntry result = cronEntryRepository.save(cronEntry);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cronEntry.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cron-entries : get all the cronEntries.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cronEntries in body
     */
    @GetMapping("/cron-entries")
    @Timed
    public ResponseEntity<List<CronEntry>> getAllCronEntries(Pageable pageable) {
        log.debug("REST request to get a page of CronEntries");
        Page<CronEntry> page = cronEntryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cron-entries");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /cron-entries/:id : get the "id" cronEntry.
     *
     * @param id the id of the cronEntry to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cronEntry, or with status 404 (Not Found)
     */
    @GetMapping("/cron-entries/{id}")
    @Timed
    public ResponseEntity<CronEntry> getCronEntry(@PathVariable String id) {
        log.debug("REST request to get CronEntry : {}", id);
        Optional<CronEntry> cronEntry = cronEntryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cronEntry);
    }

    /**
     * DELETE  /cron-entries/:id : delete the "id" cronEntry.
     *
     * @param id the id of the cronEntry to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cron-entries/{id}")
    @Timed
    public ResponseEntity<Void> deleteCronEntry(@PathVariable String id) {
        log.debug("REST request to delete CronEntry : {}", id);

        cronEntryRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
