package it.unimib.disco.bigtwine.services.cronscheduler.web.rest;

import it.unimib.disco.bigtwine.services.cronscheduler.CronschedulerApp;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronEntry;
import it.unimib.disco.bigtwine.services.cronscheduler.repository.CronEntryRepository;
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

/**
 * Test class for the CronEntryResource REST controller.
 *
 * @see CronEntryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CronschedulerApp.class)
public class CronEntryResourceIntTest {

    private static final String DEFAULT_SERVICE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_GROUP = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PARALLELISM = 0;
    private static final Integer UPDATED_PARALLELISM = 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_CRON_EXPR = "AAAAAAAAAA";
    private static final String UPDATED_CRON_EXPR = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_RUN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_RUN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CronEntryRepository cronEntryRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restCronEntryMockMvc;

    private CronEntry cronEntry;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CronEntryResource cronEntryResource = new CronEntryResource(cronEntryRepository);
        this.restCronEntryMockMvc = MockMvcBuilders.standaloneSetup(cronEntryResource)
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
    public static CronEntry createEntity() {
        CronEntry cronEntry = new CronEntry()
            .service(DEFAULT_SERVICE)
            .group(DEFAULT_GROUP)
            .name(DEFAULT_NAME)
            .parallelism(DEFAULT_PARALLELISM)
            .active(DEFAULT_ACTIVE)
            .cronExpr(DEFAULT_CRON_EXPR)
            .lastRun(DEFAULT_LAST_RUN);
        return cronEntry;
    }

    @Before
    public void initTest() {
        cronEntryRepository.deleteAll();
        cronEntry = createEntity();
    }

    @Test
    public void createCronEntry() throws Exception {
        int databaseSizeBeforeCreate = cronEntryRepository.findAll().size();

        // Create the CronEntry
        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isCreated());

        // Validate the CronEntry in the database
        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeCreate + 1);
        CronEntry testCronEntry = cronEntryList.get(cronEntryList.size() - 1);
        assertThat(testCronEntry.getService()).isEqualTo(DEFAULT_SERVICE);
        assertThat(testCronEntry.getGroup()).isEqualTo(DEFAULT_GROUP);
        assertThat(testCronEntry.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCronEntry.getParallelism()).isEqualTo(DEFAULT_PARALLELISM);
        assertThat(testCronEntry.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testCronEntry.getCronExpr()).isEqualTo(DEFAULT_CRON_EXPR);
        assertThat(testCronEntry.getLastRun()).isEqualTo(DEFAULT_LAST_RUN);
    }

    @Test
    public void createCronEntryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cronEntryRepository.findAll().size();

        // Create the CronEntry with an existing ID
        cronEntry.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        // Validate the CronEntry in the database
        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkServiceIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronEntryRepository.findAll().size();
        // set the field null
        cronEntry.setService(null);

        // Create the CronEntry, which fails.

        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkGroupIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronEntryRepository.findAll().size();
        // set the field null
        cronEntry.setGroup(null);

        // Create the CronEntry, which fails.

        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronEntryRepository.findAll().size();
        // set the field null
        cronEntry.setName(null);

        // Create the CronEntry, which fails.

        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkParallelismIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronEntryRepository.findAll().size();
        // set the field null
        cronEntry.setParallelism(null);

        // Create the CronEntry, which fails.

        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCronExprIsRequired() throws Exception {
        int databaseSizeBeforeTest = cronEntryRepository.findAll().size();
        // set the field null
        cronEntry.setCronExpr(null);

        // Create the CronEntry, which fails.

        restCronEntryMockMvc.perform(post("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllCronEntries() throws Exception {
        // Initialize the database
        cronEntryRepository.save(cronEntry);

        // Get all the cronEntryList
        restCronEntryMockMvc.perform(get("/api/cron-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cronEntry.getId())))
            .andExpect(jsonPath("$.[*].service").value(hasItem(DEFAULT_SERVICE.toString())))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].parallelism").value(hasItem(DEFAULT_PARALLELISM)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].cronExpr").value(hasItem(DEFAULT_CRON_EXPR.toString())))
            .andExpect(jsonPath("$.[*].lastRun").value(hasItem(DEFAULT_LAST_RUN.toString())));
    }
    
    @Test
    public void getCronEntry() throws Exception {
        // Initialize the database
        cronEntryRepository.save(cronEntry);

        // Get the cronEntry
        restCronEntryMockMvc.perform(get("/api/cron-entries/{id}", cronEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cronEntry.getId()))
            .andExpect(jsonPath("$.service").value(DEFAULT_SERVICE.toString()))
            .andExpect(jsonPath("$.group").value(DEFAULT_GROUP.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.parallelism").value(DEFAULT_PARALLELISM))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.cronExpr").value(DEFAULT_CRON_EXPR.toString()))
            .andExpect(jsonPath("$.lastRun").value(DEFAULT_LAST_RUN.toString()));
    }

    @Test
    public void getNonExistingCronEntry() throws Exception {
        // Get the cronEntry
        restCronEntryMockMvc.perform(get("/api/cron-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCronEntry() throws Exception {
        // Initialize the database
        cronEntryRepository.save(cronEntry);

        int databaseSizeBeforeUpdate = cronEntryRepository.findAll().size();

        // Update the cronEntry
        CronEntry updatedCronEntry = cronEntryRepository.findById(cronEntry.getId()).get();
        updatedCronEntry
            .service(UPDATED_SERVICE)
            .group(UPDATED_GROUP)
            .name(UPDATED_NAME)
            .parallelism(UPDATED_PARALLELISM)
            .active(UPDATED_ACTIVE)
            .cronExpr(UPDATED_CRON_EXPR)
            .lastRun(UPDATED_LAST_RUN);

        restCronEntryMockMvc.perform(put("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCronEntry)))
            .andExpect(status().isOk());

        // Validate the CronEntry in the database
        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeUpdate);
        CronEntry testCronEntry = cronEntryList.get(cronEntryList.size() - 1);
        assertThat(testCronEntry.getService()).isEqualTo(UPDATED_SERVICE);
        assertThat(testCronEntry.getGroup()).isEqualTo(UPDATED_GROUP);
        assertThat(testCronEntry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCronEntry.getParallelism()).isEqualTo(UPDATED_PARALLELISM);
        assertThat(testCronEntry.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testCronEntry.getCronExpr()).isEqualTo(UPDATED_CRON_EXPR);
        assertThat(testCronEntry.getLastRun()).isEqualTo(UPDATED_LAST_RUN);
    }

    @Test
    public void updateNonExistingCronEntry() throws Exception {
        int databaseSizeBeforeUpdate = cronEntryRepository.findAll().size();

        // Create the CronEntry

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCronEntryMockMvc.perform(put("/api/cron-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cronEntry)))
            .andExpect(status().isBadRequest());

        // Validate the CronEntry in the database
        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCronEntry() throws Exception {
        // Initialize the database
        cronEntryRepository.save(cronEntry);

        int databaseSizeBeforeDelete = cronEntryRepository.findAll().size();

        // Get the cronEntry
        restCronEntryMockMvc.perform(delete("/api/cron-entries/{id}", cronEntry.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CronEntry> cronEntryList = cronEntryRepository.findAll();
        assertThat(cronEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CronEntry.class);
        CronEntry cronEntry1 = new CronEntry();
        cronEntry1.setId("id1");
        CronEntry cronEntry2 = new CronEntry();
        cronEntry2.setId(cronEntry1.getId());
        assertThat(cronEntry1).isEqualTo(cronEntry2);
        cronEntry2.setId("id2");
        assertThat(cronEntry1).isNotEqualTo(cronEntry2);
        cronEntry1.setId(null);
        assertThat(cronEntry1).isNotEqualTo(cronEntry2);
    }
}
