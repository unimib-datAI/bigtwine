package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronTask;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronTaskRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.errors.BadRequestAlertException;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing CronTask.
 */
@RestController
@RequestMapping("/api")
public class CronTaskResource {

    private final Logger log = LoggerFactory.getLogger(CronTaskResource.class);

    private static final String ENTITY_NAME = "cronschedulerCronTask";

    private final CronTaskRepository cronTaskRepository;

    public CronTaskResource(CronTaskRepository cronTaskRepository) {
        this.cronTaskRepository = cronTaskRepository;
    }

    /**
     * POST  /cron-tasks : Create a new cronTask.
     *
     * @param cronTask the cronTask to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cronTask, or with status 400 (Bad Request) if the cronTask has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cron-tasks")
    @Timed
    public ResponseEntity<CronTask> createCronTask(@Valid @RequestBody CronTask cronTask) throws URISyntaxException {
        log.debug("REST request to save CronTask : {}", cronTask);
        if (cronTask.getId() != null) {
            throw new BadRequestAlertException("A new cronTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CronTask result = cronTaskRepository.save(cronTask);
        return ResponseEntity.created(new URI("/api/cron-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cron-tasks : Updates an existing cronTask.
     *
     * @param cronTask the cronTask to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cronTask,
     * or with status 400 (Bad Request) if the cronTask is not valid,
     * or with status 500 (Internal Server Error) if the cronTask couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cron-tasks")
    @Timed
    public ResponseEntity<CronTask> updateCronTask(@Valid @RequestBody CronTask cronTask) throws URISyntaxException {
        log.debug("REST request to update CronTask : {}", cronTask);
        if (cronTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CronTask result = cronTaskRepository.save(cronTask);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cronTask.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cron-tasks : get all the cronTasks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cronTasks in body
     */
    @GetMapping("/cron-tasks")
    @Timed
    public List<CronTask> getAllCronTasks() {
        log.debug("REST request to get all CronTasks");
        return cronTaskRepository.findAll();
    }

    /**
     * GET  /cron-tasks/:id : get the "id" cronTask.
     *
     * @param id the id of the cronTask to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cronTask, or with status 404 (Not Found)
     */
    @GetMapping("/cron-tasks/{id}")
    @Timed
    public ResponseEntity<CronTask> getCronTask(@PathVariable String id) {
        log.debug("REST request to get CronTask : {}", id);
        Optional<CronTask> cronTask = cronTaskRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cronTask);
    }

    /**
     * DELETE  /cron-tasks/:id : delete the "id" cronTask.
     *
     * @param id the id of the cronTask to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cron-tasks/{id}")
    @Timed
    public ResponseEntity<Void> deleteCronTask(@PathVariable String id) {
        log.debug("REST request to delete CronTask : {}", id);

        cronTaskRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
