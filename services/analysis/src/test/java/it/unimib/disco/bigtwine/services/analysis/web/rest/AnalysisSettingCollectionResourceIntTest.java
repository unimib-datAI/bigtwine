package it.unimib.disco.bigtwine.services.analysis.web.rest;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingCollection;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisSettingCollectionRepository;
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

import java.util.List;


import static it.unimib.disco.bigtwine.services.analysis.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnalysisSettingCollectionResource REST controller.
 *
 * @see AnalysisSettingCollectionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class AnalysisSettingCollectionResourceIntTest {

    @Autowired
    private AnalysisSettingCollectionRepository analysisSettingCollectionRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restAnalysisSettingCollectionMockMvc;

    private AnalysisSettingCollection analysisSettingCollection;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnalysisSettingCollectionResource analysisSettingCollectionResource = new AnalysisSettingCollectionResource(analysisSettingCollectionRepository);
        this.restAnalysisSettingCollectionMockMvc = MockMvcBuilders.standaloneSetup(analysisSettingCollectionResource)
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
    public static AnalysisSettingCollection createEntity() {
        AnalysisSettingCollection analysisSettingCollection = new AnalysisSettingCollection();
        // Add required entity
        AnalysisSetting analysisSetting = AnalysisSettingResourceIntTest.createEntity();
        analysisSetting.setId("fixed-id-for-tests");
        analysisSettingCollection.getSettings().add(analysisSetting);
        // Add required entity
        AnalysisType analysisType = AnalysisType.TWITTER_NEEL;
        analysisSettingCollection.setAnalysisType(analysisType);
        // Add required entity
        AnalysisInputType analysisInputType = AnalysisInputType.QUERY;
        analysisSettingCollection.setAnalysisInputType(analysisInputType);
        return analysisSettingCollection;
    }

    @Before
    public void initTest() {
        analysisSettingCollectionRepository.deleteAll();
        analysisSettingCollection = createEntity();
    }

    @Test
    public void createAnalysisSettingCollection() throws Exception {
        int databaseSizeBeforeCreate = analysisSettingCollectionRepository.findAll().size();

        // Create the AnalysisSettingCollection
        restAnalysisSettingCollectionMockMvc.perform(post("/api/analysis-setting-collections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisSettingCollection)))
            .andExpect(status().isCreated());

        // Validate the AnalysisSettingCollection in the database
        List<AnalysisSettingCollection> analysisSettingCollectionList = analysisSettingCollectionRepository.findAll();
        assertThat(analysisSettingCollectionList).hasSize(databaseSizeBeforeCreate + 1);
        AnalysisSettingCollection testAnalysisSettingCollection = analysisSettingCollectionList.get(analysisSettingCollectionList.size() - 1);
    }

    @Test
    public void createAnalysisSettingCollectionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = analysisSettingCollectionRepository.findAll().size();

        // Create the AnalysisSettingCollection with an existing ID
        analysisSettingCollection.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnalysisSettingCollectionMockMvc.perform(post("/api/analysis-setting-collections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisSettingCollection)))
            .andExpect(status().isBadRequest());

        // Validate the AnalysisSettingCollection in the database
        List<AnalysisSettingCollection> analysisSettingCollectionList = analysisSettingCollectionRepository.findAll();
        assertThat(analysisSettingCollectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllAnalysisSettingCollections() throws Exception {
        // Initialize the database
        analysisSettingCollectionRepository.save(analysisSettingCollection);

        // Get all the analysisSettingCollectionList
        restAnalysisSettingCollectionMockMvc.perform(get("/api/analysis-setting-collections?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(analysisSettingCollection.getId())));
    }

    @Test
    public void getAnalysisSettingCollection() throws Exception {
        // Initialize the database
        analysisSettingCollectionRepository.save(analysisSettingCollection);

        // Get the analysisSettingCollection
        restAnalysisSettingCollectionMockMvc.perform(get("/api/analysis-setting-collections/{id}", analysisSettingCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(analysisSettingCollection.getId()));
    }

    @Test
    public void getNonExistingAnalysisSettingCollection() throws Exception {
        // Get the analysisSettingCollection
        restAnalysisSettingCollectionMockMvc.perform(get("/api/analysis-setting-collections/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAnalysisSettingCollection() throws Exception {
        // Initialize the database
        analysisSettingCollectionRepository.save(analysisSettingCollection);

        int databaseSizeBeforeUpdate = analysisSettingCollectionRepository.findAll().size();

        // Update the analysisSettingCollection
        AnalysisSettingCollection updatedAnalysisSettingCollection = analysisSettingCollectionRepository.findById(analysisSettingCollection.getId()).get();

        restAnalysisSettingCollectionMockMvc.perform(put("/api/analysis-setting-collections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnalysisSettingCollection)))
            .andExpect(status().isOk());

        // Validate the AnalysisSettingCollection in the database
        List<AnalysisSettingCollection> analysisSettingCollectionList = analysisSettingCollectionRepository.findAll();
        assertThat(analysisSettingCollectionList).hasSize(databaseSizeBeforeUpdate);
        AnalysisSettingCollection testAnalysisSettingCollection = analysisSettingCollectionList.get(analysisSettingCollectionList.size() - 1);
    }

    @Test
    public void updateNonExistingAnalysisSettingCollection() throws Exception {
        int databaseSizeBeforeUpdate = analysisSettingCollectionRepository.findAll().size();

        // Create the AnalysisSettingCollection

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnalysisSettingCollectionMockMvc.perform(put("/api/analysis-setting-collections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisSettingCollection)))
            .andExpect(status().isBadRequest());

        // Validate the AnalysisSettingCollection in the database
        List<AnalysisSettingCollection> analysisSettingCollectionList = analysisSettingCollectionRepository.findAll();
        assertThat(analysisSettingCollectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteAnalysisSettingCollection() throws Exception {
        // Initialize the database
        analysisSettingCollectionRepository.save(analysisSettingCollection);

        int databaseSizeBeforeDelete = analysisSettingCollectionRepository.findAll().size();

        // Get the analysisSettingCollection
        restAnalysisSettingCollectionMockMvc.perform(delete("/api/analysis-setting-collections/{id}", analysisSettingCollection.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AnalysisSettingCollection> analysisSettingCollectionList = analysisSettingCollectionRepository.findAll();
        assertThat(analysisSettingCollectionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnalysisSettingCollection.class);
        AnalysisSettingCollection analysisSettingCollection1 = new AnalysisSettingCollection();
        analysisSettingCollection1.setId("id1");
        AnalysisSettingCollection analysisSettingCollection2 = new AnalysisSettingCollection();
        analysisSettingCollection2.setId(analysisSettingCollection1.getId());
        assertThat(analysisSettingCollection1).isEqualTo(analysisSettingCollection2);
        analysisSettingCollection2.setId("id2");
        assertThat(analysisSettingCollection1).isNotEqualTo(analysisSettingCollection2);
        analysisSettingCollection1.setId(null);
        assertThat(analysisSettingCollection1).isNotEqualTo(analysisSettingCollection2);
    }
}
