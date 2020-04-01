package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import it.unimib.disco.bigtwine.services.cronscheduler.CronschedulerApp;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronSchedule;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronScheduleRepository;
import it.unimib.disco.bigtwine.services.cronscheduler.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static it.unimib.disco.bigtwine.services.cronscheduler.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronStatus;
/**
 * Test class for the CronScheduleResource REST controller.
 *
 * @see CronScheduleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CronschedulerApp.class)
public class CronScheduleResourceIntTest {

    private static final CronStatus DEFAULT_STATUS = CronStatus.SCHEDULED;
    private static final CronStatus UPDATED_STATUS = CronStatus.RUNNING;

    private static final Integer DEFAULT_TASKS_COUNT = 1;
    private static final Integer UPDATED_TASKS_COUNT = 2;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SCHEDULED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXECUTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINISHED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CronScheduleRepository cronScheduleRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restCronScheduleMockMvc;

    private CronSchedule cronSchedule;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CronScheduleResource cronScheduleResource = new CronScheduleResource(cronScheduleRepository);
        this.restCronScheduleMockMvc = MockMvcBuilders.standaloneSetup(cronScheduleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CronSchedule createEntity() {
        CronSchedule cronSchedule = new CronSchedule()
            .status(DEFAULT_STATUS)
            .tasksCount(DEFAULT_TASKS_COUNT)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .scheduledDate(DEFAULT_SCHEDULED_DATE)
            .executedDate(DEFAULT_EXECUTED_DATE)
            .finishedDate(DEFAULT_FINISHED_DATE);
        return cronSchedule;
    }

    @Before
    public void initTest() {
        cronScheduleRepository.deleteAll();
        cronSchedule = createEntity();
    }

    @Test
    public void createCronSchedule() throws Exception {
        int databaseSizeBeforeCreate = cronScheduleRepository.findAll().size();

        // Create the CronSchedule
        restCronScheduleMockMvc.perform(post("/api/cron-schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronSchedule)))
            .andExpect(status().isCreated());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeCreate + 1);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCronSchedule.getTasksCount()).isEqualTo(DEFAULT_TASKS_COUNT);
        assertThat(testCronSchedule.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testCronSchedule.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testCronSchedule.getScheduledDate()).isEqualTo(DEFAULT_SCHEDULED_DATE);
        assertThat(testCronSchedule.getExecutedDate()).isEqualTo(DEFAULT_EXECUTED_DATE);
        assertThat(testCronSchedule.getFinishedDate()).isEqualTo(DEFAULT_FINISHED_DATE);
    }

    @Test
    public void createCronScheduleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cronScheduleRepository.findAll().size();

        // Create the CronSchedule with an existing ID
        cronSchedule.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronScheduleMockMvc.perform(post("/api/cron-schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronSchedule)))
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronScheduleRepository.findAll().size();
        // set the field null
        cronSchedule.setStatus(null);

        // Create the CronSchedule, which fails.

        restCronScheduleMockMvc.perform(post("/api/cron-schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronSchedule)))
            .andExpect(status().isBadRequest());

        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllCronSchedules() throws Exception {
        // Initialize the database
        cronScheduleRepository.save(cronSchedule);

        // Get all the cronScheduleList
        restCronScheduleMockMvc.perform(get("/api/cron-schedules?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronSchedule.getId())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].tasksCount").value(hasItem(DEFAULT_TASKS_COUNT)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(DEFAULT_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].scheduledDate").value(hasItem(DEFAULT_SCHEDULED_DATE.toString())))
            .andExpect(jsonPath("$.[*].executedDate").value(hasItem(DEFAULT_EXECUTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].finishedDate").value(hasItem(DEFAULT_FINISHED_DATE.toString())));
    }
    
    @Test
    public void getCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.save(cronSchedule);

        // Get the cronSchedule
        restCronScheduleMockMvc.perform(get("/api/cron-schedules/{id}", cronSchedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cronSchedule.getId()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.tasksCount").value(DEFAULT_TASKS_COUNT))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.updatedDate").value(DEFAULT_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.scheduledDate").value(DEFAULT_SCHEDULED_DATE.toString()))
            .andExpect(jsonPath("$.executedDate").value(DEFAULT_EXECUTED_DATE.toString()))
            .andExpect(jsonPath("$.finishedDate").value(DEFAULT_FINISHED_DATE.toString()));
    }

    @Test
    public void getNonExistingCronSchedule() throws Exception {
        // Get the cronSchedule
        restCronScheduleMockMvc.perform(get("/api/cron-schedules/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.save(cronSchedule);

        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();

        // Update the cronSchedule
        CronSchedule updatedCronSchedule = cronScheduleRepository.findById(cronSchedule.getId()).get();
        updatedCronSchedule
            .status(UPDATED_STATUS)
            .tasksCount(UPDATED_TASKS_COUNT)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .scheduledDate(UPDATED_SCHEDULED_DATE)
            .executedDate(UPDATED_EXECUTED_DATE)
            .finishedDate(UPDATED_FINISHED_DATE);

        restCronScheduleMockMvc.perform(put("/api/cron-schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCronSchedule)))
            .andExpect(status().isOk());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
        CronSchedule testCronSchedule = cronScheduleList.get(cronScheduleList.size() - 1);
        assertThat(testCronSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCronSchedule.getTasksCount()).isEqualTo(UPDATED_TASKS_COUNT);
        assertThat(testCronSchedule.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testCronSchedule.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCronSchedule.getScheduledDate()).isEqualTo(UPDATED_SCHEDULED_DATE);
        assertThat(testCronSchedule.getExecutedDate()).isEqualTo(UPDATED_EXECUTED_DATE);
        assertThat(testCronSchedule.getFinishedDate()).isEqualTo(UPDATED_FINISHED_DATE);
    }

    @Test
    public void updateNonExistingCronSchedule() throws Exception {
        int databaseSizeBeforeUpdate = cronScheduleRepository.findAll().size();

        // Create the CronSchedule

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronScheduleMockMvc.perform(put("/api/cron-schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronSchedule)))
            .andExpect(status().isBadRequest());

        // Validate the CronSchedule in the database
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCronSchedule() throws Exception {
        // Initialize the database
        cronScheduleRepository.save(cronSchedule);

        int databaseSizeBeforeDelete = cronScheduleRepository.findAll().size();

        // Get the cronSchedule
        restCronScheduleMockMvc.perform(delete("/api/cron-schedules/{id}", cronSchedule.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CronSchedule> cronScheduleList = cronScheduleRepository.findAll();
        assertThat(cronScheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronSchedule.class);
        CronSchedule cronSchedule1 = new CronSchedule();
        cronSchedule1.setId("id1");
        CronSchedule cronSchedule2 = new CronSchedule();
        cronSchedule2.setId(cronSchedule1.getId());
        assertThat(cronSchedule1).isEqualTo(cronSchedule2);
        cronSchedule2.setId("id2");
        assertThat(cronSchedule1).isNotEqualTo(cronSchedule2);
        cronSchedule1.setId(null);
        assertThat(cronSchedule1).isNotEqualTo(cronSchedule2);
    }
}
