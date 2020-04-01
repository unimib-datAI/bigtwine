package it.unimib.disco.bigtwine.services.analysis.web.rest;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;

import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.DatasetAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisRepository;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisService;
import it.unimib.disco.bigtwine.services.analysis.web.rest.errors.ExceptionTranslator;

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
import java.util.Arrays;
import java.util.List;


import static it.unimib.disco.bigtwine.services.analysis.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
/**
 * Test class for the AnalysisResource REST controller.
 *
 * @see AnalysisResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class AnalysisResourceIntTest {

    private static final AnalysisType DEFAULT_TYPE = AnalysisType.TWITTER_NEEL;
    private static final AnalysisType UPDATED_TYPE = AnalysisType.TWITTER_NEEL;

    private static final AnalysisStatus DEFAULT_STATUS = AnalysisStatus.READY;
    private static final AnalysisStatus UPDATED_STATUS = AnalysisStatus.STARTED;

    private static final AnalysisVisibility DEFAULT_VISIBILITY = AnalysisVisibility.PRIVATE;
    private static final AnalysisVisibility UPDATED_VISIBILITY = AnalysisVisibility.PUBLIC;

    private static final User DEFAULT_OWNER = new User().uid("AAAAAAAAAA").username("AAAAAAAAAA");
    private static final User UPDATED_OWNER = new User().uid("BBBBBBBBBB").username("BBBBBBBBBB");

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final QueryAnalysisInput DEFAULT_INPUT = new QueryAnalysisInput()
        .tokens(Arrays.asList("AAAAA", "AAAAA"))
        .joinOperator(QueryAnalysisInput.JoinOperator.ANY);
    private static final DatasetAnalysisInput UPDATED_INPUT = new DatasetAnalysisInput()
        .documentId("AAAAA");

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restAnalysisMockMvc;

    private Analysis analysis;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnalysisResource analysisResource = new AnalysisResource(analysisService);
        this.restAnalysisMockMvc = MockMvcBuilders.standaloneSetup(analysisResource)
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
    public static Analysis createEntity() {
        return new Analysis()
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .visibility(DEFAULT_VISIBILITY)
            .owner(DEFAULT_OWNER)
            .createDate(DEFAULT_CREATE_DATE)
            .updateDate(DEFAULT_UPDATE_DATE)
            .input(DEFAULT_INPUT);
    }

    @Before
    public void initTest() {
        analysisRepository.deleteAll();
        analysis = createEntity();
    }

    @Test
    public void createAnalysis() throws Exception {
        int databaseSizeBeforeCreate = analysisRepository.findAll().size();

        // Create the Analysis
        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isCreated());

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeCreate + 1);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAnalysis.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAnalysis.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testAnalysis.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testAnalysis.getCreateDate()).isNotNull();
        assertThat(testAnalysis.getUpdateDate()).isNotNull();
        assertThat(testAnalysis.getInput()).isEqualTo(DEFAULT_INPUT);
    }

    @Test
    public void createAnalysisWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = analysisRepository.findAll().size();

        // Create the Analysis with an existing ID
        analysis.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setType(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setStatus(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkVisibilityIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setVisibility(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkOwnerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setOwner(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCreateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setCreateDate(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkUpdateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().size();
        // set the field null
        analysis.setUpdateDate(null);

        // Create the Analysis, which fails.

        restAnalysisMockMvc.perform(post("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllAnalyses() throws Exception {
        // Initialize the database
        analysisRepository.save(analysis);

        // Get all the analysisList
        restAnalysisMockMvc.perform(get("/api/analyses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(analysis.getId())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())))
            .andExpect(jsonPath("$.[*].owner.id").value(hasItem(DEFAULT_OWNER.getUid())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].input.tokens").value(hasItem(DEFAULT_INPUT.getTokens())));
    }

    @Test
    public void getAnalysis() throws Exception {
        // Initialize the database
        analysisRepository.save(analysis);

        // Get the analysis
        restAnalysisMockMvc.perform(get("/api/analyses/{id}", analysis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(analysis.getId()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.input").isNotEmpty())
            .andExpect(jsonPath("$.input.tokens").isArray())
            .andExpect(jsonPath("$.input.tokens", is(DEFAULT_INPUT.getTokens())));
    }

    @Test
    public void getNonExistingAnalysis() throws Exception {
        // Get the analysis
        restAnalysisMockMvc.perform(get("/api/analyses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAnalysis() throws Exception {
        // Initialize the database
        analysisService.save(analysis);

        int databaseSizeBeforeUpdate = analysisRepository.findAll().size();

        // Update the analysis
        Analysis updatedAnalysis = analysisRepository.findById(analysis.getId()).get();
        updatedAnalysis
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .visibility(UPDATED_VISIBILITY)
            .owner(UPDATED_OWNER)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .input(UPDATED_INPUT);

        restAnalysisMockMvc.perform(put("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnalysis)))
            .andExpect(status().isOk());

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAnalysis.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAnalysis.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testAnalysis.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testAnalysis.getCreateDate()).isNotNull();
        assertThat(testAnalysis.getUpdateDate()).isNotNull();
        assertThat(testAnalysis.getInput()).isEqualTo(UPDATED_INPUT);
    }

    @Test
    public void updateNonExistingAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().size();

        // Create the Analysis

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnalysisMockMvc.perform(put("/api/analyses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysis)))
            .andExpect(status().isBadRequest());

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteAnalysis() throws Exception {
        // Initialize the database
        analysisService.save(analysis);

        int databaseSizeBeforeDelete = analysisRepository.findAll().size();

        // Get the analysis
        restAnalysisMockMvc.perform(delete("/api/analyses/{id}", analysis.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Analysis> analysisList = analysisRepository.findAll();
        assertThat(analysisList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Analysis.class);
        Analysis analysis1 = new Analysis();
        analysis1.setId("id1");
        Analysis analysis2 = new Analysis();
        analysis2.setId(analysis1.getId());
        assertThat(analysis1).isEqualTo(analysis2);
        analysis2.setId("id2");
        assertThat(analysis1).isNotEqualTo(analysis2);
        analysis1.setId(null);
        assertThat(analysis1).isNotEqualTo(analysis2);
    }
}
