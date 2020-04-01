package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import it.unimib.disco.bigtwine.services.cronscheduler.CronschedulerApp;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronTask;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronTaskRepository;
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

import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronTaskStatus;
/**
 * Test class for the CronTaskResource REST controller.
 *
 * @see CronTaskResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CronschedulerApp.class)
public class CronTaskResourceIntTest {

    private static final Integer DEFAULT_TASK = 1;
    private static final Integer UPDATED_TASK = 2;

    private static final CronTaskStatus DEFAULT_STATUS = CronTaskStatus.RUNNING;
    private static final CronTaskStatus UPDATED_STATUS = CronTaskStatus.FAILED;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXECUTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINISHED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CronTaskRepository cronTaskRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restCronTaskMockMvc;

    private CronTask cronTask;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CronTaskResource cronTaskResource = new CronTaskResource(cronTaskRepository);
        this.restCronTaskMockMvc = MockMvcBuilders.standaloneSetup(cronTaskResource)
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
    public static CronTask createEntity() {
        CronTask cronTask = new CronTask()
            .task(DEFAULT_TASK)
            .status(DEFAULT_STATUS)
            .message(DEFAULT_MESSAGE)
            .executedDate(DEFAULT_EXECUTED_DATE)
            .finishedDate(DEFAULT_FINISHED_DATE);
        return cronTask;
    }

    @Before
    public void initTest() {
        cronTaskRepository.deleteAll();
        cronTask = createEntity();
    }

    @Test
    public void createCronTask() throws Exception {
        int databaseSizeBeforeCreate = cronTaskRepository.findAll().size();

        // Create the CronTask
        restCronTaskMockMvc.perform(post("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronTask)))
            .andExpect(status().isCreated());

        // Validate the CronTask in the database
        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeCreate + 1);
        CronTask testCronTask = cronTaskList.get(cronTaskList.size() - 1);
        assertThat(testCronTask.getTask()).isEqualTo(DEFAULT_TASK);
        assertThat(testCronTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCronTask.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testCronTask.getExecutedDate()).isEqualTo(DEFAULT_EXECUTED_DATE);
        assertThat(testCronTask.getFinishedDate()).isEqualTo(DEFAULT_FINISHED_DATE);
    }

    @Test
    public void createCronTaskWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cronTaskRepository.findAll().size();

        // Create the CronTask with an existing ID
        cronTask.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronTaskMockMvc.perform(post("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronTask)))
            .andExpect(status().isBadRequest());

        // Validate the CronTask in the database
        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkTaskIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronTaskRepository.findAll().size();
        // set the field null
        cronTask.setTask(null);

        // Create the CronTask, which fails.

        restCronTaskMockMvc.perform(post("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronTask)))
            .andExpect(status().isBadRequest());

        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronTaskRepository.findAll().size();
        // set the field null
        cronTask.setStatus(null);

        // Create the CronTask, which fails.

        restCronTaskMockMvc.perform(post("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronTask)))
            .andExpect(status().isBadRequest());

        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllCronTasks() throws Exception {
        // Initialize the database
        cronTaskRepository.save(cronTask);

        // Get all the cronTaskList
        restCronTaskMockMvc.perform(get("/api/cron-tasks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronTask.getId())))
            .andExpect(jsonPath("$.[*].task").value(hasItem(DEFAULT_TASK)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].executedDate").value(hasItem(DEFAULT_EXECUTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].finishedDate").value(hasItem(DEFAULT_FINISHED_DATE.toString())));
    }
    
    @Test
    public void getCronTask() throws Exception {
        // Initialize the database
        cronTaskRepository.save(cronTask);

        // Get the cronTask
        restCronTaskMockMvc.perform(get("/api/cron-tasks/{id}", cronTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cronTask.getId()))
            .andExpect(jsonPath("$.task").value(DEFAULT_TASK))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE.toString()))
            .andExpect(jsonPath("$.executedDate").value(DEFAULT_EXECUTED_DATE.toString()))
            .andExpect(jsonPath("$.finishedDate").value(DEFAULT_FINISHED_DATE.toString()));
    }

    @Test
    public void getNonExistingCronTask() throws Exception {
        // Get the cronTask
        restCronTaskMockMvc.perform(get("/api/cron-tasks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCronTask() throws Exception {
        // Initialize the database
        cronTaskRepository.save(cronTask);

        int databaseSizeBeforeUpdate = cronTaskRepository.findAll().size();

        // Update the cronTask
        CronTask updatedCronTask = cronTaskRepository.findById(cronTask.getId()).get();
        updatedCronTask
            .task(UPDATED_TASK)
            .status(UPDATED_STATUS)
            .message(UPDATED_MESSAGE)
            .executedDate(UPDATED_EXECUTED_DATE)
            .finishedDate(UPDATED_FINISHED_DATE);

        restCronTaskMockMvc.perform(put("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCronTask)))
            .andExpect(status().isOk());

        // Validate the CronTask in the database
        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeUpdate);
        CronTask testCronTask = cronTaskList.get(cronTaskList.size() - 1);
        assertThat(testCronTask.getTask()).isEqualTo(UPDATED_TASK);
        assertThat(testCronTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCronTask.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testCronTask.getExecutedDate()).isEqualTo(UPDATED_EXECUTED_DATE);
        assertThat(testCronTask.getFinishedDate()).isEqualTo(UPDATED_FINISHED_DATE);
    }

    @Test
    public void updateNonExistingCronTask() throws Exception {
        int databaseSizeBeforeUpdate = cronTaskRepository.findAll().size();

        // Create the CronTask

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronTaskMockMvc.perform(put("/api/cron-tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronTask)))
            .andExpect(status().isBadRequest());

        // Validate the CronTask in the database
        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCronTask() throws Exception {
        // Initialize the database
        cronTaskRepository.save(cronTask);

        int databaseSizeBeforeDelete = cronTaskRepository.findAll().size();

        // Get the cronTask
        restCronTaskMockMvc.perform(delete("/api/cron-tasks/{id}", cronTask.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CronTask> cronTaskList = cronTaskRepository.findAll();
        assertThat(cronTaskList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronTask.class);
        CronTask cronTask1 = new CronTask();
        cronTask1.setId("id1");
        CronTask cronTask2 = new CronTask();
        cronTask2.setId(cronTask1.getId());
        assertThat(cronTask1).isEqualTo(cronTask2);
        cronTask2.setId("id2");
        assertThat(cronTask1).isNotEqualTo(cronTask2);
        cronTask1.setId(null);
        assertThat(cronTask1).isNotEqualTo(cronTask2);
    }
}
