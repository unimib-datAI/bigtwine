package it.unimib.disco.bigtwine.services.analysis.web.rest;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisDefaultSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisDefaultSettingRepository;
import it.unimib.disco.bigtwine.services.analysis.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;


import static it.unimib.disco.bigtwine.services.analysis.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnalysisDefaultSettingResource REST controller.
 *
 * @see AnalysisDefaultSettingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class AnalysisDefaultSettingResourceIntTest {

    private static final String DEFAULT_DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_DEFAULT_VALUE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_USER_CAN_OVERRIDE = false;
    private static final Boolean UPDATED_USER_CAN_OVERRIDE = true;

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    @Autowired
    private AnalysisDefaultSettingRepository analysisDefaultSettingRepository;

    @Mock
    private AnalysisDefaultSettingRepository analysisDefaultSettingRepositoryMock;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restAnalysisDefaultSettingMockMvc;

    private AnalysisDefaultSetting analysisDefaultSetting;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnalysisDefaultSettingResource analysisDefaultSettingResource = new AnalysisDefaultSettingResource(analysisDefaultSettingRepository);
        this.restAnalysisDefaultSettingMockMvc = MockMvcBuilders.standaloneSetup(analysisDefaultSettingResource)
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
    public static AnalysisDefaultSetting createEntity() {
        AnalysisDefaultSetting analysisDefaultSetting = new AnalysisDefaultSetting()
            .defaultValue(DEFAULT_DEFAULT_VALUE)
            .userCanOverride(DEFAULT_USER_CAN_OVERRIDE)
            .priority(DEFAULT_PRIORITY);
        // Add required entity
        AnalysisSetting analysisSetting = AnalysisSettingResourceIntTest.createEntity();
        analysisSetting.setId("fixed-id-for-tests");
        analysisDefaultSetting.setSetting(analysisSetting);
        return analysisDefaultSetting;
    }

    @Before
    public void initTest() {
        analysisDefaultSettingRepository.deleteAll();
        analysisDefaultSetting = createEntity();
    }

    @Test
    public void createAnalysisDefaultSetting() throws Exception {
        int databaseSizeBeforeCreate = analysisDefaultSettingRepository.findAll().size();

        // Create the AnalysisDefaultSetting
        restAnalysisDefaultSettingMockMvc.perform(post("/api/analysis-default-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisDefaultSetting)))
            .andExpect(status().isCreated());

        // Validate the AnalysisDefaultSetting in the database
        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeCreate + 1);
        AnalysisDefaultSetting testAnalysisDefaultSetting = analysisDefaultSettingList.get(analysisDefaultSettingList.size() - 1);
        assertThat(testAnalysisDefaultSetting.getDefaultValue()).isEqualTo(DEFAULT_DEFAULT_VALUE);
        assertThat(testAnalysisDefaultSetting.isUserCanOverride()).isEqualTo(DEFAULT_USER_CAN_OVERRIDE);
        assertThat(testAnalysisDefaultSetting.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    public void createAnalysisDefaultSettingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = analysisDefaultSettingRepository.findAll().size();

        // Create the AnalysisDefaultSetting with an existing ID
        analysisDefaultSetting.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnalysisDefaultSettingMockMvc.perform(post("/api/analysis-default-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisDefaultSetting)))
            .andExpect(status().isBadRequest());

        // Validate the AnalysisDefaultSetting in the database
        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkDefaultValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisDefaultSettingRepository.findAll().size();
        // set the field null
        analysisDefaultSetting.setDefaultValue(null);

        // Create the AnalysisDefaultSetting, which fails.

        restAnalysisDefaultSettingMockMvc.perform(post("/api/analysis-default-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisDefaultSetting)))
            .andExpect(status().isBadRequest());

        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllAnalysisDefaultSettings() throws Exception {
        // Initialize the database
        analysisDefaultSettingRepository.save(analysisDefaultSetting);

        // Get all the analysisDefaultSettingList
        restAnalysisDefaultSettingMockMvc.perform(get("/api/analysis-default-settings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(analysisDefaultSetting.getId())))
            .andExpect(jsonPath("$.[*].defaultValue").value(hasItem(DEFAULT_DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].userCanOverride").value(hasItem(DEFAULT_USER_CAN_OVERRIDE.booleanValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllAnalysisDefaultSettingsWithEagerRelationshipsIsEnabled() throws Exception {
        AnalysisDefaultSettingResource analysisDefaultSettingResource = new AnalysisDefaultSettingResource(analysisDefaultSettingRepositoryMock);
        when(analysisDefaultSettingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restAnalysisDefaultSettingMockMvc = MockMvcBuilders.standaloneSetup(analysisDefaultSettingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAnalysisDefaultSettingMockMvc.perform(get("/api/analysis-default-settings?eagerload=true"))
        .andExpect(status().isOk());

        verify(analysisDefaultSettingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllAnalysisDefaultSettingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        AnalysisDefaultSettingResource analysisDefaultSettingResource = new AnalysisDefaultSettingResource(analysisDefaultSettingRepositoryMock);
            when(analysisDefaultSettingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restAnalysisDefaultSettingMockMvc = MockMvcBuilders.standaloneSetup(analysisDefaultSettingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAnalysisDefaultSettingMockMvc.perform(get("/api/analysis-default-settings?eagerload=true"))
        .andExpect(status().isOk());

            verify(analysisDefaultSettingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    public void getAnalysisDefaultSetting() throws Exception {
        // Initialize the database
        analysisDefaultSettingRepository.save(analysisDefaultSetting);

        // Get the analysisDefaultSetting
        restAnalysisDefaultSettingMockMvc.perform(get("/api/analysis-default-settings/{id}", analysisDefaultSetting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(analysisDefaultSetting.getId()))
            .andExpect(jsonPath("$.defaultValue").value(DEFAULT_DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.userCanOverride").value(DEFAULT_USER_CAN_OVERRIDE.booleanValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY));
    }

    @Test
    public void getNonExistingAnalysisDefaultSetting() throws Exception {
        // Get the analysisDefaultSetting
        restAnalysisDefaultSettingMockMvc.perform(get("/api/analysis-default-settings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAnalysisDefaultSetting() throws Exception {
        // Initialize the database
        analysisDefaultSettingRepository.save(analysisDefaultSetting);

        int databaseSizeBeforeUpdate = analysisDefaultSettingRepository.findAll().size();

        // Update the analysisDefaultSetting
        AnalysisDefaultSetting updatedAnalysisDefaultSetting = analysisDefaultSettingRepository.findById(analysisDefaultSetting.getId()).get();
        updatedAnalysisDefaultSetting
            .defaultValue(UPDATED_DEFAULT_VALUE)
            .userCanOverride(UPDATED_USER_CAN_OVERRIDE)
            .priority(UPDATED_PRIORITY);

        restAnalysisDefaultSettingMockMvc.perform(put("/api/analysis-default-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnalysisDefaultSetting)))
            .andExpect(status().isOk());

        // Validate the AnalysisDefaultSetting in the database
        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeUpdate);
        AnalysisDefaultSetting testAnalysisDefaultSetting = analysisDefaultSettingList.get(analysisDefaultSettingList.size() - 1);
        assertThat(testAnalysisDefaultSetting.getDefaultValue()).isEqualTo(UPDATED_DEFAULT_VALUE);
        assertThat(testAnalysisDefaultSetting.isUserCanOverride()).isEqualTo(UPDATED_USER_CAN_OVERRIDE);
        assertThat(testAnalysisDefaultSetting.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    public void updateNonExistingAnalysisDefaultSetting() throws Exception {
        int databaseSizeBeforeUpdate = analysisDefaultSettingRepository.findAll().size();

        // Create the AnalysisDefaultSetting

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnalysisDefaultSettingMockMvc.perform(put("/api/analysis-default-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(analysisDefaultSetting)))
            .andExpect(status().isBadRequest());

        // Validate the AnalysisDefaultSetting in the database
        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteAnalysisDefaultSetting() throws Exception {
        // Initialize the database
        analysisDefaultSettingRepository.save(analysisDefaultSetting);

        int databaseSizeBeforeDelete = analysisDefaultSettingRepository.findAll().size();

        // Get the analysisDefaultSetting
        restAnalysisDefaultSettingMockMvc.perform(delete("/api/analysis-default-settings/{id}", analysisDefaultSetting.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AnalysisDefaultSetting> analysisDefaultSettingList = analysisDefaultSettingRepository.findAll();
        assertThat(analysisDefaultSettingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnalysisDefaultSetting.class);
        AnalysisDefaultSetting analysisDefaultSetting1 = new AnalysisDefaultSetting();
        analysisDefaultSetting1.setId("id1");
        AnalysisDefaultSetting analysisDefaultSetting2 = new AnalysisDefaultSetting();
        analysisDefaultSetting2.setId(analysisDefaultSetting1.getId());
        assertThat(analysisDefaultSetting1).isEqualTo(analysisDefaultSetting2);
        analysisDefaultSetting2.setId("id2");
        assertThat(analysisDefaultSetting1).isNotEqualTo(analysisDefaultSetting2);
        analysisDefaultSetting1.setId(null);
        assertThat(analysisDefaultSetting1).isNotEqualTo(analysisDefaultSetting2);
    }
}
