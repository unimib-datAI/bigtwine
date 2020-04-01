package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronSchedule;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronScheduleRepository;
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
 * REST controller for managing CronSchedule.
 */
@RestController
@RequestMapping("/api")
public class CronScheduleResource {

    private final Logger log = LoggerFactory.getLogger(CronScheduleResource.class);

    private static final String ENTITY_NAME = "cronschedulerCronSchedule";

    private final CronScheduleRepository cronScheduleRepository;

    public CronScheduleResource(CronScheduleRepository cronScheduleRepository) {
        this.cronScheduleRepository = cronScheduleRepository;
    }

    /**
     * POST  /cron-schedules : Create a new cronSchedule.
     *
     * @param cronSchedule the cronSchedule to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cronSchedule, or with status 400 (Bad Request) if the cronSchedule has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cron-schedules")
    @Timed
    public ResponseEntity<CronSchedule> createCronSchedule(@Valid @RequestBody CronSchedule cronSchedule) throws URISyntaxException {
        log.debug("REST request to save CronSchedule : {}", cronSchedule);
        if (cronSchedule.getId() != null) {
            throw new BadRequestAlertException("A new cronSchedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CronSchedule result = cronScheduleRepository.save(cronSchedule);
        return ResponseEntity.created(new URI("/api/cron-schedules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cron-schedules : Updates an existing cronSchedule.
     *
     * @param cronSchedule the cronSchedule to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cronSchedule,
     * or with status 400 (Bad Request) if the cronSchedule is not valid,
     * or with status 500 (Internal Server Error) if the cronSchedule couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cron-schedules")
    @Timed
    public ResponseEntity<CronSchedule> updateCronSchedule(@Valid @RequestBody CronSchedule cronSchedule) throws URISyntaxException {
        log.debug("REST request to update CronSchedule : {}", cronSchedule);
        if (cronSchedule.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CronSchedule result = cronScheduleRepository.save(cronSchedule);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cronSchedule.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cron-schedules : get all the cronSchedules.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cronSchedules in body
     */
    @GetMapping("/cron-schedules")
    @Timed
    public ResponseEntity<List<CronSchedule>> getAllCronSchedules(Pageable pageable) {
        log.debug("REST request to get a page of CronSchedules");
        Page<CronSchedule> page = cronScheduleRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cron-schedules");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /cron-schedules/:id : get the "id" cronSchedule.
     *
     * @param id the id of the cronSchedule to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cronSchedule, or with status 404 (Not Found)
     */
    @GetMapping("/cron-schedules/{id}")
    @Timed
    public ResponseEntity<CronSchedule> getCronSchedule(@PathVariable String id) {
        log.debug("REST request to get CronSchedule : {}", id);
        Optional<CronSchedule> cronSchedule = cronScheduleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cronSchedule);
    }

    /**
     * DELETE  /cron-schedules/:id : delete the "id" cronSchedule.
     *
     * @param id the id of the cronSchedule to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cron-schedules/{id}")
    @Timed
    public ResponseEntity<Void> deleteCronSchedule(@PathVariable String id) {
        log.debug("REST request to delete CronSchedule : {}", id);

        cronScheduleRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
